package com.codeUp.demo.controller;

import com.codeUp.demo.RespostaPadrao;
import com.codeUp.demo.dto.LoginDTO;
import com.codeUp.demo.dto.UsuarioDTO;
import com.codeUp.demo.security.JwtUtil;
import com.codeUp.demo.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

        var usuarioOpt = usuarioService.findByEmail(dto.getEmail());

        if (usuarioOpt.isEmpty() ||
                !usuarioOpt.get().getSenha().equals(dto.getSenha())) {

            return ResponseEntity.status(401)
                    .body(new RespostaPadrao<>(false, "Credenciais inválidas", null));
        }

        var usuario = usuarioOpt.get();

        // 🔑 Gerar token JWT
        String token = jwtUtil.gerarToken(usuario.getId(), usuario.getEmail());

        // 📦 Montar objeto usuario para o front
        Map<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("id", usuario.getId());
        usuarioMap.put("nome", usuario.getNome());
        usuarioMap.put("email", usuario.getEmail());
        usuarioMap.put("bio", usuario.getBio());
        usuarioMap.put("fotoPerfil", usuario.getFotoPerfil());

        // 📦 Resposta dentro de "dados"
        Map<String, Object> dados = new HashMap<>();
        dados.put("token", token);
        dados.put("usuario", usuarioMap);

        return ResponseEntity.ok(
                new RespostaPadrao<>(true, "Login bem-sucedido", dados)
        );
    }
}
