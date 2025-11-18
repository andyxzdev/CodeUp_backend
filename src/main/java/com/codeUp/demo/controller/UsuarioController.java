package com.codeUp.demo.controller;

import com.codeUp.demo.RespostaPadrao;
import com.codeUp.demo.dto.PublicacaoDTO;
import com.codeUp.demo.dto.UsuarioDTO;
import com.codeUp.demo.dto.UsuarioUpdateDTO;
import com.codeUp.demo.model.Usuario;
import com.codeUp.demo.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService service) {
        this.usuarioService = service;
    }

    // Criando usuário
    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody UsuarioDTO dto) {
        try {
            Usuario usuario = new Usuario(dto.getNome(), dto.getEmail(), dto.getSenha());
            Usuario criado = usuarioService.criar(usuario);

            Map<String, Object> usuarioResponse = new HashMap<>();
            usuarioResponse.put("id", criado.getId());
            usuarioResponse.put("nome", criado.getNome());
            usuarioResponse.put("email", criado.getEmail());
            usuarioResponse.put("bio", criado.getBio());
            usuarioResponse.put("fotoPerfil", criado.getFotoPerfil());

            // ✅ CORRIGIDO: Barra antes do ID
            return ResponseEntity.created(URI.create("/api/usuarios/" + criado.getId()))
                    .body(new RespostaPadrao<>(true, "Usuário criado com sucesso", usuarioResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Erro ao criar usuário: " + e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<?> all() {
        try {
            List<Map<String, Object>> usuarios = usuarioService.findAll().stream()
                    .map(u -> {
                        Map<String, Object> usuarioMap = new HashMap<>();
                        usuarioMap.put("id", u.getId());
                        usuarioMap.put("nome", u.getNome());
                        usuarioMap.put("email", u.getEmail());
                        usuarioMap.put("bio", u.getBio());
                        usuarioMap.put("fotoPerfil", u.getFotoPerfil());
                        return usuarioMap;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new RespostaPadrao<>(true, "Usuários carregados", usuarios));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Erro ao carregar usuários", null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return usuarioService.findById(id)
                .map(u -> {
                    Map<String, Object> usuarioCompleto = new HashMap<>();
                    usuarioCompleto.put("id", u.getId());
                    usuarioCompleto.put("nome", u.getNome());
                    usuarioCompleto.put("email", u.getEmail());
                    usuarioCompleto.put("bio", u.getBio());
                    usuarioCompleto.put("fotoPerfil", u.getFotoPerfil());

                    // ✅ PADRONIZADO: Usando RespostaPadrao
                    return ResponseEntity.ok(new RespostaPadrao<>(true, "Perfil carregado com sucesso", usuarioCompleto));
                })
                .orElse(ResponseEntity.status(404)
                        .body(new RespostaPadrao<>(false, "Usuário não encontrado", null)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        try {
            return usuarioService.findById(id).map(existing -> {
                existing.setNome(dto.getNome() != null ? dto.getNome() : existing.getNome());
                existing.setEmail(dto.getEmail() != null ? dto.getEmail() : existing.getEmail());
                if (dto.getSenha() != null) existing.setSenha(dto.getSenha());

                Usuario salvo = usuarioService.atualizar(existing);

                Map<String, Object> usuarioResponse = new HashMap<>();
                usuarioResponse.put("id", salvo.getId());
                usuarioResponse.put("nome", salvo.getNome());
                usuarioResponse.put("email", salvo.getEmail());
                usuarioResponse.put("bio", salvo.getBio());
                usuarioResponse.put("fotoPerfil", salvo.getFotoPerfil());

                return ResponseEntity.ok(new RespostaPadrao<>(true, "Usuário atualizado com sucesso", usuarioResponse));
            }).orElse(ResponseEntity.status(404)
                    .body(new RespostaPadrao<>(false, "Usuário não encontrado", null)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Erro ao atualizar usuário", null));
        }
    }

    @GetMapping("/{id}/salvos")
    public ResponseEntity<?> listarSalvos(@PathVariable Long id) {
        try {
            var usuario = usuarioService.findById(id);

            if (usuario.isEmpty())
                return ResponseEntity.badRequest()
                        .body(new RespostaPadrao<>(false, "Usuário inválido", null));

            var lista = usuario.get().getPublicacoesSalvas()
                    .stream()
                    .map(publicacao -> new PublicacaoDTO(
                            publicacao.getId(),
                            publicacao.getConteudo(),
                            publicacao.getCreatedAt(),
                            publicacao.getCurtidasCount(),
                            publicacao.getAuthor().getId(),
                            publicacao.getAuthor().getNome()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    new RespostaPadrao<>(true, "Publicações salvas carregadas", lista)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Erro ao carregar publicações salvas", null));
        }
    }

    @PutMapping("/{id}/perfil")
    public ResponseEntity<?> atualizarPerfil(@PathVariable Long id, @RequestBody UsuarioUpdateDTO dto) {
        try {
            var usuarioOpt = usuarioService.findById(id);

            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new RespostaPadrao<>(false, "Usuário não encontrado", null));
            }

            Usuario usuario = usuarioOpt.get();

            // Atualizar apenas o que foi enviado
            if (dto.getNome() != null) usuario.setNome(dto.getNome());
            if (dto.getEmail() != null) usuario.setEmail(dto.getEmail());
            if (dto.getBio() != null) usuario.setBio(dto.getBio());
            if (dto.getFotoPerfil() != null) usuario.setFotoPerfil(dto.getFotoPerfil());
            if (dto.getSenha() != null) usuario.setSenha(dto.getSenha());

            Usuario salvo = usuarioService.atualizar(usuario);

            Map<String, Object> usuarioResponse = new HashMap<>();
            usuarioResponse.put("id", salvo.getId());
            usuarioResponse.put("nome", salvo.getNome());
            usuarioResponse.put("email", salvo.getEmail());
            usuarioResponse.put("bio", salvo.getBio());
            usuarioResponse.put("fotoPerfil", salvo.getFotoPerfil());

            return ResponseEntity.ok(new RespostaPadrao<>(true, "Perfil atualizado com sucesso", usuarioResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Erro ao atualizar perfil", null));
        }
    }
}