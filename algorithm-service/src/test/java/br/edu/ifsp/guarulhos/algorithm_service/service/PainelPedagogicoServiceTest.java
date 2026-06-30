package br.edu.ifsp.guarulhos.algorithm_service.service;

import br.edu.ifsp.guarulhos.algorithm_service.dto.response.PainelPedagogicoItemResponse;
import br.edu.ifsp.guarulhos.algorithm_service.exception.AcessoNegadoException;
import br.edu.ifsp.guarulhos.algorithm_service.model.Exercicio;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Dificuldade;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.StatusExercicio;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PainelPedagogicoServiceTest {

    @Mock
    private ExercicioRepository exercicioRepository;

    @Mock
    private SubmissaoRepository submissaoRepository;

    @Mock
    private CasoTesteRepository casoTesteRepository;

    @InjectMocks
    private ExercicioService exercicioService;

    @Test
    void listarPainelPedagogico_naoModerador_lancaExcecao() {
        assertThatThrownBy(() -> exercicioService.listarPainelPedagogico(10, "ESTUDANTE"))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void listarPainelPedagogico_ordenaPorMaiorTaxaDeErro() {
        Exercicio ex1 = exercicio(1L, "Fácil demais", Dificuldade.FACIL);
        Exercicio ex2 = exercicio(2L, "Ninguém resolve", Dificuldade.DIFICIL);

        when(exercicioRepository.findByStatus(StatusExercicio.PUBLICADO)).thenReturn(List.of(ex1, ex2));

        // ex1: 10 submissões, 8 aceitas → taxaAcerto 80%, taxaErro 20%
        when(submissaoRepository.countByExercicioId(1L)).thenReturn(10L);
        when(submissaoRepository.countByExercicioIdAndVeredito(1L, Veredito.ACEITO)).thenReturn(8L);

        // ex2: 10 submissões, 1 aceita → taxaAcerto 10%, taxaErro 90%
        when(submissaoRepository.countByExercicioId(2L)).thenReturn(10L);
        when(submissaoRepository.countByExercicioIdAndVeredito(2L, Veredito.ACEITO)).thenReturn(1L);

        List<PainelPedagogicoItemResponse> resultado = exercicioService.listarPainelPedagogico(10, "MODERADOR");

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getId()).isEqualTo(2L);
        assertThat(resultado.get(0).getTaxaErro()).isGreaterThan(resultado.get(1).getTaxaErro());
    }

    @Test
    void listarPainelPedagogico_excluiExerciciosSemSubmissoes() {
        Exercicio ex1 = exercicio(1L, "Com submissões", Dificuldade.MEDIO);
        Exercicio ex2 = exercicio(2L, "Sem submissões", Dificuldade.FACIL);

        when(exercicioRepository.findByStatus(StatusExercicio.PUBLICADO)).thenReturn(List.of(ex1, ex2));
        when(submissaoRepository.countByExercicioId(1L)).thenReturn(5L);
        when(submissaoRepository.countByExercicioIdAndVeredito(1L, Veredito.ACEITO)).thenReturn(2L);
        when(submissaoRepository.countByExercicioId(2L)).thenReturn(0L);

        List<PainelPedagogicoItemResponse> resultado = exercicioService.listarPainelPedagogico(10, "MODERADOR");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void listarPainelPedagogico_respeitaLimite() {
        List<Exercicio> exercicios = List.of(
                exercicio(1L, "Ex1", Dificuldade.FACIL),
                exercicio(2L, "Ex2", Dificuldade.MEDIO),
                exercicio(3L, "Ex3", Dificuldade.DIFICIL)
        );
        when(exercicioRepository.findByStatus(StatusExercicio.PUBLICADO)).thenReturn(exercicios);
        for (long i = 1; i <= 3; i++) {
            when(submissaoRepository.countByExercicioId(i)).thenReturn(10L);
            when(submissaoRepository.countByExercicioIdAndVeredito(i, Veredito.ACEITO)).thenReturn(5L);
        }

        List<PainelPedagogicoItemResponse> resultado = exercicioService.listarPainelPedagogico(2, "MODERADOR");

        assertThat(resultado).hasSize(2);
    }

    private Exercicio exercicio(Long id, String titulo, Dificuldade dificuldade) {
        return Exercicio.builder()
                .id(id)
                .titulo(titulo)
                .dificuldade(dificuldade)
                .categoria("algoritmos")
                .status(StatusExercicio.PUBLICADO)
                .autorId(1L)
                .build();
    }
}
