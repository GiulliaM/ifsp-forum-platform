package br.edu.ifsp.guarulhos.forum_service.service;

import br.edu.ifsp.guarulhos.forum_service.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.guarulhos.forum_service.model.Like;
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

    /*
    * US-03 - curtir um tópico. Funciona como alternância: se o usuário já curtiu,
    * clicar de novo remove o like. Retorna o total de likes atualizado.
    * */
    public long alternarLikeTopico(Long topicoId, Long usuarioId){
        if(!topicoRepository.existsById(topicoId)){
            throw new RecursoNaoEncontradoException("Tópico não encontrado");
        }
        alternar(usuarioId, TipoLike.TOPICO, topicoId);
        return likeRepository.countByTipoAndReferenciaId(TipoLike.TOPICO, topicoId);
    }

    /*
    * US-03 - curtir um comentário (mesma lógica de alternância).
    * */
    public long alternarLikeComentario(Long comentarioId, Long usuarioId){
        if(!comentarioRepository.existsById(comentarioId)){
            throw new RecursoNaoEncontradoException("Comentário não encontrado");
        }
        alternar(usuarioId, TipoLike.COMENTARIO, comentarioId);
        return likeRepository.countByTipoAndReferenciaId(TipoLike.COMENTARIO, comentarioId);
    }

    private void alternar(Long usuarioId, TipoLike tipo, Long referenciaId){
        likeRepository.findByUsuarioIdAndTipoAndReferenciaId(usuarioId, tipo, referenciaId)
                .ifPresentOrElse(
                        likeRepository::delete,
                        () -> likeRepository.save(Like.builder()
                                .usuarioId(usuarioId)
                                .tipo(tipo)
                                .referenciaId(referenciaId)
                                .build())
                );
    }
}
