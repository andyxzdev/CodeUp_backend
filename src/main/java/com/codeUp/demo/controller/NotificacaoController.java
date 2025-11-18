package com.codeUp.demo.controller;

import com.codeUp.demo.RespostaPadrao;
import com.codeUp.demo.dto.NotificacaoDTO;
import com.codeUp.demo.model.Notificacao;
import com.codeUp.demo.service.NotificacaoService;
import com.codeUp.demo.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;
    private final UsuarioService usuarioService;

    public NotificacaoController(NotificacaoService notificacaoService, UsuarioService usuarioService) {
        this.notificacaoService = notificacaoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<?> listar(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        var usuario = usuarioService.findById(userId);
        if (usuario.isEmpty())
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Usuário inválido", null));

        var lista = notificacaoService.listarNotificacoes(usuario.get())
                .stream()
                .map(NotificacaoDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new RespostaPadrao<>(true, "Notificações carregadas", lista)
        );
    }

    @PostMapping("/{id}/lida")
    public ResponseEntity<?> marcarComoLida(@PathVariable Long id) {

        notificacaoService.marcarComoLida(id);

        return ResponseEntity.ok(
                new RespostaPadrao<>(true, "Notificação marcada como lida", null)
        );
    }
}
