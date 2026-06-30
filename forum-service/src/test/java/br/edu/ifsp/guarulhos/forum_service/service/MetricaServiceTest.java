package br.edu.ifsp.guarulhos.forum_service.service;

import br.edu.ifsp.guarulhos.forum_service.dto.response.MetricaCategoriaResponse;
import br.edu.ifsp.guarulhos.forum_service.exception.RegraNegocioException;
import br.edu.ifsp.guarulhos.forum_service.repository.TopicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricaServiceTest {

    @Mock
    private TopicoRepository topicoRepository;

    @InjectMocks
    private MetricaService metricaService;

    @Test
    void categoriasSemResposta_periodoInvalido_lancaExcecao() {
        assertThatThrownBy(() -> metricaService.categoriasSemResposta(15))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void categoriasSemResposta_periodo7_retornaListaCorreta() {
        Object[] row = {"Java", 3L};
        when(topicoRepository.findCategoriasSemResposta(any(LocalDateTime.class)))
                .thenReturn(List.<Object[]>of(row));

        List<MetricaCategoriaResponse> resultado = metricaService.categoriasSemResposta(7);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCategoria()).isEqualTo("Java");
        assertThat(resultado.get(0).getTotalTopicosSemResposta()).isEqualTo(3L);
        assertThat(resultado.get(0).getPeriodoDias()).isEqualTo(7);
    }

    @Test
    void categoriasSemResposta_periodo30_retornaListaCorreta() {
        Object[] row1 = {"Algoritmos", 5L};
        Object[] row2 = {"Banco de Dados", 2L};
        when(topicoRepository.findCategoriasSemResposta(any(LocalDateTime.class)))
                .thenReturn(List.<Object[]>of(row1, row2));

        List<MetricaCategoriaResponse> resultado = metricaService.categoriasSemResposta(30);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getCategoria()).isEqualTo("Algoritmos");
        assertThat(resultado.get(1).getPeriodoDias()).isEqualTo(30);
    }
}
