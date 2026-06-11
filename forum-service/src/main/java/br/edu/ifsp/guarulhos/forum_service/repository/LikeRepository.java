package br.edu.ifsp.guarulhos.forum_service.repository;

import br.edu.ifsp.guarulhos.forum_service.model.Like;
import br.edu.ifsp.guarulhos.forum_service.model.enums.TipoLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUsuarioIdAndTipoAndReferenciaId(
            Long usuarioId, TipoLike tipo, Long referenciaId
    );

    long countByTipoAndReferenciaId(TipoLike like, Long referenciaId);
}
