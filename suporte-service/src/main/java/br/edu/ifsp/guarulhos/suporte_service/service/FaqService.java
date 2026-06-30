package br.edu.ifsp.guarulhos.suporte_service.service;

import br.edu.ifsp.guarulhos.suporte_service.dto.response.FaqResponse;
import br.edu.ifsp.guarulhos.suporte_service.repository.FaqEntradaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqEntradaRepository faqEntradaRepository;

    public List<FaqResponse> listar(){
        return faqEntradaRepository.findAll().stream()
                .map(f -> FaqResponse.builder()
                        .pergunta(f.getPergunta())
                        .resposta(f.getResposta())
                        .build())
                .toList();
    }
}
