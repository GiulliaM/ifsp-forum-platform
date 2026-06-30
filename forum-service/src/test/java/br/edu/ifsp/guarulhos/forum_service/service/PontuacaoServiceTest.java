package br.edu.ifsp.guarulhos.forum_service.service;

import br.edu.ifsp.guarulhos.forum_service.dto.response.RankingItemResponse;
import br.edu.ifsp.guarulhos.forum_service.model.Pontuacao;
import br.edu.ifsp.guarulhos.forum_service.model.enums.TipoPontuacao;
import br.edu.ifsp.guarulhos.forum_service.repository.PontuacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PontuacaoServiceTest {

    @Mock
    private PontuacaoRepository pontuacaoRepository;

    @InjectMocks
    private PontuacaoService pontuacaoService;

    @Test
    void registrarTopico_deveSalvarComCincoPontos() {
        pontuacaoService.registrarTopico(1L, 10L);

        verify(pontuacaoRepository).save(argThat(p ->
                p.getPontos() == 5
                && p.getTipo() == TipoPontuacao.CRIAR_TOPICO
                && p.getUsuarioId().equals(1L)
                && p.getReferenciaId().equals(10L)
        ));
    }

    @Test
    void registrarComentario_deveSalvarComTresPontos() {
        pontuacaoService.registrarComentario(2L, 20L);

        verify(pontuacaoRepository).save(argThat(p ->
                p.getPontos() == 3
                && p.getTipo() == TipoPontuacao.COMENTAR
                && p.getUsuarioId().equals(2L)
        ));
    }

    @Test
    void registrarLike_quandoNaoExiste_deveSalvarComDoisPontos() {
        when(pontuacaoRepository.findByTipoAndReferenciaIdAndCurtidorId(
                TipoPontuacao.RECEBER_LIKE, 5L, 99L))
                .thenReturn(Optional.empty());

        pontuacaoService.registrarLike(3L, 5L, 99L);

        verify(pontuacaoRepository).save(argThat(p ->
                p.getPontos() == 2
                && p.getTipo() == TipoPontuacao.RECEBER_LIKE
                && p.getCurtidorId().equals(99L)
        ));
    }

    @Test
    void registrarLike_quandoJaExiste_naoDeveDuplicar() {
        Pontuacao existente = Pontuacao.builder().id(1L).build();
        when(pontuacaoRepository.findByTipoAndReferenciaIdAndCurtidorId(
                TipoPontuacao.RECEBER_LIKE, 5L, 99L))
                .thenReturn(Optional.of(existente));

        pontuacaoService.registrarLike(3L, 5L, 99L);

        verify(pontuacaoRepository, never()).save(any());
    }

    @Test
    void removerPontoLike_quandoExiste_deveExcluir() {
        Pontuacao p = Pontuacao.builder().id(7L).build();
        when(pontuacaoRepository.findByTipoAndReferenciaIdAndCurtidorId(
                TipoPontuacao.RECEBER_LIKE, 5L, 99L))
                .thenReturn(Optional.of(p));

        pontuacaoService.removerPontoLike(3L, 5L, 99L);

        verify(pontuacaoRepository).delete(p);
    }

    @Test
    void removerPontoLike_quandoNaoExiste_naoFazNada() {
        when(pontuacaoRepository.findByTipoAndReferenciaIdAndCurtidorId(any(), any(), any()))
                .thenReturn(Optional.empty());

        pontuacaoService.removerPontoLike(3L, 5L, 99L);

        verify(pontuacaoRepository, never()).delete(any(Pontuacao.class));
    }

    @Test
    void ranking_deveRetornarListaOrdenada() {
        Object[] row1 = {10L, 100L};
        Object[] row2 = {20L, 50L};
        when(pontuacaoRepository.findRanking(PageRequest.of(0, 10)))
                .thenReturn(List.of(row1, row2));

        List<RankingItemResponse> ranking = pontuacaoService.ranking(10);

        assertThat(ranking).hasSize(2);
        assertThat(ranking.get(0).getPosicao()).isEqualTo(1);
        assertThat(ranking.get(0).getUsuarioId()).isEqualTo(10L);
        assertThat(ranking.get(0).getTotalPontos()).isEqualTo(100L);
        assertThat(ranking.get(1).getPosicao()).isEqualTo(2);
    }
}
