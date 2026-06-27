package br.edu.ifsp.guarulhos.gamification_service.repository;

import br.edu.ifsp.guarulhos.gamification_service.model.UsuarioConquista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acesso às conquistas desbloqueadas por usuário, exibidas no perfil (US-12, CA3).
 */
public interface UsuarioConquistaRepository extends JpaRepository<UsuarioConquista, Long> {

    List<UsuarioConquista> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndConquistaId(Long usuarioId, Long conquistaId);
}
