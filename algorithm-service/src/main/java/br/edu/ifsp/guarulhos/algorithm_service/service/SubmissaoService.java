package br.edu.ifsp.guarulhos.algorithm_service.service;

import br.edu.ifsp.guarulhos.algorithm_service.dto.request.SubmissaoRequest;
import br.edu.ifsp.guarulhos.algorithm_service.dto.response.ResultadoCasoTesteResponse;
import br.edu.ifsp.guarulhos.algorithm_service.dto.response.SubmissaoResponse;
import br.edu.ifsp.guarulhos.algorithm_service.exception.AcessoNegadoException;
import br.edu.ifsp.guarulhos.algorithm_service.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.guarulhos.algorithm_service.exception.RegraNegocioException;
import br.edu.ifsp.guarulhos.algorithm_service.model.CasoTeste;
import br.edu.ifsp.guarulhos.algorithm_service.model.Exercicio;
import br.edu.ifsp.guarulhos.algorithm_service.model.ResultadoCasoTeste;
import br.edu.ifsp.guarulhos.algorithm_service.model.Submissao;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.StatusExercicio;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Veredito;
import br.edu.ifsp.guarulhos.algorithm_service.repository.CasoTesteRepository;
import br.edu.ifsp.guarulhos.algorithm_service.repository.ExercicioRepository;
import br.edu.ifsp.guarulhos.algorithm_service.repository.ResultadoCasoTesteRepository;
import br.edu.ifsp.guarulhos.algorithm_service.repository.SubmissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Regras de negócio das submissões. Implementa o juiz por comparação, que confronta as
 * saídas enviadas com as esperadas de cada caso (US-08), mantém o histórico (US-08 CA5)
 * e fornece o feedback detalhado por caso de teste ao dono da submissão (US-09).
 */
@Service
@RequiredArgsConstructor
public class SubmissaoService {

    private final SubmissaoRepository submissaoRepository;
    private final ResultadoCasoTesteRepository resultadoRepository;
    private final ExercicioRepository exercicioRepository;
    private final CasoTesteRepository casoTesteRepository;

    public SubmissaoResponse submeter(SubmissaoRequest request, Long usuarioId){
        Exercicio exercicio = exercicioRepository.findById(request.getExercicioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exercício não encontrado"));

        if (exercicio.getStatus() != StatusExercicio.PUBLICADO){
            throw new RegraNegocioException("Exercício não está disponível para submissão");
        }

        List<CasoTeste> casos = casoTesteRepository.findByExercicioIdOrderByIdAsc(exercicio.getId());
        if (casos.isEmpty()){
            throw new RegraNegocioException("Exercício não possui casos de teste cadastrados");
        }

        List<String> saidas = request.getSaidas() != null ? request.getSaidas() : List.of();

        List<ResultadoCasoTeste> resultados = new ArrayList<>();
        int passados = 0;
        for (int i = 0; i < casos.size(); i++){
            CasoTeste caso = casos.get(i);
            String saidaObtida = i < saidas.size() ? saidas.get(i) : "";
            boolean passou = normalizar(caso.getSaidaEsperada()).equals(normalizar(saidaObtida));
            if (passou) passados++;

            resultados.add(ResultadoCasoTeste.builder()
                    .casoTesteId(caso.getId())
                    .passou(passou)
                    .entrada(caso.getEntrada())
                    .saidaEsperada(caso.getSaidaEsperada())
                    .saidaObtida(saidaObtida)
                    .build());
        }

        Veredito veredito = (passados == casos.size()) ? Veredito.ACEITO : Veredito.RESPOSTA_ERRADA;

        Submissao submissao = Submissao.builder()
                .exercicioId(exercicio.getId())
                .usuarioId(usuarioId)
                .linguagem(request.getLinguagem())
                .codigo(request.getCodigo())
                .veredito(veredito)
                .tempoExecucaoMs(estimarTempoMs(request.getCodigo()))
                .memoriaKb(estimarMemoriaKb(request.getCodigo()))
                .build();
        submissaoRepository.save(submissao);

        for (ResultadoCasoTeste resultado : resultados){
            resultado.setSubmissaoId(submissao.getId());
            resultadoRepository.save(resultado);
        }

        return montarResponse(submissao, passados, casos.size(), null);
    }

    public List<SubmissaoResponse> historico(Long usuarioId){
        return submissaoRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId).stream()
                .map(submissao -> {
                    List<ResultadoCasoTeste> resultados =
                            resultadoRepository.findBySubmissaoIdOrderByIdAsc(submissao.getId());
                    int passados = (int) resultados.stream().filter(ResultadoCasoTeste::isPassou).count();
                    return montarResponse(submissao, passados, resultados.size(), null);
                })
                .toList();
    }

    public SubmissaoResponse feedback(Long submissaoId, Long usuarioId){
        Submissao submissao = submissaoRepository.findById(submissaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Submissão não encontrada"));

        if (!submissao.getUsuarioId().equals(usuarioId)){
            throw new AcessoNegadoException("Você só pode ver o feedback das suas próprias submissões");
        }

        List<ResultadoCasoTeste> resultados =
                resultadoRepository.findBySubmissaoIdOrderByIdAsc(submissao.getId());
        int passados = (int) resultados.stream().filter(ResultadoCasoTeste::isPassou).count();

        List<ResultadoCasoTesteResponse> detalhes = new ArrayList<>();
        for (int i = 0; i < resultados.size(); i++){
            ResultadoCasoTeste resultado = resultados.get(i);
            detalhes.add(ResultadoCasoTesteResponse.builder()
                    .numero(i + 1)
                    .passou(resultado.isPassou())
                    .entrada(resultado.getEntrada())
                    .saidaEsperada(resultado.getSaidaEsperada())
                    .saidaObtida(resultado.getSaidaObtida())
                    .build());
        }

        return montarResponse(submissao, passados, resultados.size(), detalhes);
    }

    private SubmissaoResponse montarResponse(Submissao submissao, int passados, int total,
                                             List<ResultadoCasoTesteResponse> detalhes){
        return SubmissaoResponse.builder()
                .id(submissao.getId())
                .exercicioId(submissao.getExercicioId())
                .usuarioId(submissao.getUsuarioId())
                .linguagem(submissao.getLinguagem())
                .veredito(submissao.getVeredito())
                .tempoExecucaoMs(submissao.getTempoExecucaoMs())
                .memoriaKb(submissao.getMemoriaKb())
                .casosPassados(passados)
                .totalCasos(total)
                .criadoEm(submissao.getCriadoEm())
                .resultados(detalhes)
                .build();
    }

    private String normalizar(String texto){
        if (texto == null) return "";
        String[] linhas = texto.replace("\r\n", "\n").replace("\r", "\n").split("\n", -1);
        StringBuilder sb = new StringBuilder();
        for (String linha : linhas){
            if (sb.length() > 0) sb.append("\n");
            sb.append(linha.stripTrailing());
        }
        return sb.toString().strip();
    }

    private long estimarTempoMs(String codigo){
        return Math.min(50 + codigo.length() / 10L, 9000);
    }

    private long estimarMemoriaKb(String codigo){
        return 1024 + codigo.length();
    }
}
