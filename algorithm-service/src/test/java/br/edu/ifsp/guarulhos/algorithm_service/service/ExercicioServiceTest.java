package br.edu.ifsp.guarulhos.algorithm_service.service;

import br.edu.ifsp.guarulhos.algorithm_service.dto.request.CasoTesteRequest;
import br.edu.ifsp.guarulhos.algorithm_service.dto.request.ExercicioRequest;
import br.edu.ifsp.guarulhos.algorithm_service.dto.response.ExercicioDetalheResponse;
import br.edu.ifsp.guarulhos.algorithm_service.dto.response.ExercicioResumoResponse;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExercicioServiceTest {

    @Mock
    private ExercicioRepository exercicioRepository;
    @Mock
    private CasoTesteRepository casoTesteRepository;
    @Mock
    private SubmissaoRepository submissaoRepository;
    @InjectMocks
    private ExercicioService exercicioService;

    private ExercicioRequest requestValido() {
        CasoTesteRequest caso = new CasoTesteRequest();
        caso.setEntrada("2 3");
        caso.setSaidaEsperada("5");
        ExercicioRequest request = new ExercicioRequest();
        request.setTitulo("Soma de dois números");
        request.setEnunciado("Leia dois inteiros e imprima a soma deles.");
        request.setDificuldade(Dificuldade.FACIL);
        request.setCategoria("Matemática");
        request.setCasosTeste(List.of(caso));
        request.setPublicar(true);
        return request;
    }

    @Test
    void criar_quandoNaoEhModerador_lancaAcessoNegado() {
        assertThatThrownBy(() -> exercicioService.criar(requestValido(), "ESTUDANTE", 1L))
                .isInstanceOf(AcessoNegadoException.class);
        verify(exercicioRepository, never()).save(any());
    }

    @Test
    void criar_quandoModerador_salvaExercicioECasosTeste() {
        when(exercicioRepository.save(any(Exercicio.class))).thenAnswer(invocacao -> {
            Exercicio e = invocacao.getArgument(0);
            e.setId(1L);
            return e;
        });
        when(casoTesteRepository.findByExercicioIdOrderByIdAsc(1L)).thenReturn(List.of());

        ExercicioDetalheResponse response = exercicioService.criar(requestValido(), "MODERADOR", 9L);

        assertThat(response.getTitulo()).isEqualTo("Soma de dois números");
        verify(exercicioRepository).save(any(Exercicio.class));
        verify(casoTesteRepository, times(1)).save(any(CasoTeste.class));
    }

    @Test
    void criar_comoRascunho_naoApareceNoCatalogo() {
        ExercicioRequest request = requestValido();
        request.setPublicar(false);
        when(exercicioRepository.save(any(Exercicio.class))).thenAnswer(invocacao -> {
            Exercicio e = invocacao.getArgument(0);
            e.setId(2L);
            return e;
        });
        when(casoTesteRepository.findByExercicioIdOrderByIdAsc(2L)).thenReturn(List.of());

        exercicioService.criar(request, "MODERADOR", 9L);

        verify(exercicioRepository).save(org.mockito.ArgumentMatchers.argThat(
                e -> e.getStatus() == StatusExercicio.RASCUNHO));
    }

    @Test
    void buscarPorId_quandoNaoExiste_lancaRecursoNaoEncontrado() {
        when(exercicioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> exercicioService.buscarPorId(99L, null))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void listar_calculaTaxaDeAcertoEStatusPessoal() {
        Exercicio exercicio = Exercicio.builder()
                .id(1L).titulo("Soma").enunciado("...").dificuldade(Dificuldade.FACIL)
                .categoria("Matemática").status(StatusExercicio.PUBLICADO).autorId(9L).build();
        when(exercicioRepository.findByStatus(StatusExercicio.PUBLICADO)).thenReturn(List.of(exercicio));
        when(submissaoRepository.countByExercicioId(1L)).thenReturn(4L);
        when(submissaoRepository.countByExercicioIdAndVeredito(1L, Veredito.ACEITO)).thenReturn(1L);
        Submissao aceita = Submissao.builder().veredito(Veredito.ACEITO).build();
        when(submissaoRepository.findByExercicioIdAndUsuarioId(1L, 7L)).thenReturn(List.of(aceita));

        List<ExercicioResumoResponse> catalogo = exercicioService.listar(null, null, 7L);

        assertThat(catalogo).hasSize(1);
        assertThat(catalogo.get(0).getTaxaAcerto()).isEqualTo(25.0);
        assertThat(catalogo.get(0).getStatusPessoal()).isEqualTo(StatusPessoal.RESOLVIDO);
    }
}
