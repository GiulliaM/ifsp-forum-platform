package br.edu.ifsp.guarulhos.forum_service.service;

import br.edu.ifsp.guarulhos.forum_service.dto.request.TopicoRequest;
import br.edu.ifsp.guarulhos.forum_service.dto.response.TopicoResponse;
import br.edu.ifsp.guarulhos.forum_service.exception.AcessoNegadoException;
import br.edu.ifsp.guarulhos.forum_service.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.guarulhos.forum_service.exception.RegraNegocioException;
import br.edu.ifsp.guarulhos.forum_service.model.Topico;
import br.edu.ifsp.guarulhos.forum_service.repository.ComentarioRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.LikeRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.TopicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopicoServiceTest {

    @Mock
    private TopicoRepository topicoRepository;
    @Mock
    private ComentarioRepository comentarioRepository;
    @Mock
    private LikeRepository likeRepository;
    @InjectMocks
    private TopicoService topicoService;

    @Test
    void criar_deveSalvarERetornarResponse() {
        TopicoRequest request = new TopicoRequest();
        request.setTitulo("Como usar injeção de dependência no Spring");
        request.setDescricao("Estou com dúvida sobre @Autowired e construtor no Spring Boot");
        request.setCategoria("Java");
        when(likeRepository.countByTipoAndReferenciaId(any(), any())).thenReturn(0L);
        when(comentarioRepository.countByTopicoId(any())).thenReturn(0L);

        TopicoResponse response = topicoService.criar(request, 7L);

        assertThat(response.getTitulo()).isEqualTo("Como usar injeção de dependência no Spring");
        assertThat(response.getAutorId()).isEqualTo(7L);
        assertThat(response.isEncerrado()).isFalse();
        verify(topicoRepository).save(any(Topico.class));
    }

    @Test
    void criar_comTituloDuplicado_lancaRegraNegocio() {
        TopicoRequest request = new TopicoRequest();
        request.setTitulo("Como usar injeção de dependência no Spring");
        request.setDescricao("Estou com dúvida sobre @Autowired e construtor no Spring Boot");
        request.setCategoria("Java");
        when(topicoRepository.existsByTituloIgnoreCase(request.getTitulo())).thenReturn(true);

        assertThatThrownBy(() -> topicoService.criar(request, 7L))
                .isInstanceOf(RegraNegocioException.class);
        verify(topicoRepository, never()).save(any(Topico.class));
    }

    @Test
    void buscarPorId_quandoNaoExiste_lancaRecursoNaoEncontrado() {
        when(topicoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> topicoService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void encerrar_quandoNaoEhModerador_lancaAcessoNegado() {
        assertThatThrownBy(() -> topicoService.encerrar(1L, "ESTUDANTE"))
                .isInstanceOf(AcessoNegadoException.class);
        // nem chega a procurar o tópico se não tem permissão
        verify(topicoRepository, never()).findById(any());
    }

    @Test
    void encerrar_quandoEhModerador_marcaComoEncerrado() {
        Topico topico = Topico.builder()
                .id(1L).titulo("t").descricao("d").categoria("Java")
                .autorId(5L).criadoEm(LocalDateTime.now()).build();
        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));
        when(likeRepository.countByTipoAndReferenciaId(any(), any())).thenReturn(0L);
        when(comentarioRepository.countByTopicoId(any())).thenReturn(0L);

        TopicoResponse response = topicoService.encerrar(1L, "MODERADOR");

        assertThat(response.isEncerrado()).isTrue();
        verify(topicoRepository).save(topico);
    }

    @Test
    void listar_deveTrazerFixadosPrimeiro() {
        Topico normal = Topico.builder()
                .id(1L).titulo("normal").descricao("d").categoria("Java")
                .fixado(false).criadoEm(LocalDateTime.now().minusHours(1)).build();
        Topico fixado = Topico.builder()
                .id(2L).titulo("fixado").descricao("d").categoria("Java")
                .fixado(true).criadoEm(LocalDateTime.now().minusHours(2)).build();
        when(topicoRepository.findAll()).thenReturn(List.of(normal, fixado));
        when(likeRepository.countByTipoAndReferenciaId(any(), any())).thenReturn(0L);
        when(comentarioRepository.countByTopicoId(any())).thenReturn(0L);

        List<TopicoResponse> lista = topicoService.listar();

        assertThat(lista.get(0).isFixado()).isTrue();
        assertThat(lista.get(0).getTitulo()).isEqualTo("fixado");
    }
}
