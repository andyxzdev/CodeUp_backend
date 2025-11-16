package com.codeUp.demo.controller;

import com.codeUp.demo.RespostaPadrao;
import com.codeUp.demo.dto.ComentarioDTO;
import com.codeUp.demo.model.Comentario;
import com.codeUp.demo.service.ComentarioService;
import com.codeUp.demo.service.PublicacaoService;
import com.codeUp.demo.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("api/comentarios")
public class ComentarioController {
    private final ComentarioService comentarioService;
    private final UsuarioService usuarioService;
    private final PublicacaoService publicacaoService;

    public ComentarioController(ComentarioService comentarioService, UsuarioService usuarioService, PublicacaoService publicacaoService){
        this.comentarioService = comentarioService;
        this.usuarioService = usuarioService;
        this.publicacaoService = publicacaoService;
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody ComentarioDTO dto){
        var usuario = usuarioService.findById(dto.getAuthorId());
        if (usuario.isEmpty()) return ResponseEntity.badRequest()
                .body(new RespostaPadrao<>(false, "Usuário Invalido", null));

        var publicacao = publicacaoService.findById(dto.getPublicacaoId());
        if (publicacao.isEmpty())
            return ResponseEntity.badRequest().body(new RespostaPadrao<>(false, "Publicacao inválida", null));

        Comentario comentario = new Comentario(dto.getConteudo(), usuario.get(), publicacao.get());
        Comentario salvo = comentarioService.criar(comentario);

        return ResponseEntity.ok(new RespostaPadrao<>(
                true,"Comentário criado", null));
    }

    @GetMapping("/publicacao/{publicacaoId}")
    public ResponseEntity<?> listar(@PathVariable long publicacaoId){
        var lista = comentarioService.listarPorPublicacao(publicacaoId)
                .stream().map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new RespostaPadrao<>(
                true, "Comentarios carregados", null));
    }

    private ComentarioDTO toDTO(Comentario comentario){
        ComentarioDTO dto = new ComentarioDTO();
        dto.setId(comentario.getId());
        dto.setConteudo(comentario.getConteudo());
        dto.setCreatedAt(comentario.getCreatedAt());
        dto.setAuthorId(comentario.getAuthor().getId());
        dto.setAuthorName(comentario.getAuthor().getNome());
        dto.setPublicacaoId(comentario.getPublicacao().getId());

        return dto;
    }

}
