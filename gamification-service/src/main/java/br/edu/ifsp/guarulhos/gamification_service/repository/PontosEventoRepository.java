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

/**
 * Acesso aos eventos de pontuação. Concentra a checagem de idempotência (US-12) e a
 * consulta agregada que alimenta o ranking (US-11).
 */
public interface PontosEventoRepository extends JpaRepository<PontosEvento, Long> {

    /** Idempotência: o mesmo fato não pode pontuar duas vezes. */
    boolean existsByTipoAndReferenciaIdAndUsuarioId(TipoEvento tipo, Long referenciaId, Long usuarioId);

    /** Extrato de pontos do usuário, mais recentes primeiro (US-12, CA2). */
    List<PontosEvento> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);

    /** Quantidade de eventos de um tipo específico para o usuário; usado pela avaliação de badges. */
    long countByUsuarioIdAndTipo(Long usuarioId, TipoEvento tipo);

    /**
     * Ranking agregado: soma os pontos por usuário dentro do escopo (tipos de evento) e do
     * período (a partir de {@code desde}), ordenando do maior para o menor (US-11).
     */
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
