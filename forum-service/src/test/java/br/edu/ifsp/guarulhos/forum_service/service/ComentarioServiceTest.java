package br.edu.ifsp.guarulhos.forum_service.service;

import br.edu.ifsp.guarulhos.forum_service.dto.request.ComentarioRequest;
import br.edu.ifsp.guarulhos.forum_service.dto.response.ComentarioResponse;
import br.edu.ifsp.guarulhos.forum_service.exception.AcessoNegadoException;
import br.edu.ifsp.guarulhos.forum_service.exception.RegraNegocioException;
import br.edu.ifsp.guarulhos.forum_service.model.Comentario;
import br.edu.ifsp.guarulhos.forum_service.model.Topico;
import br.edu.ifsp.guarulhos.forum_service.repository.ComentarioRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.LikeRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.TopicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComentarioServiceTest {

    @Mock
    private ComentarioRepository comentarioRepository;
    @Mock
    private TopicoRepository topicoRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PontuacaoService pontuacaoService;
    @InjectMocks
    private ComentarioService comentarioService;

    @Test
    void criar_emTopicoEncerrado_lancaRegraNegocio() {
        Topico encerrado = Topico.builder().id(1L).encerrado(true).build();
        when(topicoRepository.findById(1L)).thenReturn(Optional.of(encerrado));
        ComentarioRequest request = new ComentarioRequest();
        request.setConteudo("posso comentar?");

        assertThatThrownBy(() -> comentarioService.criar(1L, request, 3L))
                .isInstanceOf(RegraNegocioException.class);
        verify(comentarioRepository, never()).save(any());
    }

    @Test
    void criar_emTopicoAberto_salvaComentario() {
        Topico topico = Topico.builder().id(1L).encerrado(false).build();
        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));
        when(likeRepository.countByTipoAndReferenciaId(any(), any())).thenReturn(0L);
        ComentarioRequest request = new ComentarioRequest();
        request.setConteudo("minha resposta para o tópico");

        ComentarioResponse response = comentarioService.criar(1L, request, 3L);

        assertThat(response.getConteudo()).isEqualTo("minha resposta para o tópico");
        assertThat(response.getAutorId()).isEqualTo(3L);
        verify(comentarioRepository).save(any(Comentario.class));
    }

    @Test
    void editar_porQuemNaoEhDono_lancaAcessoNegado() {
        Comentario comentario = Comentario.builder()
                .id(1L).autorId(10L).conteudo("original")
                .criadoEm(LocalDateTime.now()).build();
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentario));
        ComentarioRequest request = new ComentarioRequest();
        request.setConteudo("tentando editar o que não é meu");

        assertThatThrownBy(() -> comentarioService.editar(1L, request, 99L))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void editar_aposOsTrintaMinutos_lancaRegraNegocio() {
        Comentario comentario = Comentario.builder()
                .id(1L).autorId(10L).conteudo("original")
                .criadoEm(LocalDateTime.now().minusMinutes(31)).build();
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentario));
        ComentarioRequest request = new ComentarioRequest();
        request.setConteudo("editando tarde demais");

        assertThatThrownBy(() -> comentarioService.editar(1L, request, 10L))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void editar_dentroDoPrazo_atualizaConteudo() {
        Comentario comentario = Comentario.builder()
                .id(1L).autorId(10L).conteudo("conteúdo antigo")
                .criadoEm(LocalDateTime.now().minusMinutes(5)).build();
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentario));
        when(likeRepository.countByTipoAndReferenciaId(any(), any())).thenReturn(0L);
        ComentarioRequest request = new ComentarioRequest();
        request.setConteudo("conteúdo corrigido");

        ComentarioResponse response = comentarioService.editar(1L, request, 10L);

        assertThat(response.getConteudo()).isEqualTo("conteúdo corrigido");
        assertThat(response.getEditadoEm()).isNotNull();
        verify(comentarioRepository).save(comentario);
    }
}
