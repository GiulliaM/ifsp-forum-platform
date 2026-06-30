package br.edu.ifsp.guarulhos.suporte_service.repository;

import br.edu.ifsp.guarulhos.suporte_service.model.Chamado;
import br.edu.ifsp.guarulhos.suporte_service.model.enums.StatusChamado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    List<Chamado> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);

    Optional<Chamado> findByProtocolo(String protocolo);

    List<Chamado> findByStatusOrderByCriadoEmDesc(StatusChamado status);

    List<Chamado> findAllByOrderByCriadoEmDesc();
}
