package br.edu.ifsp.guarulhos.forum_service.controller;

import br.edu.ifsp.guarulhos.forum_service.dto.request.ComentarioRequest;
import br.edu.ifsp.guarulhos.forum_service.dto.request.TopicoRequest;
import br.edu.ifsp.guarulhos.forum_service.dto.response.ComentarioResponse;
import br.edu.ifsp.guarulhos.forum_service.dto.response.TopicoResponse;
import br.edu.ifsp.guarulhos.forum_service.service.ComentarioService;
import br.edu.ifsp.guarulhos.forum_service.service.LikeService;
import br.edu.ifsp.guarulhos.forum_service.service.SeguimentoService;
import br.edu.ifsp.guarulhos.forum_service.service.TopicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topicos")
@RequiredArgsConstructor
public class TopicoController {

    private final TopicoService topicoService;
    private final ComentarioService comentarioService;
    private final LikeService likeService;
    private final SeguimentoService seguimentoService;

    // US-01 - criar tópico
    @PostMapping
    public ResponseEntity<TopicoResponse> criar(@Valid @RequestBody TopicoRequest request,
                                                @RequestHeader("X-User-Id") Long usuarioId){
        TopicoResponse response = topicoService.criar(request, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // US-01 - listar tópicos (público)
    @GetMapping
    public ResponseEntity<List<TopicoResponse>> listar(){
        return ResponseEntity.ok(topicoService.listar());
    }

    // US-01 - detalhar um tópico (público)
    @GetMapping("/{id}")
    public ResponseEntity<TopicoResponse> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(topicoService.buscarPorId(id));
    }

    // US-02 - comentar em um tópico
    @PostMapping("/{id}/comentarios")
    public ResponseEntity<ComentarioResponse> comentar(@PathVariable Long id,
                                                       @Valid @RequestBody ComentarioRequest request,
                                                       @RequestHeader("X-User-Id") Long usuarioId){
        ComentarioResponse response = comentarioService.criar(id, request, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // US-02 - listar comentários de um tópico (público)
    @GetMapping("/{id}/comentarios")
    public ResponseEntity<List<ComentarioResponse>> listarComentarios(@PathVariable Long id){
        return ResponseEntity.ok(comentarioService.listarPorTopico(id));
    }

    // US-03 - curtir/descurtir um tópico
    @PostMapping("/{id}/like")
    public ResponseEntity<Long> curtir(@PathVariable Long id,
                                       @RequestHeader("X-User-Id") Long usuarioId){
        return ResponseEntity.ok(likeService.alternarLikeTopico(id, usuarioId));
    }

    // US-04 - moderação: encerrar tópico
    @PatchMapping("/{id}/encerrar")
    public ResponseEntity<TopicoResponse> encerrar(@PathVariable Long id,
                                                   @RequestHeader("X-User-Role") String perfil){
        return ResponseEntity.ok(topicoService.encerrar(id, perfil));
    }

    // US-04 - moderação: fixar/desafixar tópico
    @PatchMapping("/{id}/fixar")
    public ResponseEntity<TopicoResponse> fixar(@PathVariable Long id,
                                                @RequestHeader("X-User-Role") String perfil){
        return ResponseEntity.ok(topicoService.fixar(id, perfil));
    }

    // US-04 - moderação: excluir tópico
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id,
                                        @RequestHeader("X-User-Role") String perfil){
        topicoService.deletar(id, perfil);
        return ResponseEntity.noContent().build();
    }

    // US-05 - seguir tópico
    @PostMapping("/{id}/seguir")
    public ResponseEntity<Void> seguir(@PathVariable Long id,
                                       @RequestHeader("X-User-Id") Long usuarioId){
        seguimentoService.seguir(id, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // US-05 - deixar de seguir tópico
    @DeleteMapping("/{id}/seguir")
    public ResponseEntity<Void> deixarDeSeguir(@PathVariable Long id,
                                               @RequestHeader("X-User-Id") Long usuarioId){
        seguimentoService.deixarDeSeguir(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
