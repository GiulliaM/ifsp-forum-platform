package br.edu.ifsp.guarulhos.suporte_service.repository;

import br.edu.ifsp.guarulhos.suporte_service.model.RespostaChamado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RespostaChamadoRepository extends JpaRepository<RespostaChamado, Long> {

    List<RespostaChamado> findByChamadoIdOrderByCriadoEmAsc(Long chamadoId);
}
