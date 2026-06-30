package br.edu.ifsp.guarulhos.algorithm_service.repository;

import br.edu.ifsp.guarulhos.algorithm_service.model.Submissao;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Veredito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissaoRepository extends JpaRepository<Submissao, Long> {

    List<Submissao> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);

    List<Submissao> findByExercicioIdAndUsuarioId(Long exercicioId, Long usuarioId);

    long countByExercicioId(Long exercicioId);

    long countByExercicioIdAndVeredito(Long exercicioId, Veredito veredito);
}
