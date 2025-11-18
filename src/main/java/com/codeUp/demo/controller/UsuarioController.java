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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios/")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService service) {
        this.usuarioService = service;
    }

    //Criando usuario
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioDTO> registrar(@RequestBody UsuarioDTO dto){
        Usuario usuario = new Usuario(dto.getNome(), dto.getEmail(), dto.getSenha());
        Usuario criado = usuarioService.criar(usuario);
        UsuarioDTO out = new UsuarioDTO(criado.getId(), criado.getNome(), criado.getEmail());

        return ResponseEntity.created(URI.create("/api/usuarios" + criado.getId())).body(out);
    }

    @GetMapping
    public List<UsuarioDTO> all(){
        return usuarioService.findAll().stream()
                .map(u -> new UsuarioDTO(u.getId(), u.getNome(), u.getEmail()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getById(@PathVariable Long id){
        return usuarioService.findById(id)
                .map(u -> ResponseEntity.ok(new UsuarioDTO(u.getId(), u.getNome(), u.getEmail())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto){
        return usuarioService.findById(id).map(existing -> {
            existing.setNome(dto.getNome() != null ? dto.getNome() : existing.getNome());
            existing.setEmail(dto.getEmail() != null ? dto.getEmail() : existing.getEmail());
            if (dto.getSenha() != null) existing.setSenha(dto.getSenha());
            Usuario salvo = usuarioService.atualizar(existing);

            return ResponseEntity.ok(new UsuarioDTO(salvo.getId(), salvo.getNome(), salvo.getEmail()));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/salvos")
    public ResponseEntity<?> listarSalvos(@PathVariable Long id) {
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
    }

    @PutMapping("/{id}/perfil")
    public ResponseEntity<?> atualizarPerfil(@PathVariable Long id, @RequestBody UsuarioUpdateDTO dto) {

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
        if (dto.getSenha() != null) usuario.setSenha(dto.getSenha()); // futuramente hash

        Usuario salvo = usuarioService.atualizar(usuario);

        UsuarioDTO resposta = new UsuarioDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEmail()
        );

        return ResponseEntity.ok(new RespostaPadrao<>(true, "Perfil atualizado", resposta));
    }


}
