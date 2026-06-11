package br.edu.ifsp.guarulhos.forum_service.repository;

import br.edu.ifsp.guarulhos.forum_service.model.Seguimento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SeguimentoRepository extends JpaRepository<Seguimento, Long> {

    Optional<Seguimento> findByUsuarioIdAndTopicoId(Long usuarioId, Long topicoId);

    List<Seguimento> findByUsuarioId(Long usuarioId);
}
