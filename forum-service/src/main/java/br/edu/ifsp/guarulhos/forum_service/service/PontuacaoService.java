package br.edu.ifsp.guarulhos.forum_service.service;

import br.edu.ifsp.guarulhos.forum_service.dto.request.EventoPontosRequest;
import br.edu.ifsp.guarulhos.forum_service.model.enums.TipoEventoPontos;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Notifica o gamification-service sobre ações pontuáveis do fórum. O cálculo do
 * valor em pontos e a idempotência são responsabilidade do gamification-service.
 */
@Service
@RequiredArgsConstructor
public class PontuacaoService {

    private static final Logger log = LoggerFactory.getLogger(PontuacaoService.class);

    private final RestClient restClient;

    @Value("${services.gamification-url}")
    private String gamificationUrl;

    public void registrarTopico(Long autorId, Long topicoId) {
        registrarEvento(TipoEventoPontos.TOPICO_CRIADO, autorId, topicoId);
    }

    public void registrarComentario(Long autorId, Long comentarioId) {
        registrarEvento(TipoEventoPontos.COMENTARIO, autorId, comentarioId);
    }

    public void registrarLike(Long autorConteudo, Long referenciaId, Long curtidorId) {
        registrarEvento(TipoEventoPontos.LIKE_RECEBIDO, autorConteudo, referenciaId);
    }

    private void registrarEvento(TipoEventoPontos tipo, Long usuarioId, Long referenciaId) {
        try {
            restClient.post()
                    .uri(gamificationUrl + "/api/pontos/eventos")
                    .body(new EventoPontosRequest(tipo, usuarioId, referenciaId))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Falha ao registrar evento de pontuacao ({}, usuarioId={}, referenciaId={}): {}",
                    tipo, usuarioId, referenciaId, e.getMessage());
        }
    }
}
