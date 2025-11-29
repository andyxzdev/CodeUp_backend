package com.codeUp.demo.controller;

import com.codeUp.demo.RespostaPadrao;
import com.codeUp.demo.dto.ComentarioDTO;
import com.codeUp.demo.model.Comentario;
import com.codeUp.demo.service.ComentarioService;
import com.codeUp.demo.service.NotificacaoService;
import com.codeUp.demo.service.PublicacaoService;
import com.codeUp.demo.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final UsuarioService usuarioService;
    private final PublicacaoService publicacaoService;
    private final NotificacaoService notificacaoService;

    public ComentarioController(ComentarioService comentarioService,
                                UsuarioService usuarioService,
                                PublicacaoService publicacaoService,
                                NotificacaoService notificacaoService) {
        this.comentarioService = comentarioService;
        this.usuarioService = usuarioService;
        this.publicacaoService = publicacaoService;
        this.notificacaoService = notificacaoService;
    }

    // Criar comentário (AUTENTICADO)
    @PostMapping
    public ResponseEntity<?> criar(
            HttpServletRequest request,
            @Valid @RequestBody ComentarioDTO dto) {

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null)
            return ResponseEntity.status(401)
                    .body(new RespostaPadrao<>(false, "Token inválido ou ausente", null));

        var usuario = usuarioService.findById(userId);
        if (usuario.isEmpty())
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Usuário inválido", null));

        var publicacao = publicacaoService.findById(dto.getPublicacaoId());
        if (publicacao.isEmpty())
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Publicação inválida", null));

        Comentario comentario = new Comentario(
                dto.getConteudo(),
                usuario.get(),
                publicacao.get()
        );
        comentario.setCreatedAt(LocalDateTime.now());

        Comentario salvo = comentarioService.criar(comentario);

        // 🔥 Notificação de comentário
        if (!usuario.get().getId().equals(publicacao.get().getAuthor().getId())) {
            notificacaoService.criarNotificacao(
                    publicacao.get().getAuthor(),
                    usuario.get().getNome() + " comentou: \"" + dto.getConteudo() + "\"",
                    "comentario",
                    publicacao.get().getId()
            );
        }

        return ResponseEntity.ok(
                new RespostaPadrao<>(true, "Comentário criado", toDTO(salvo))
        );
    }

    // Listar comentários de publicação
    @GetMapping("/publicacao/{publicacaoId}")
    public ResponseEntity<?> listar(@PathVariable long publicacaoId) {

        var publicacao = publicacaoService.findById(publicacaoId);
        if (publicacao.isEmpty())
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Publicação não encontrada", null));

        var lista = comentarioService.listarPorPublicacao(publicacaoId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new RespostaPadrao<>(true, "Comentários carregados", lista)
        );
    }

    private ComentarioDTO toDTO(Comentario c) {
        ComentarioDTO dto = new ComentarioDTO();
        dto.setId(c.getId());
        dto.setConteudo(c.getConteudo());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setAuthorId(c.getAuthor().getId());
        dto.setAuthorName(c.getAuthor().getNome());
        dto.setPublicacaoId(c.getPublicacao().getId());
        return dto;
    }
}
