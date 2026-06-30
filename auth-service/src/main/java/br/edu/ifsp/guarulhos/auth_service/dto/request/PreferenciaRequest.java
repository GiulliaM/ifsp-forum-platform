package br.edu.ifsp.guarulhos.auth_service.dto.request;

import br.edu.ifsp.guarulhos.auth_service.model.enums.NivelAprendizado;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PreferenciaRequest {

    @NotNull(message = "nivel é obrigatório")
    private NivelAprendizado nivel;

    @Size(max = 10, message = "máximo de 10 interesses")
    private List<String> interesses;

    @Size(max = 10, message = "máximo de 10 linguagens")
    private List<String> linguagens;
}
