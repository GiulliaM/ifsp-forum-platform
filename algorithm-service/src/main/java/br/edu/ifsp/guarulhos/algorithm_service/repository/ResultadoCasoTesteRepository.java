package br.edu.ifsp.guarulhos.algorithm_service.repository;

import br.edu.ifsp.guarulhos.algorithm_service.model.ResultadoCasoTeste;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultadoCasoTesteRepository extends JpaRepository<ResultadoCasoTeste, Long> {

    List<ResultadoCasoTeste> findBySubmissaoIdOrderByIdAsc(Long submissaoId);
}
