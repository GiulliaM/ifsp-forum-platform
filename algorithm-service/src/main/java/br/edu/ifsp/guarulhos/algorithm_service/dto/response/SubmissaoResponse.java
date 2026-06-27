package br.edu.ifsp.guarulhos.algorithm_service.dto.response;

import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Linguagem;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Veredito;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Resposta de uma submissão. Sem a lista de resultados serve ao histórico (US-08 CA5);
 * com a lista preenchida serve ao feedback detalhado (US-09).
 */
@Data
@Builder
@AllArgsConstructor
public class SubmissaoResponse {

    private Long id;
    private Long exercicioId;
    private Long usuarioId;
    private Linguagem linguagem;
    private Veredito veredito;
    private long tempoExecucaoMs;
    private long memoriaKb;
    private int casosPassados;
    private int totalCasos;
    private LocalDateTime criadoEm;

    private List<ResultadoCasoTesteResponse> resultados;
}
