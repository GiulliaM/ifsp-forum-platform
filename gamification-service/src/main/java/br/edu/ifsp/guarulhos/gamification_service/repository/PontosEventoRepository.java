package br.edu.ifsp.guarulhos.gamification_service.repository;

import br.edu.ifsp.guarulhos.gamification_service.model.PontosEvento;
import br.edu.ifsp.guarulhos.gamification_service.model.enums.TipoEvento;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface PontosEventoRepository extends JpaRepository<PontosEvento, Long> {

    boolean existsByTipoAndReferenciaIdAndUsuarioId(TipoEvento tipo, Long referenciaId, Long usuarioId);

    List<PontosEvento> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);

    /** Quantidade de eventos de um tipo específico para o usuário; usado pela avaliação de badges. */
    long countByUsuarioIdAndTipo(Long usuarioId, TipoEvento tipo);

    @Query("""
            SELECT e.usuarioId AS usuarioId, SUM(e.pontos) AS total
            FROM PontosEvento e
            WHERE e.tipo IN :tipos AND e.criadoEm >= :desde
            GROUP BY e.usuarioId
            ORDER BY total DESC
            """)
    List<RankingProjection> ranking(@Param("tipos") Collection<TipoEvento> tipos,
                                    @Param("desde") LocalDateTime desde,
                                    Pageable pageable);
}
