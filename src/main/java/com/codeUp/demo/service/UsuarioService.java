package com.codeUp.demo.service;

import com.codeUp.demo.model.Publicacao;
import com.codeUp.demo.model.Usuario;
import com.codeUp.demo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario criar(Usuario usuario){
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario>findById(Long id){
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario>findByEmail(String email){
        return usuarioRepository.findByEmail(email);
    }

    public Usuario atualizar(Usuario usuario){
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> findAll(){
        return usuarioRepository.findAll();
    }

    public void salvarPublicacao(Usuario usuario, Publicacao publicacao){
        if (!usuario.getPublicacoesSalvas().contains(publicacao)){
            usuario.getPublicacoesSalvas().add(publicacao);
        }
    }

    public void removerPublicacaoSalva(Usuario usuario, Publicacao publicacao) {
        usuario.getPublicacoesSalvas().remove(publicacao);
    }

    public void seguir(Long seguidorId, Long seguidoId) {

        if (seguidorId.equals(seguidoId)) {
            throw new RuntimeException("Você não pode seguir você mesmo");
        }

        var seguidor = findById(seguidorId)
                .orElseThrow(() -> new RuntimeException("Seguidor não encontrado"));

        var seguido = findById(seguidoId)
                .orElseThrow(() -> new RuntimeException("Usuário a ser seguido não encontrado"));

        // Evitar seguir duas vezes
        if (seguido.getSeguidores().contains(seguidor)) {
            throw new RuntimeException("Você já segue este usuário");
        }

        seguido.getSeguidores().add(seguidor);
        atualizar(seguido);
    }

    public void deixarDeSeguir(Long seguidorId, Long seguidoId) {
        var seguidor = findById(seguidorId)
                .orElseThrow(() -> new RuntimeException("Seguidor não encontrado"));

        var seguido = findById(seguidoId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        seguido.getSeguidores().remove(seguidor);

        atualizar(seguido);
    }

}
