package com.codeUp.demo.controller;

import com.codeUp.demo.RespostaPadrao;
import com.codeUp.demo.dto.LoginDTO;
import com.codeUp.demo.dto.UsuarioDTO;
import com.codeUp.demo.model.Usuario;
import com.codeUp.demo.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginDTO dto){

        if(dto.getEmail() == null || dto.getSenha() == null){
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Email e senha são obrigatórios", null));
        }

        var usuario = usuarioService.findByEmail(dto.getEmail());

        if(usuario.isEmpty()){
            return ResponseEntity.status(404)
                    .body(new RespostaPadrao<>(false, "Usuário não encontrado", null));
        }

        if(!usuario.get().getSenha().equals(dto.getSenha())){
            return ResponseEntity.status(401)
                    .body(new RespostaPadrao<>(false, "Senha incorreta", null));
        }

        Usuario u = usuario.get();

        UsuarioDTO resposta = new UsuarioDTO(u.getId(), u.getNome(), u.getEmail());

        return ResponseEntity.ok(
                new RespostaPadrao<>(true, "Login realizado", resposta)
        );
    }
}
