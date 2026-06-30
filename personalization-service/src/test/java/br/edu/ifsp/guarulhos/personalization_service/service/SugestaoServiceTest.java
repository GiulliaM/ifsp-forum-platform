package br.edu.ifsp.guarulhos.personalization_service.service;

import br.edu.ifsp.guarulhos.personalization_service.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SugestaoServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec<?> uriSpec;

    @Mock
    private RestClient.RequestHeadersSpec<?> headersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private SugestaoService sugestaoService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sugestaoService, "authUrl", "http://localhost:8081");
        ReflectionTestUtils.setField(sugestaoService, "forumUrl", "http://localhost:8082");
        ReflectionTestUtils.setField(sugestaoService, "algorithmUrl", "http://localhost:8083");
    }

    @SuppressWarnings("unchecked")
    @Test
    void sugerir_comPreferencias_retornaTopicosEExerciciosFiltrados() {
        PreferenciaResponse pref = new PreferenciaResponse();
        pref.setUsuarioId(1L);
        pref.setNivel("INTERMEDIARIO");
        pref.setInteresses(List.of("backend"));
        pref.setLinguagens(List.of("Java"));

        TopicoSugestaoResponse topico = new TopicoSugestaoResponse();
        topico.setId(1L);
        topico.setTitulo("Tópico de backend");
        topico.setCategoria("backend");

        ExercicioSugestaoResponse exercicio = new ExercicioSugestaoResponse();
        exercicio.setId(1L);
        exercicio.setTitulo("Exercício médio");
        exercicio.setDificuldade("MEDIO");

        when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(contains("/preferencias"))).thenReturn((RestClient.RequestHeadersSpec) headersSpec);
        when(headersSpec.header(eq("X-User-Id"), anyString())).thenReturn((RestClient.RequestHeadersSpec) headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(PreferenciaResponse.class)).thenReturn(pref);

        when(uriSpec.uri(contains("/topicos"))).thenReturn((RestClient.RequestHeadersSpec) headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(List.of(topico), List.of(exercicio));

        when(uriSpec.uri(contains("/exercicios"))).thenReturn((RestClient.RequestHeadersSpec) headersSpec);

        SugestaoResponse resultado = sugestaoService.sugerir(1L);

        assertThat(resultado.getTopicos()).hasSize(1);
        assertThat(resultado.getTopicos().get(0).getId()).isEqualTo(1L);
        assertThat(resultado.getExercicios()).hasSize(1);
        assertThat(resultado.getExercicios().get(0).getId()).isEqualTo(1L);
    }

    @SuppressWarnings("unchecked")
    @Test
    void sugerir_quandoAuthServiceFalha_retornaListasVazias() {
        when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(anyString())).thenReturn((RestClient.RequestHeadersSpec) headersSpec);
        when(headersSpec.header(anyString(), anyString())).thenReturn((RestClient.RequestHeadersSpec) headersSpec);
        when(headersSpec.retrieve()).thenThrow(new RuntimeException("conexão recusada"));

        SugestaoResponse resultado = sugestaoService.sugerir(1L);

        assertThat(resultado.getTopicos()).isEmpty();
        assertThat(resultado.getExercicios()).isEmpty();
    }
}
