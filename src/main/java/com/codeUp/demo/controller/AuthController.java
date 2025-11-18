package com.codeUp.demo.controller;

import com.codeUp.demo.RespostaPadrao;
import com.codeUp.demo.dto.LoginDTO;
import com.codeUp.demo.dto.UsuarioDTO;
import com.codeUp.demo.security.JwtUtil;
import com.codeUp.demo.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {

        var usuario = usuarioService.findByEmail(dto.getEmail());

        if (usuario.isEmpty() ||
                !usuario.get().getSenha().equals(dto.getSenha())) {

            return ResponseEntity.status(401)
                    .body(new RespostaPadrao<>(false, "Credenciais inválidas", null));
        }

        var user = usuario.get();
        String token = jwtUtil.gerarToken(user.getId(), user.getEmail());

        UsuarioDTO userDTO = new UsuarioDTO(user.getId(), user.getNome(), user.getEmail());

        return ResponseEntity.ok(
                new RespostaPadrao<>(true, "Login bem-sucedido",
                        new Object() {
                            public String token;
                            public UsuarioDTO usuario = userDTO;
                        })
        );
    }
}
