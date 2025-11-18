package com.codeUp.demo.controller;

import com.codeUp.demo.RespostaPadrao;
import com.codeUp.demo.dto.MensagemDTO;
import com.codeUp.demo.model.Mensagem;
import com.codeUp.demo.service.MensagemService;
import com.codeUp.demo.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final MensagemService mensagemService;
    private final UsuarioService usuarioService;

    public ChatController(MensagemService mensagemService, UsuarioService usuarioService) {
        this.mensagemService = mensagemService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/enviar")
    public ResponseEntity<?> enviar(@RequestBody MensagemDTO dto) {

        var remetente = usuarioService.findById(dto.getRemetenteId());
        var destinatario = usuarioService.findById(dto.getDestinatarioId());

        if (remetente.isEmpty() || destinatario.isEmpty())
            return ResponseEntity.badRequest().body("Usuário inválido");

        Mensagem msg = new Mensagem(
                remetente.get(),
                destinatario.get(),
                dto.getConteudo()
        );

        Mensagem enviada = mensagemService.enviar(msg);

        return ResponseEntity.ok("Mensagem enviada");
    }

    @GetMapping("/conversa")
    public ResponseEntity<?> conversa(
            @RequestParam Long usuario1,
            @RequestParam Long usuario2
    ){
        var lista = mensagemService.conversa(usuario1, usuario2);

        return ResponseEntity.ok(lista.stream().map(msg -> {
            MensagemDTO dto = new MensagemDTO();
            dto.setId(msg.getId());
            dto.setConteudo(msg.getConteudo());
            dto.setEnviadoEm(msg.getEnviadoEm());
            dto.setRemetenteId(msg.getRemetente().getId());
            dto.setDestinatarioId(msg.getDestinatario().getId());
            return dto;
        }).collect(Collectors.toList()));
    }

    @GetMapping("/conversas-recentes/{usuarioId}")
    public ResponseEntity<?> conversasRecentes(@PathVariable Long usuarioId) {

        var usuario = usuarioService.findById(usuarioId);
        if (usuario.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Usuário inválido");
        }

        var conversas = mensagemService.conversasRecentes(usuarioId);

        return ResponseEntity.ok(
                new RespostaPadrao<>(true, "Conversas carregadas", conversas)
        );
    }

}
