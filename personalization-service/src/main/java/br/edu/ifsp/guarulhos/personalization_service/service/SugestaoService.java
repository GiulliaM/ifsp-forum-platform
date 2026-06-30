package br.edu.ifsp.guarulhos.personalization_service.service;

import br.edu.ifsp.guarulhos.personalization_service.dto.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SugestaoService {

    private static final Logger log = LoggerFactory.getLogger(SugestaoService.class);
    private static final int LIMITE_SUGESTOES = 5;

    private static final Map<String, String> NIVEL_PARA_DIFICULDADE = Map.of(
            "INICIANTE", "FACIL",
            "INTERMEDIARIO", "MEDIO",
            "AVANCADO", "DIFICIL"
    );

    private final RestClient restClient;

    @Value("${services.auth-url}")
    private String authUrl;

    @Value("${services.forum-url}")
    private String forumUrl;

    @Value("${services.algorithm-url}")
    private String algorithmUrl;

    public SugestaoResponse sugerir(Long usuarioId) {
        PreferenciaResponse preferencias = buscarPreferencias(usuarioId);
        List<TopicoSugestaoResponse> topicos = buscarTopicos(preferencias);
        List<ExercicioSugestaoResponse> exercicios = buscarExercicios(preferencias);
        return new SugestaoResponse(topicos, exercicios);
    }

    private PreferenciaResponse buscarPreferencias(Long usuarioId) {
        try {
            return restClient.get()
                    .uri(authUrl + "/api/auth/usuarios/preferencias")
                    .header("X-User-Id", String.valueOf(usuarioId))
                    .retrieve()
                    .body(PreferenciaResponse.class);
        } catch (Exception e) {
            log.warn("Falha ao buscar preferencias do usuario {}: {}", usuarioId, e.getMessage());
            return new PreferenciaResponse();
        }
    }

    private List<TopicoSugestaoResponse> buscarTopicos(PreferenciaResponse preferencias) {
        try {
            List<TopicoSugestaoResponse> todos = restClient.get()
                    .uri(forumUrl + "/api/topicos")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (todos == null) return List.of();

            List<String> interesses = preferencias.getInteresses();
            if (interesses == null || interesses.isEmpty()) {
                return todos.stream().limit(LIMITE_SUGESTOES).toList();
            }

            List<TopicoSugestaoResponse> filtrados = todos.stream()
                    .filter(t -> interesses.stream()
                            .anyMatch(i -> t.getCategoria() != null &&
                                    t.getCategoria().toLowerCase().contains(i.toLowerCase())))
                    .limit(LIMITE_SUGESTOES)
                    .toList();

            return filtrados.isEmpty()
                    ? todos.stream().limit(LIMITE_SUGESTOES).toList()
                    : filtrados;
        } catch (Exception e) {
            log.warn("Falha ao buscar topicos: {}", e.getMessage());
            return List.of();
        }
    }

    private List<ExercicioSugestaoResponse> buscarExercicios(PreferenciaResponse preferencias) {
        try {
            String dificuldade = preferencias.getNivel() != null
                    ? NIVEL_PARA_DIFICULDADE.getOrDefault(preferencias.getNivel(), null)
                    : null;

            String uri = algorithmUrl + "/api/exercicios";
            if (dificuldade != null) {
                uri += "?dificuldade=" + dificuldade;
            }

            List<ExercicioSugestaoResponse> todos = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (todos == null) return List.of();
            return todos.stream().limit(LIMITE_SUGESTOES).toList();
        } catch (Exception e) {
            log.warn("Falha ao buscar exercicios: {}", e.getMessage());
            return List.of();
        }
    }
}
