package com.codeUp.demo.service;

import com.codeUp.demo.model.Comentario;
import com.codeUp.demo.repository.ComentarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComentarioService {
    private final ComentarioRepository comentarioRepository;

    public ComentarioService(ComentarioRepository comentarioRepository){
        this.comentarioRepository = comentarioRepository;
    }

    public Comentario criar(Comentario comentario){
        return comentarioRepository.save(comentario);
    }

    public List<Comentario> listarPorPublicacao(Long publicacaoId){
        return comentarioRepository.findByPublicacaoIdOrderByCreatedAtDesc(publicacaoId);
    }

    public Optional<Comentario> findById(Long id){
        return comentarioRepository.findById(id);
    }

    public void deletar(Long id){
        comentarioRepository.deleteById(id);
    }
}
