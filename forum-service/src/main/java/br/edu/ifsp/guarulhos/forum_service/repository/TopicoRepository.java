package br.edu.ifsp.guarulhos.forum_service.repository;

import br.edu.ifsp.guarulhos.forum_service.model.Topico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicoRepository extends JpaRepository<Topico, Long> {

    List<Topico> findByCategoria(String categoria);

    List<Topico> findByFixadoTrue();

    boolean existsByTituloIgnoreCase(String titulo);
}
