package br.edu.ifsp.guarulhos.forum_service.service;

import br.edu.ifsp.guarulhos.forum_service.dto.response.MetricaCategoriaResponse;
import br.edu.ifsp.guarulhos.forum_service.exception.RegraNegocioException;
import br.edu.ifsp.guarulhos.forum_service.repository.TopicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricaService {

    private final TopicoRepository topicoRepository;

    public List<MetricaCategoriaResponse> categoriasSemResposta(int dias) {
        if (dias != 7 && dias != 30) {
            throw new RegraNegocioException("Período inválido. Informe 7 ou 30 dias.");
        }
        LocalDateTime desde = LocalDateTime.now().minusDays(dias);
        return topicoRepository.findCategoriasSemResposta(desde).stream()
                .map(row -> new MetricaCategoriaResponse(
                        (String) row[0],
                        ((Number) row[1]).longValue(),
                        dias
                ))
                .toList();
    }
}
