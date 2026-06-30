package br.edu.ifsp.guarulhos.forum_service.controller;

import br.edu.ifsp.guarulhos.forum_service.dto.request.ComentarioRequest;
import br.edu.ifsp.guarulhos.forum_service.dto.response.ComentarioResponse;
import br.edu.ifsp.guarulhos.forum_service.service.ComentarioService;
import br.edu.ifsp.guarulhos.forum_service.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final LikeService likeService;

    @PutMapping("/{id}")
    public ResponseEntity<ComentarioResponse> editar(@PathVariable Long id,
                                                     @Valid @RequestBody ComentarioRequest request,
                                                     @RequestHeader("X-User-Id") Long usuarioId){
        return ResponseEntity.ok(comentarioService.editar(id, request, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id,
                                        @RequestHeader("X-User-Id") Long usuarioId){
        comentarioService.deletar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Long> curtir(@PathVariable Long id,
                                       @RequestHeader("X-User-Id") Long usuarioId){
        return ResponseEntity.ok(likeService.alternarLikeComentario(id, usuarioId));
    }
}
