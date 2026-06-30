package br.edu.ifsp.guarulhos.forum_service.repository;

import br.edu.ifsp.guarulhos.forum_service.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findByTopicoIdOrderByCriadoEmAsc(Long topicoId);

    long countByTopicoId(Long topicoId);

    boolean existsByTopicoIdAndCriadoEmAfter(Long topicoId, LocalDateTime data);

    @Query("SELECT DISTINCT t.categoria FROM Topico t " +
           "WHERE t.id IN (SELECT c.topico.id FROM Comentario c WHERE c.autorId = :autorId)")
    List<String> findCategoriasByComentadorId(@Param("autorId") Long autorId);
}
