package br.edu.ifsp.guarulhos.algorithm_service.service;

import br.edu.ifsp.guarulhos.algorithm_service.dto.request.SubmissaoRequest;
import br.edu.ifsp.guarulhos.algorithm_service.dto.response.SubmissaoResponse;
import br.edu.ifsp.guarulhos.algorithm_service.exception.AcessoNegadoException;
import br.edu.ifsp.guarulhos.algorithm_service.exception.RegraNegocioException;
import br.edu.ifsp.guarulhos.algorithm_service.model.CasoTeste;
import br.edu.ifsp.guarulhos.algorithm_service.model.Exercicio;
import br.edu.ifsp.guarulhos.algorithm_service.model.ResultadoCasoTeste;
import br.edu.ifsp.guarulhos.algorithm_service.model.Submissao;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Dificuldade;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Linguagem;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.StatusExercicio;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Veredito;
import br.edu.ifsp.guarulhos.algorithm_service.repository.CasoTesteRepository;
import br.edu.ifsp.guarulhos.algorithm_service.repository.ExercicioRepository;
import br.edu.ifsp.guarulhos.algorithm_service.repository.ResultadoCasoTesteRepository;
import br.edu.ifsp.guarulhos.algorithm_service.repository.SubmissaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do juiz por comparação: vereditos de aceite e resposta errada,
 * normalização de saídas e regras de feedback restrito ao dono da submissão.
 */
@ExtendWith(MockitoExtension.class)
class SubmissaoServiceTest {

    @Mock
    private SubmissaoRepository submissaoRepository;
    @Mock
    private ResultadoCasoTesteRepository resultadoRepository;
    @Mock
    private ExercicioRepository exercicioRepository;
    @Mock
    private CasoTesteRepository casoTesteRepository;
    @InjectMocks
    private SubmissaoService submissaoService;

    private Exercicio exercicioPublicado() {
        return Exercicio.builder()
                .id(1L).titulo("Soma").enunciado("...").dificuldade(Dificuldade.FACIL)
                .categoria("Matemática").status(StatusExercicio.PUBLICADO).autorId(9L).build();
    }

    private SubmissaoRequest request(List<String> saidas) {
        SubmissaoRequest request = new SubmissaoRequest();
        request.setExercicioId(1L);
        request.setLinguagem(Linguagem.JAVA);
        request.setCodigo("public class Main {}");
        request.setSaidas(saidas);
        return request;
    }

    @Test
    void submeter_quandoTodasSaidasCorretas_retornaAceito() {
        when(exercicioRepository.findById(1L)).thenReturn(Optional.of(exercicioPublicado()));
        CasoTeste c1 = CasoTeste.builder().id(10L).exercicioId(1L).entrada("2 3").saidaEsperada("5").build();
        CasoTeste c2 = CasoTeste.builder().id(11L).exercicioId(1L).entrada("4 4").saidaEsperada("8").build();
        when(casoTesteRepository.findByExercicioIdOrderByIdAsc(1L)).thenReturn(List.of(c1, c2));
        when(submissaoRepository.save(any(Submissao.class))).thenAnswer(inv -> {
            Submissao s = inv.getArgument(0);
            s.setId(100L);
            return s;
        });

        SubmissaoResponse response = submissaoService.submeter(request(List.of("5", "8")), 7L);

        assertThat(response.getVeredito()).isEqualTo(Veredito.ACEITO);
        assertThat(response.getCasosPassados()).isEqualTo(2);
        assertThat(response.getTotalCasos()).isEqualTo(2);
        verify(resultadoRepository, times(2)).save(any(ResultadoCasoTeste.class));
    }

