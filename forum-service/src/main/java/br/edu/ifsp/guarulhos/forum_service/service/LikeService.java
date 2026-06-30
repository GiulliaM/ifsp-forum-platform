package br.edu.ifsp.guarulhos.forum_service.service;

import br.edu.ifsp.guarulhos.forum_service.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.guarulhos.forum_service.model.Comentario;
import br.edu.ifsp.guarulhos.forum_service.model.Like;
import br.edu.ifsp.guarulhos.forum_service.model.Topico;
import br.edu.ifsp.guarulhos.forum_service.model.enums.TipoLike;
import br.edu.ifsp.guarulhos.forum_service.repository.ComentarioRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.LikeRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.TopicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final TopicoRepository topicoRepository;
    private final ComentarioRepository comentarioRepository;
    private final PontuacaoService pontuacaoService;

    public long alternarLikeTopico(Long topicoId, Long usuarioId) {
        Topico topico = topicoRepository.findById(topicoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tópico não encontrado"));
        alternar(usuarioId, TipoLike.TOPICO, topicoId, topico.getAutorId());
        return likeRepository.countByTipoAndReferenciaId(TipoLike.TOPICO, topicoId);
    }

    public long alternarLikeComentario(Long comentarioId, Long usuarioId) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Comentário não encontrado"));
        alternar(usuarioId, TipoLike.COMENTARIO, comentarioId, comentario.getAutorId());
        return likeRepository.countByTipoAndReferenciaId(TipoLike.COMENTARIO, comentarioId);
    }

    private void alternar(Long usuarioId, TipoLike tipo, Long referenciaId, Long autorConteudo) {
        likeRepository.findByUsuarioIdAndTipoAndReferenciaId(usuarioId, tipo, referenciaId)
                .ifPresentOrElse(
                        // o gamification-service ainda não expõe estorno de pontos (ver SPRINT2.md);
                        // ao tirar o like, o ponto já concedido permanece.
                        likeRepository::delete,
                        () -> {
                            likeRepository.save(Like.builder()
                                    .usuarioId(usuarioId)
                                    .tipo(tipo)
                                    .referenciaId(referenciaId)
                                    .build());
                            pontuacaoService.registrarLike(autorConteudo, referenciaId, usuarioId);
                        }
                );
    }
}
