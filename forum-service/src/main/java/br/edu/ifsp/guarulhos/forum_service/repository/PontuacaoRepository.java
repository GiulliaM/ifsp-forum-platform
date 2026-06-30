package br.edu.ifsp.guarulhos.forum_service.repository;

import br.edu.ifsp.guarulhos.forum_service.model.Pontuacao;
import br.edu.ifsp.guarulhos.forum_service.model.enums.TipoPontuacao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PontuacaoRepository extends JpaRepository<Pontuacao, Long> {

    List<Pontuacao> findByUsuarioId(Long usuarioId);

    Optional<Pontuacao> findByTipoAndReferenciaIdAndCurtidorId(
            TipoPontuacao tipo, Long referenciaId, Long curtidorId);

    @Query("SELECT p.usuarioId, SUM(p.pontos) FROM Pontuacao p GROUP BY p.usuarioId ORDER BY SUM(p.pontos) DESC")
    List<Object[]> findRanking(Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.pontos), 0) FROM Pontuacao p WHERE p.usuarioId = :usuarioId")
    long sumPontosByUsuarioId(@Param("usuarioId") Long usuarioId);
}
