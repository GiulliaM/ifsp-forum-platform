package br.edu.ifsp.guarulhos.forum_service.service;

import br.edu.ifsp.guarulhos.forum_service.dto.response.TopicoResponse;
import br.edu.ifsp.guarulhos.forum_service.model.Topico;
import br.edu.ifsp.guarulhos.forum_service.repository.ComentarioRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.TopicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SugestaoService {

    private final TopicoRepository topicoRepository;
    private final ComentarioRepository comentarioRepository;
    private final TopicoService topicoService;

    /*
    * US-13 - sugestões baseadas nas categorias em que o usuário já participou
    * (criou tópico ou comentou). Para novos usuários sem histórico, retorna
    * os 10 tópicos mais recentes em aberto.
    * */
    public List<TopicoResponse> sugeridos(Long usuarioId) {
        Set<String> categorias = new HashSet<>();
        categorias.addAll(topicoRepository.findCategoriasByAutorId(usuarioId));
        categorias.addAll(comentarioRepository.findCategoriasByComentadorId(usuarioId));

        if (categorias.isEmpty()) {
            return topicoRepository.findAll().stream()
                    .filter(t -> !t.isEncerrado())
                    .sorted(Comparator.comparing(Topico::getCriadoEm).reversed())
                    .limit(10)
                    .map(topicoService::montarResponse)
                    .toList();
        }

        return categorias.stream()
                .flatMap(cat -> topicoRepository.findByCategoria(cat).stream())
                .filter(t -> !t.isEncerrado() && !t.getAutorId().equals(usuarioId))
                .distinct()
                .sorted(Comparator.comparing(Topico::getCriadoEm).reversed())
                .limit(10)
                .map(topicoService::montarResponse)
                .toList();
    }
}
