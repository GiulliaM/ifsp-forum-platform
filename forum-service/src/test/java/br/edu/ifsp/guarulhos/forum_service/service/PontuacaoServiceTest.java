package br.edu.ifsp.guarulhos.forum_service.service;

import br.edu.ifsp.guarulhos.forum_service.dto.request.EventoPontosRequest;
import br.edu.ifsp.guarulhos.forum_service.model.enums.TipoEventoPontos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PontuacaoServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec bodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec bodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private PontuacaoService pontuacaoService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pontuacaoService, "gamificationUrl", "http://localhost:8084");
    }

    @SuppressWarnings("unchecked")
    @Test
    void registrarTopico_deveEnviarEventoTopicoCriado() {
        when(restClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.body(any(EventoPontosRequest.class))).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        pontuacaoService.registrarTopico(1L, 10L);

        verify(bodySpec).body(argThat((EventoPontosRequest req) ->
                req.getTipo() == TipoEventoPontos.TOPICO_CRIADO
                && req.getUsuarioId().equals(1L)
                && req.getReferenciaId().equals(10L)
        ));
        verify(bodyUriSpec).uri(eq("http://localhost:8084/api/pontos/eventos"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void registrarComentario_deveEnviarEventoComentario() {
        when(restClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.body(any(EventoPontosRequest.class))).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        pontuacaoService.registrarComentario(2L, 20L);

        verify(bodySpec).body(argThat((EventoPontosRequest req) ->
                req.getTipo() == TipoEventoPontos.COMENTARIO
                && req.getUsuarioId().equals(2L)
                && req.getReferenciaId().equals(20L)
        ));
    }

    @SuppressWarnings("unchecked")
    @Test
    void registrarLike_deveEnviarEventoComAutorDoConteudo() {
        when(restClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.body(any(EventoPontosRequest.class))).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        pontuacaoService.registrarLike(3L, 5L, 99L);

        verify(bodySpec).body(argThat((EventoPontosRequest req) ->
                req.getTipo() == TipoEventoPontos.LIKE_RECEBIDO
                && req.getUsuarioId().equals(3L)
                && req.getReferenciaId().equals(5L)
        ));
    }

    @SuppressWarnings("unchecked")
    @Test
    void registrarEvento_quandoGamificationServiceFalha_naoPropagaExcecao() {
        when(restClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.body(any(EventoPontosRequest.class))).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenThrow(new RuntimeException("conexão recusada"));

        pontuacaoService.registrarTopico(1L, 10L);
    }
}
