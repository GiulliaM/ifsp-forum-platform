package br.edu.ifsp.guarulhos.algorithm_service.repository;

import br.edu.ifsp.guarulhos.algorithm_service.model.CasoTeste;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acesso aos casos de teste de um exercício, em ordem estável para casar com a ordem
 * das saídas enviadas na submissão (US-08).
 */
public interface CasoTesteRepository extends JpaRepository<CasoTeste, Long> {

    List<CasoTeste> findByExercicioIdOrderByIdAsc(Long exercicioId);
}
