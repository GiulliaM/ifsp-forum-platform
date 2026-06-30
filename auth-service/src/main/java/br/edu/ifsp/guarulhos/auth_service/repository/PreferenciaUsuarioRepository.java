package br.edu.ifsp.guarulhos.auth_service.repository;

import br.edu.ifsp.guarulhos.auth_service.model.PreferenciaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferenciaUsuarioRepository extends JpaRepository<PreferenciaUsuario, Long> {
}
