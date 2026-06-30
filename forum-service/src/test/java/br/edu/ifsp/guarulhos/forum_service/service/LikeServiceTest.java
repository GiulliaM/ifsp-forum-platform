package br.edu.ifsp.guarulhos.forum_service.service;

import br.edu.ifsp.guarulhos.forum_service.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.guarulhos.forum_service.model.Like;
import br.edu.ifsp.guarulhos.forum_service.model.Topico;
import br.edu.ifsp.guarulhos.forum_service.model.enums.TipoLike;
import br.edu.ifsp.guarulhos.forum_service.repository.ComentarioRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.LikeRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.TopicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private TopicoRepository topicoRepository;
    @Mock
    private ComentarioRepository comentarioRepository;
    @Mock
    private PontuacaoService pontuacaoService;
    @InjectMocks
    private LikeService likeService;

    @Test
    void alternarLikeTopico_quandoAindaNaoCurtiu_salvaLike() {
        Topico topico = Topico.builder().id(1L).autorId(9L).build();
        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));
        when(likeRepository.findByUsuarioIdAndTipoAndReferenciaId(5L, TipoLike.TOPICO, 1L))
                .thenReturn(Optional.empty());
        when(likeRepository.countByTipoAndReferenciaId(TipoLike.TOPICO, 1L)).thenReturn(1L);

        long total = likeService.alternarLikeTopico(1L, 5L);

        assertThat(total).isEqualTo(1L);
        verify(likeRepository).save(any(Like.class));
        verify(likeRepository, never()).delete(any());
    }

    @Test
    void alternarLikeTopico_quandoJaCurtiu_removeLike() {
        Like like = Like.builder()
                .id(1L).usuarioId(5L).tipo(TipoLike.TOPICO).referenciaId(1L).build();
        Topico topico = Topico.builder().id(1L).autorId(9L).build();
        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));
        when(likeRepository.findByUsuarioIdAndTipoAndReferenciaId(5L, TipoLike.TOPICO, 1L))
                .thenReturn(Optional.of(like));
        when(likeRepository.countByTipoAndReferenciaId(TipoLike.TOPICO, 1L)).thenReturn(0L);

        long total = likeService.alternarLikeTopico(1L, 5L);

        assertThat(total).isEqualTo(0L);
        verify(likeRepository).delete(like);
        verify(likeRepository, never()).save(any());
    }

    @Test
    void alternarLikeTopico_quandoTopicoNaoExiste_lancaRecursoNaoEncontrado() {
        when(topicoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.alternarLikeTopico(1L, 5L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
