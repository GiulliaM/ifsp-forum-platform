package br.edu.ifsp.guarulhos.algorithm_service.repository;

import br.edu.ifsp.guarulhos.algorithm_service.model.Exercicio;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Dificuldade;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.StatusExercicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExercicioRepository extends JpaRepository<Exercicio, Long> {

    List<Exercicio> findByStatus(StatusExercicio status);

    List<Exercicio> findByStatusAndDificuldade(StatusExercicio status, Dificuldade dificuldade);

    List<Exercicio> findByStatusAndCategoria(StatusExercicio status, String categoria);
}