    @Test
    void submeter_quandoAlgumaSaidaErrada_retornaRespostaErrada() {
        when(exercicioRepository.findById(1L)).thenReturn(Optional.of(exercicioPublicado()));
        CasoTeste c1 = CasoTeste.builder().id(10L).exercicioId(1L).entrada("2 3").saidaEsperada("5").build();
        CasoTeste c2 = CasoTeste.builder().id(11L).exercicioId(1L).entrada("4 4").saidaEsperada("8").build();
        when(casoTesteRepository.findByExercicioIdOrderByIdAsc(1L)).thenReturn(List.of(c1, c2));
        when(submissaoRepository.save(any(Submissao.class))).thenAnswer(inv -> {
            Submissao s = inv.getArgument(0);
            s.setId(100L);
            return s;
        });

        SubmissaoResponse response = submissaoService.submeter(request(List.of("5", "9")), 7L);

        assertThat(response.getVeredito()).isEqualTo(Veredito.RESPOSTA_ERRADA);
        assertThat(response.getCasosPassados()).isEqualTo(1);
    }

    @Test
    void submeter_ignoraEspacosEQuebrasDeLinhaNaComparacao() {
        when(exercicioRepository.findById(1L)).thenReturn(Optional.of(exercicioPublicado()));
        CasoTeste c1 = CasoTeste.builder().id(10L).exercicioId(1L).entrada("x").saidaEsperada("5").build();
        when(casoTesteRepository.findByExercicioIdOrderByIdAsc(1L)).thenReturn(List.of(c1));
        when(submissaoRepository.save(any(Submissao.class))).thenAnswer(inv -> {
            Submissao s = inv.getArgument(0);
            s.setId(100L);
            return s;
        });

        SubmissaoResponse response = submissaoService.submeter(request(List.of("  5 \n")), 7L);

        assertThat(response.getVeredito()).isEqualTo(Veredito.ACEITO);
    }

    @Test
    void submeter_quandoExercicioEhRascunho_lancaRegraNegocio() {
        Exercicio rascunho = exercicioPublicado();
        rascunho.setStatus(StatusExercicio.RASCUNHO);
        when(exercicioRepository.findById(1L)).thenReturn(Optional.of(rascunho));

        assertThatThrownBy(() -> submissaoService.submeter(request(List.of("5")), 7L))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void feedback_quandoNaoEhDono_lancaAcessoNegado() {
        Submissao submissao = Submissao.builder().id(100L).usuarioId(7L).exercicioId(1L)
                .linguagem(Linguagem.JAVA).codigo("x").veredito(Veredito.ACEITO).build();
        when(submissaoRepository.findById(100L)).thenReturn(Optional.of(submissao));

        assertThatThrownBy(() -> submissaoService.feedback(100L, 8L))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void feedback_quandoEhDono_retornaResultadosDetalhados() {
        Submissao submissao = Submissao.builder().id(100L).usuarioId(7L).exercicioId(1L)
                .linguagem(Linguagem.JAVA).codigo("x").veredito(Veredito.RESPOSTA_ERRADA).build();
        when(submissaoRepository.findById(100L)).thenReturn(Optional.of(submissao));
        ResultadoCasoTeste r1 = ResultadoCasoTeste.builder().id(1L).submissaoId(100L).casoTesteId(10L)
                .passou(true).entrada("2 3").saidaEsperada("5").saidaObtida("5").build();
        ResultadoCasoTeste r2 = ResultadoCasoTeste.builder().id(2L).submissaoId(100L).casoTesteId(11L)
                .passou(false).entrada("4 4").saidaEsperada("8").saidaObtida("9").build();
        when(resultadoRepository.findBySubmissaoIdOrderByIdAsc(100L)).thenReturn(List.of(r1, r2));

        SubmissaoResponse response = submissaoService.feedback(100L, 7L);

        assertThat(response.getResultados()).hasSize(2);
        assertThat(response.getResultados().get(0).getNumero()).isEqualTo(1);
        assertThat(response.getResultados().get(1).isPassou()).isFalse();
        assertThat(response.getCasosPassados()).isEqualTo(1);
    }
}
