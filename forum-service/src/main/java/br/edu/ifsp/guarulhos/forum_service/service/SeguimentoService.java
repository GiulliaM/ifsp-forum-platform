package br.edu.ifsp.guarulhos.forum_service.service;

import br.edu.ifsp.guarulhos.forum_service.dto.response.TopicoResponse;
import br.edu.ifsp.guarulhos.forum_service.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.guarulhos.forum_service.exception.RegraNegocioException;
import br.edu.ifsp.guarulhos.forum_service.model.Seguimento;
import br.edu.ifsp.guarulhos.forum_service.model.Topico;
import br.edu.ifsp.guarulhos.forum_service.repository.ComentarioRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.SeguimentoRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.TopicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeguimentoService {

    private final SeguimentoRepository seguimentoRepository;
    private final TopicoRepository topicoRepository;
    private final ComentarioRepository comentarioRepository;
    private final TopicoService topicoService;

    // a constraint do banco também garante que não haja seguimento duplicado
    public void seguir(Long topicoId, Long usuarioId){
        Topico topico = topicoRepository.findById(topicoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tópico não encontrado"));

        seguimentoRepository.findByUsuarioIdAndTopicoId(usuarioId, topicoId)
                .ifPresent(s -> { throw new RegraNegocioException("Você já segue este tópico"); });

        Seguimento seguimento = Seguimento.builder()
                .usuarioId(usuarioId)
                .topico(topico)
                .build();
        seguimentoRepository.save(seguimento);
    }

    public void deixarDeSeguir(Long topicoId, Long usuarioId){
        Seguimento seguimento = seguimentoRepository.findByUsuarioIdAndTopicoId(usuarioId, topicoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Você não segue este tópico"));
        seguimentoRepository.delete(seguimento);
    }

    public List<TopicoResponse> topicosSeguidos(Long usuarioId, String categoria, String ordem){
        Comparator<TopicoResponse> porData = Comparator.comparing(
                TopicoResponse::getCriadoEm, Comparator.nullsLast(Comparator.naturalOrder()));
        if(!"asc".equalsIgnoreCase(ordem)){
            porData = porData.reversed();
        }

        return seguimentoRepository.findByUsuarioId(usuarioId).stream()
                .filter(seguimento -> categoria == null || categoria.isBlank()
                        || categoria.equalsIgnoreCase(seguimento.getTopico().getCategoria()))
                .map(seguimento -> {
                    Topico topico = seguimento.getTopico();
                    TopicoResponse response = topicoService.montarResponse(topico);
                    boolean novidades = seguimento.getSeguidoEm() != null
                            && comentarioRepository.existsByTopicoIdAndCriadoEmAfter(
                                    topico.getId(), seguimento.getSeguidoEm());
                    response.setTemNovidades(novidades);
                    return response;
                })
                .sorted(porData)
                .toList();
    }
}
