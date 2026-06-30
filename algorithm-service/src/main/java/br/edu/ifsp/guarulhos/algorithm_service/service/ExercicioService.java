package br.edu.ifsp.guarulhos.algorithm_service.service;

import br.edu.ifsp.guarulhos.algorithm_service.dto.request.CasoTesteRequest;
import br.edu.ifsp.guarulhos.algorithm_service.dto.request.ExercicioRequest;
import br.edu.ifsp.guarulhos.algorithm_service.dto.response.ExercicioDetalheResponse;
import br.edu.ifsp.guarulhos.algorithm_service.dto.response.ExercicioResumoResponse;
import br.edu.ifsp.guarulhos.algorithm_service.dto.response.PainelPedagogicoItemResponse;
import br.edu.ifsp.guarulhos.algorithm_service.exception.AcessoNegadoException;
import br.edu.ifsp.guarulhos.algorithm_service.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.guarulhos.algorithm_service.model.CasoTeste;
import br.edu.ifsp.guarulhos.algorithm_service.model.Exercicio;
import br.edu.ifsp.guarulhos.algorithm_service.model.Submissao;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Dificuldade;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.StatusExercicio;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.StatusPessoal;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Veredito;
import br.edu.ifsp.guarulhos.algorithm_service.repository.CasoTesteRepository;
import br.edu.ifsp.guarulhos.algorithm_service.repository.ExercicioRepository;
import br.edu.ifsp.guarulhos.algorithm_service.repository.SubmissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Regras de negócio do catálogo de exercícios: cadastro pelo moderador (US-10) e
 * listagem/detalhe com taxa de acerto e status pessoal do usuário (US-07).
 */
@Service
@RequiredArgsConstructor
public class ExercicioService {

    private final ExercicioRepository exercicioRepository;
    private final CasoTesteRepository casoTesteRepository;
    private final SubmissaoRepository submissaoRepository;

    public ExercicioDetalheResponse criar(ExercicioRequest request, String perfil, Long autorId){
        validarModerador(perfil);

        StatusExercicio status = request.isPublicar()
                ? StatusExercicio.PUBLICADO
                : StatusExercicio.RASCUNHO;

        Exercicio exercicio = Exercicio.builder()
                .titulo(request.getTitulo())
                .enunciado(request.getEnunciado())
                .dificuldade(request.getDificuldade())
                .categoria(request.getCategoria())
                .restricoes(request.getRestricoes())
                .exemploEntrada(request.getExemploEntrada())
                .exemploSaida(request.getExemploSaida())
                .status(status)
                .autorId(autorId)
                .build();
        exercicioRepository.save(exercicio);

        for (CasoTesteRequest casoRequest : request.getCasosTeste()){
            CasoTeste caso = CasoTeste.builder()
                    .exercicioId(exercicio.getId())
                    .entrada(casoRequest.getEntrada())
                    .saidaEsperada(casoRequest.getSaidaEsperada())
                    .oculto(casoRequest.isOculto())
                    .build();
            casoTesteRepository.save(caso);
        }

        return montarDetalhe(exercicio, null);
    }

    public List<ExercicioResumoResponse> listar(Dificuldade dificuldade, String categoria, Long usuarioId){
        List<Exercicio> exercicios;
        if (dificuldade != null){
            exercicios = exercicioRepository.findByStatusAndDificuldade(StatusExercicio.PUBLICADO, dificuldade);
        } else if (categoria != null && !categoria.isBlank()){
            exercicios = exercicioRepository.findByStatusAndCategoria(StatusExercicio.PUBLICADO, categoria);
        } else {
            exercicios = exercicioRepository.findByStatus(StatusExercicio.PUBLICADO);
        }

        return exercicios.stream()
                .map(exercicio -> ExercicioResumoResponse.builder()
                        .id(exercicio.getId())
                        .titulo(exercicio.getTitulo())
                        .dificuldade(exercicio.getDificuldade())
                        .categoria(exercicio.getCategoria())
                        .taxaAcerto(calcularTaxaAcerto(exercicio.getId()))
                        .statusPessoal(calcularStatusPessoal(exercicio.getId(), usuarioId))
                        .build())
                .toList();
    }

    public ExercicioDetalheResponse buscarPorId(Long id, Long usuarioId){
        Exercicio exercicio = exercicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exercício não encontrado"));
        return montarDetalhe(exercicio, usuarioId);
    }

    private ExercicioDetalheResponse montarDetalhe(Exercicio exercicio, Long usuarioId){
        long totalCasos = casoTesteRepository.findByExercicioIdOrderByIdAsc(exercicio.getId()).size();
        return ExercicioDetalheResponse.builder()
                .id(exercicio.getId())
                .titulo(exercicio.getTitulo())
                .enunciado(exercicio.getEnunciado())
                .dificuldade(exercicio.getDificuldade())
                .categoria(exercicio.getCategoria())
                .restricoes(exercicio.getRestricoes())
                .exemploEntrada(exercicio.getExemploEntrada())
                .exemploSaida(exercicio.getExemploSaida())
                .taxaAcerto(calcularTaxaAcerto(exercicio.getId()))
                .totalCasosTeste(totalCasos)
                .statusPessoal(calcularStatusPessoal(exercicio.getId(), usuarioId))
                .build();
    }

    private double calcularTaxaAcerto(Long exercicioId){
        long total = submissaoRepository.countByExercicioId(exercicioId);
        if (total == 0) return 0.0;
        long aceitas = submissaoRepository.countByExercicioIdAndVeredito(exercicioId, Veredito.ACEITO);
        return Math.round((aceitas * 10000.0) / total) / 100.0;
    }

    private StatusPessoal calcularStatusPessoal(Long exercicioId, Long usuarioId){
        if (usuarioId == null) return null;
        List<Submissao> submissoes = submissaoRepository.findByExercicioIdAndUsuarioId(exercicioId, usuarioId);
        if (submissoes.isEmpty()) return StatusPessoal.NAO_TENTADO;
        boolean resolvido = submissoes.stream().anyMatch(s -> s.getVeredito() == Veredito.ACEITO);
        return resolvido ? StatusPessoal.RESOLVIDO : StatusPessoal.TENTADO;
    }

    public List<PainelPedagogicoItemResponse> listarPainelPedagogico(int limite, String perfil) {
        validarModerador(perfil);
        return exercicioRepository.findByStatus(StatusExercicio.PUBLICADO).stream()
                .map(e -> {
                    long total = submissaoRepository.countByExercicioId(e.getId());
                    double taxaAcerto = calcularTaxaAcerto(e.getId());
                    double taxaErro = total == 0 ? 0.0 : Math.round((100.0 - taxaAcerto) * 100) / 100.0;
                    return PainelPedagogicoItemResponse.builder()
                            .id(e.getId())
                            .titulo(e.getTitulo())
                            .dificuldade(e.getDificuldade())
                            .categoria(e.getCategoria())
                            .taxaAcerto(taxaAcerto)
                            .taxaErro(taxaErro)
                            .totalSubmissoes(total)
                            .build();
                })
                .filter(item -> item.getTotalSubmissoes() > 0)
                .sorted((a, b) -> Double.compare(b.getTaxaErro(), a.getTaxaErro()))
                .limit(limite)
                .toList();
    }

    private void validarModerador(String perfil){
        if (!"MODERADOR".equals(perfil)){
            throw new AcessoNegadoException("Apenas moderadores podem cadastrar exercícios");
        }
    }
}
