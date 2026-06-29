package br.edu.ifsp.guarulhos.gamification_service.service;

import br.edu.ifsp.guarulhos.gamification_service.dto.response.RankingItemResponse;
import br.edu.ifsp.guarulhos.gamification_service.model.enums.Escopo;
import br.edu.ifsp.guarulhos.gamification_service.model.enums.Periodo;
import br.edu.ifsp.guarulhos.gamification_service.model.enums.TipoEvento;
import br.edu.ifsp.guarulhos.gamification_service.repository.PontosEventoRepository;
import br.edu.ifsp.guarulhos.gamification_service.repository.RankingProjection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock
    private PontosEventoRepository pontosEventoRepository;
    @InjectMocks
    private RankingService rankingService;

    @Test
    void consultar_escopoGeral_incluiTodosOsTiposDeEvento() {
        when(pontosEventoRepository.ranking(any(), any(), any())).thenReturn(List.of());

        rankingService.consultar(Escopo.GERAL, Periodo.TOTAL);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<TipoEvento>> tiposCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(pontosEventoRepository).ranking(tiposCaptor.capture(), any(), any());

        assertThat(tiposCaptor.getValue()).containsExactlyInAnyOrder(TipoEvento.values());
    }

    @Test
    void consultar_escopoForum_filtraApenasTopicoCriadoComentarioELike() {
        when(pontosEventoRepository.ranking(any(), any(), any())).thenReturn(List.of());

        rankingService.consultar(Escopo.FORUM, Periodo.TOTAL);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<TipoEvento>> tiposCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(pontosEventoRepository).ranking(tiposCaptor.capture(), any(), any());

        assertThat(tiposCaptor.getValue())
                .containsExactlyInAnyOrder(
                        TipoEvento.TOPICO_CRIADO, TipoEvento.COMENTARIO, TipoEvento.LIKE_RECEBIDO);
        assertThat(tiposCaptor.getValue()).doesNotContain(TipoEvento.EXERCICIO_RESOLVIDO);
    }

    @Test
    void consultar_escopoAlgoritmos_filtraApenasExercicioResolvido() {
        when(pontosEventoRepository.ranking(any(), any(), any())).thenReturn(List.of());

        rankingService.consultar(Escopo.ALGORITMOS, Periodo.TOTAL);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<TipoEvento>> tiposCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(pontosEventoRepository).ranking(tiposCaptor.capture(), any(), any());

        assertThat(tiposCaptor.getValue())
                .containsExactlyInAnyOrder(TipoEvento.EXERCICIO_RESOLVIDO);
    }

    @Test
    void consultar_periodoSemana_usaDataHaMenosDe8Dias() {
        when(pontosEventoRepository.ranking(any(), any(), any())).thenReturn(List.of());

        LocalDateTime antes = LocalDateTime.now().minusWeeks(1).minusMinutes(1);
        rankingService.consultar(Escopo.GERAL, Periodo.SEMANA);

        ArgumentCaptor<LocalDateTime> desdeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(pontosEventoRepository).ranking(any(), desdeCaptor.capture(), any());

        assertThat(desdeCaptor.getValue()).isAfter(antes);
    }

    @Test
    void consultar_periodoTotal_usaDataMuitoAntiga() {
        when(pontosEventoRepository.ranking(any(), any(), any())).thenReturn(List.of());

        rankingService.consultar(Escopo.GERAL, Periodo.TOTAL);

        ArgumentCaptor<LocalDateTime> desdeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(pontosEventoRepository).ranking(any(), desdeCaptor.capture(), any());

        assertThat(desdeCaptor.getValue()).isBefore(LocalDateTime.of(2000, 1, 1, 0, 0));
    }

    @Test
    void consultar_atribuiPosicaoSequencialCorreta() {
        RankingProjection p1 = projecao(10L, 300L);
        RankingProjection p2 = projecao(20L, 200L);
        RankingProjection p3 = projecao(30L, 100L);
        when(pontosEventoRepository.ranking(any(), any(), any())).thenReturn(List.of(p1, p2, p3));

        List<RankingItemResponse> resultado = rankingService.consultar(Escopo.GERAL, Periodo.TOTAL);

        assertThat(resultado).hasSize(3);
        assertThat(resultado.get(0).getPosicao()).isEqualTo(1);
        assertThat(resultado.get(0).getUsuarioId()).isEqualTo(10L);
        assertThat(resultado.get(1).getPosicao()).isEqualTo(2);
        assertThat(resultado.get(2).getPosicao()).isEqualTo(3);
    }

    @Test
    void consultar_limitaTop50Registros() {
        when(pontosEventoRepository.ranking(any(), any(), any())).thenReturn(List.of());

        rankingService.consultar(Escopo.GERAL, Periodo.TOTAL);

        ArgumentCaptor<Pageable> pageCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(pontosEventoRepository).ranking(any(), any(), pageCaptor.capture());

        assertThat(pageCaptor.getValue().getPageSize()).isEqualTo(50);
    }

    private RankingProjection projecao(long usuarioId, long total) {
        return new RankingProjection() {
            public Long getUsuarioId() { return usuarioId; }
            public Long getTotal()     { return total; }
        };
    }
}
