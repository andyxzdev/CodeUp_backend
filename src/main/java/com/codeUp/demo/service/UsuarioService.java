package com.codeUp.demo.service;

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
}
