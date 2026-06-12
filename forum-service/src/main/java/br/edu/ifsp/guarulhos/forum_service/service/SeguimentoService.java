package br.edu.ifsp.guarulhos.forum_service.service;

import br.edu.ifsp.guarulhos.forum_service.dto.response.TopicoResponse;
import br.edu.ifsp.guarulhos.forum_service.model.Seguimento;
import br.edu.ifsp.guarulhos.forum_service.model.Topico;
import br.edu.ifsp.guarulhos.forum_service.repository.SeguimentoRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.TopicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeguimentoService {

    private final SeguimentoRepository seguimentoRepository;
    private final TopicoRepository topicoRepository;
    private final TopicoService topicoService;

    /*
    * US-05 - seguir um tópico para acompanhar novidades. Se o usuário já segue,
    * não cria duplicado (a constraint do banco também garante isso).
    * */
    public void seguir(Long topicoId, Long usuarioId){
        Topico topico = topicoRepository.findById(topicoId)
                .orElseThrow(() -> new RuntimeException("Tópico não encontrado"));

        seguimentoRepository.findByUsuarioIdAndTopicoId(usuarioId, topicoId)
                .ifPresent(s -> { throw new RuntimeException("Você já segue este tópico"); });

        Seguimento seguimento = Seguimento.builder()
                .usuarioId(usuarioId)
                .topico(topico)
                .build();
        seguimentoRepository.save(seguimento);
    }

    /*
    * US-05 - deixar de seguir um tópico.
    * */
    public void deixarDeSeguir(Long topicoId, Long usuarioId){
        Seguimento seguimento = seguimentoRepository.findByUsuarioIdAndTopicoId(usuarioId, topicoId)
                .orElseThrow(() -> new RuntimeException("Você não segue este tópico"));
        seguimentoRepository.delete(seguimento);
    }

    /*
    * US-06 - listar os tópicos que o usuário segue.
    * */
    public List<TopicoResponse> topicosSeguidos(Long usuarioId){
        return seguimentoRepository.findByUsuarioId(usuarioId).stream()
                .map(Seguimento::getTopico)
                .map(topicoService::montarResponse)
                .toList();
    }
}
