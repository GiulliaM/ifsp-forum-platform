package br.edu.ifsp.guarulhos.forum_service.repository;

import br.edu.ifsp.guarulhos.forum_service.model.Topico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TopicoRepository extends JpaRepository<Topico, Long> {

    List<Topico> findByCategoria(String categoria);

    List<Topico> findByFixadoTrue();

    @Query("SELECT DISTINCT t.categoria FROM Topico t WHERE t.autorId = :autorId")
    List<String> findCategoriasByAutorId(@Param("autorId") Long autorId);

    @Query("SELECT t.categoria, COUNT(t) FROM Topico t " +
           "WHERE t.criadoEm >= :desde " +
           "AND (SELECT COUNT(c) FROM Comentario c WHERE c.topico.id = t.id) = 0 " +
           "GROUP BY t.categoria ORDER BY COUNT(t) DESC")
    List<Object[]> findCategoriasSemResposta(@Param("desde") LocalDateTime desde);
}
