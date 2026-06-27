package br.edu.ifsp.guarulhos.algorithm_service.repository;

import br.edu.ifsp.guarulhos.algorithm_service.model.ResultadoCasoTeste;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acesso aos resultados por caso de teste de uma submissão, que compõem o feedback
 * detalhado (US-09).
 */
public interface ResultadoCasoTesteRepository extends JpaRepository<ResultadoCasoTeste, Long> {

    List<ResultadoCasoTeste> findBySubmissaoIdOrderByIdAsc(Long submissaoId);
}
