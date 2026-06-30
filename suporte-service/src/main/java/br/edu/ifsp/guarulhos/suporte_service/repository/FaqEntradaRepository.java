package br.edu.ifsp.guarulhos.suporte_service.repository;

import br.edu.ifsp.guarulhos.suporte_service.model.FaqEntrada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FaqEntradaRepository extends JpaRepository<FaqEntrada, Long> {

    Optional<FaqEntrada> findByPergunta(String pergunta);
}
