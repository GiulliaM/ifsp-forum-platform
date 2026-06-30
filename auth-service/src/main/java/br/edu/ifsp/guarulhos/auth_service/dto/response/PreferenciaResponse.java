package br.edu.ifsp.guarulhos.auth_service.dto.response;

import br.edu.ifsp.guarulhos.auth_service.model.enums.NivelAprendizado;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PreferenciaResponse {

    private Long usuarioId;
    private NivelAprendizado nivel;
    private List<String> interesses;
    private List<String> linguagens;
}
