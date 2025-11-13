package com.codeUp.demo.service;

import com.codeUp.demo.model.Publicacao;
import com.codeUp.demo.repository.PublicacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PublicacaoService {
    private final PublicacaoRepository publicacaoRepository;

    public PublicacaoService(PublicacaoRepository publicacaoRepository){
        this.publicacaoRepository =  publicacaoRepository;
    }

    public Publicacao criar(Publicacao publicacao){
        return publicacaoRepository.save(publicacao);
    }

    public List<Publicacao> feed(){
        return publicacaoRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Publicacao> findById(Long id){
        return publicacaoRepository.findById(id);
    }

    public List<Publicacao> findByAuthor(Long authorId){
        return publicacaoRepository.findByAuthorIdOrderByCreatedAtDesc(authorId);
    }

    public Publicacao salvar(Publicacao publicacao){
        return publicacaoRepository.save(publicacao);
    }
}
