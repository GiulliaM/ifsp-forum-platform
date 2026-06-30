package br.edu.ifsp.guarulhos.gamification_service.repository;

import br.edu.ifsp.guarulhos.gamification_service.model.Conquista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConquistaRepository extends JpaRepository<Conquista, Long> {

    Optional<Conquista> findByCriterio(String criterio);
}
