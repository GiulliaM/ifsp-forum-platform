package br.edu.ifsp.guarulhos.forum_service.service;

import br.edu.ifsp.guarulhos.forum_service.dto.request.ComentarioRequest;
import br.edu.ifsp.guarulhos.forum_service.dto.response.ComentarioResponse;
import br.edu.ifsp.guarulhos.forum_service.model.Comentario;
import br.edu.ifsp.guarulhos.forum_service.model.Topico;
import br.edu.ifsp.guarulhos.forum_service.model.enums.TipoLike;
import br.edu.ifsp.guarulhos.forum_service.repository.ComentarioRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.LikeRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.TopicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    // Janela de tempo em que o autor ainda pode editar ou excluir o comentário
    private static final long MINUTOS_PARA_EDITAR = 30;

    private final ComentarioRepository comentarioRepository;
    private final TopicoRepository topicoRepository;
    private final LikeRepository likeRepository;

    /*
    * US-02 - comentar em um tópico. Se vier parentId, é uma resposta a outro
    * comentário (thread). Não deixa comentar em tópico encerrado.
    * */
    public ComentarioResponse criar(Long topicoId, ComentarioRequest request, Long autorId){
        Topico topico = topicoRepository.findById(topicoId)
                .orElseThrow(() -> new RuntimeException("Tópico não encontrado"));

        if(topico.isEncerrado()){
            throw new RuntimeException("Este tópico está encerrado e não aceita novos comentários");
        }

        Comentario comentario = Comentario.builder()
                .conteudo(request.getConteudo())
                .topico(topico)
                .autorId(autorId)
                .build();

        if(request.getParentId() != null){
            Comentario parent = comentarioRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Comentário pai não encontrado"));
            comentario.setParent(parent);
        }

        comentarioRepository.save(comentario);
        return montarResponse(comentario);
    }

    public List<ComentarioResponse> listarPorTopico(Long topicoId){
        return comentarioRepository.findByTopicoIdOrderByCriadoEmAsc(topicoId).stream()
                .map(this::montarResponse)
                .toList();
    }

    /*
    * US-02 - editar o próprio comentário, dentro do limite de 30 minutos.
    * */
    public ComentarioResponse editar(Long id, ComentarioRequest request, Long autorId){
        Comentario comentario = buscarValidandoAutor(id, autorId);
        validarPrazo(comentario);

        comentario.setConteudo(request.getConteudo());
        comentario.setEditadoEm(LocalDateTime.now());
        comentarioRepository.save(comentario);
        return montarResponse(comentario);
    }

    /*
    * US-02 - excluir o próprio comentário, dentro do limite de 30 minutos.
    * */
    public void deletar(Long id, Long autorId){
        Comentario comentario = buscarValidandoAutor(id, autorId);
        validarPrazo(comentario);
        comentarioRepository.delete(comentario);
    }

    private Comentario buscarValidandoAutor(Long id, Long autorId){
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado"));
        if(!comentario.getAutorId().equals(autorId)){
            throw new RuntimeException("Você só pode alterar os seus próprios comentários");
        }
        return comentario;
    }

    private void validarPrazo(Comentario comentario){
        long minutos = Duration.between(comentario.getCriadoEm(), LocalDateTime.now()).toMinutes();
        if(minutos >= MINUTOS_PARA_EDITAR){
            throw new RuntimeException("O prazo de 30 minutos para alterar o comentário já passou");
        }
    }

    private ComentarioResponse montarResponse(Comentario comentario){
        long totalLikes = likeRepository.countByTipoAndReferenciaId(TipoLike.COMENTARIO, comentario.getId());

        return ComentarioResponse.builder()
                .id(comentario.getId())
                .conteudo(comentario.getConteudo())
                .autorId(comentario.getAutorId())
                .parentId(comentario.getParent() != null ? comentario.getParent().getId() : null)
                .totalLikes(totalLikes)
                .criadoEm(comentario.getCriadoEm())
                .editadoEm(comentario.getEditadoEm())
                .build();
    }
}
