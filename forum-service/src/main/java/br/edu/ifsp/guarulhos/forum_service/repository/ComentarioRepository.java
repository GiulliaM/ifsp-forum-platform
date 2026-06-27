package br.edu.ifsp.guarulhos.forum_service.repository;

import br.edu.ifsp.guarulhos.forum_service.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findByTopicoIdOrderByCriadoEmAsc(Long topicoId);

    long countByTopicoId(Long topicoId);

    boolean existsByTopicoIdAndCriadoEmAfter(Long topicoId, LocalDateTime data);
}

