package br.edu.ifsp.guarulhos.suporte_service.service;

import br.edu.ifsp.guarulhos.suporte_service.dto.response.FaqResponse;
import br.edu.ifsp.guarulhos.suporte_service.model.FaqEntrada;
import br.edu.ifsp.guarulhos.suporte_service.repository.FaqEntradaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FaqServiceTest {

    @Mock
    private FaqEntradaRepository faqEntradaRepository;
    @InjectMocks
    private FaqService faqService;

    @Test
    void listar_retornaTodasAsEntradas(){
        FaqEntrada entrada = FaqEntrada.builder().id(1L)
                .pergunta("Como recupero minha senha?")
                .resposta("Use a opção 'Esqueci minha senha' na tela de login.")
                .build();
        when(faqEntradaRepository.findAll()).thenReturn(List.of(entrada));

        List<FaqResponse> resultado = faqService.listar();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPergunta()).isEqualTo("Como recupero minha senha?");
    }
}
