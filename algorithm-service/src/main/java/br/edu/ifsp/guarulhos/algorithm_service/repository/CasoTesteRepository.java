package br.edu.ifsp.guarulhos.algorithm_service.repository;

import br.edu.ifsp.guarulhos.algorithm_service.model.CasoTeste;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * A ordem estável é necessária para casar posicionalmente com a lista de saídas
 * enviadas na submissão.
 */
public interface CasoTesteRepository extends JpaRepository<CasoTeste, Long> {

    List<CasoTeste> findByExercicioIdOrderByIdAsc(Long exercicioId);
}
