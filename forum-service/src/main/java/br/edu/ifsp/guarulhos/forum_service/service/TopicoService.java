package br.edu.ifsp.guarulhos.forum_service.service;

import br.edu.ifsp.guarulhos.forum_service.dto.request.TopicoRequest;
import br.edu.ifsp.guarulhos.forum_service.dto.response.TopicoResponse;
import br.edu.ifsp.guarulhos.forum_service.exception.AcessoNegadoException;
import br.edu.ifsp.guarulhos.forum_service.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.guarulhos.forum_service.exception.RegraNegocioException;
import br.edu.ifsp.guarulhos.forum_service.model.Topico;
import br.edu.ifsp.guarulhos.forum_service.model.enums.TipoLike;
import br.edu.ifsp.guarulhos.forum_service.repository.ComentarioRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.LikeRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.TopicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicoService {

    private final TopicoRepository topicoRepository;
    private final ComentarioRepository comentarioRepository;
    private final LikeRepository likeRepository;
    private final PontuacaoService pontuacaoService;

    /*
    * US-01 - criar um novo tópico de discussão. O autor vem do header X-User-Id
    * que o gateway injeta depois de validar o JWT.
    * */
    public TopicoResponse criar(TopicoRequest request, Long autorId){
        if(topicoRepository.existsByTituloIgnoreCase(request.getTitulo())){
            throw new RegraNegocioException("Já existe um tópico com este título");
        }

        Topico topico = Topico.builder()
                .titulo(request.getTitulo())
                .descricao(request.getDescricao())
                .categoria(request.getCategoria())
                .autorId(autorId)
                .fixado(false)
                .encerrado(false)
                .imageUrl(request.getImageUrl())
                .build();

        topicoRepository.save(topico);
        pontuacaoService.registrarTopico(autorId, topico.getId());
        return montarResponse(topico);
    }

    /*
    * US-01 - listar tópicos. Os fixados aparecem primeiro, depois os mais recentes.
    * */
    public List<TopicoResponse> listar(){
        return topicoRepository.findAll().stream()
                .sorted(Comparator.comparing(Topico::isFixado).reversed()
                        .thenComparing(Topico::getCriadoEm, Comparator.reverseOrder()))
                .map(this::montarResponse)
                .toList();
    }

    public TopicoResponse buscarPorId(Long id){
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tópico não encontrado"));
        return montarResponse(topico);
    }

    /*
    * US-04 - moderação: encerrar discussão. Só moderador pode.
    * */
    public TopicoResponse encerrar(Long id, String perfil){
        validarModerador(perfil);
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tópico não encontrado"));
        topico.setEncerrado(true);
        topicoRepository.save(topico);
        return montarResponse(topico);
    }

    /*
    * US-04 - moderação: fixar/desafixar tópico. Só moderador pode.
    * */
    public TopicoResponse fixar(Long id, String perfil){
        validarModerador(perfil);
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tópico não encontrado"));
        topico.setFixado(!topico.isFixado());
        topicoRepository.save(topico);
        return montarResponse(topico);
    }

    /*
    * US-04 - moderação: excluir tópico. Só moderador pode.
    * */
    public void deletar(Long id, String perfil){
        validarModerador(perfil);
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tópico não encontrado"));
        topicoRepository.delete(topico);
    }

    private void validarModerador(String perfil){
        if(!"MODERADOR".equals(perfil)){
            throw new AcessoNegadoException("Apenas moderadores podem realizar esta ação");
        }
    }

    /*
    * Monta o response juntando as contagens de likes e comentários do tópico.
    * É public porque o SeguimentoService também usa pra montar a lista de seguidos.
    * */
    public TopicoResponse montarResponse(Topico topico){
        long totalLikes = likeRepository.countByTipoAndReferenciaId(TipoLike.TOPICO, topico.getId());
        long totalComentarios = comentarioRepository.countByTopicoId(topico.getId());

        return TopicoResponse.builder()
                .id(topico.getId())
                .titulo(topico.getTitulo())
                .descricao(topico.getDescricao())
                .categoria(topico.getCategoria())
                .autorId(topico.getAutorId())
                .fixado(topico.isFixado())
                .encerrado(topico.isEncerrado())
                .totalLikes(totalLikes)
                .totalComentarios(totalComentarios)
                .criadoEm(topico.getCriadoEm())
                .imageUrl(topico.getImageUrl())
                .build();
    }
}
