package com.codeUp.demo.service;

import com.codeUp.demo.model.Publicacao;
import com.codeUp.demo.repository.PublicacaoRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PublicacaoService {

    private final PublicacaoRepository publicacaoRepository;

    public PublicacaoService(PublicacaoRepository repo) {
        this.publicacaoRepository = repo;
    }

    public Publicacao criar(Publicacao p) {
        return publicacaoRepository.save(p);
    }

    public Page<Publicacao> feed(Pageable pageable) {
        return publicacaoRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Optional<Publicacao> findById(Long id) {
        return publicacaoRepository.findById(id);
    }

    public List<Publicacao> findByAuthor(Long authorId) {
        return publicacaoRepository.findByAuthorIdOrderByCreatedAtDesc(authorId);
    }

    public Publicacao salvar(Publicacao p) {
        return publicacaoRepository.save(p);
    }

    public String uploadImagem(MultipartFile imagem, String ipLocal) {
        try {
            String nomeArquivo = UUID.randomUUID() + "-" + imagem.getOriginalFilename();

            String pasta = "C:/Users/Andy/Documents/0 - WEB DEV PROJECTS/uploads/";
            File dir = new File(pasta);
            if (!dir.exists()) dir.mkdirs();

            File destino = new File(dir, nomeArquivo);
            imagem.transferTo(destino);

            return "http://" + ipLocal + ":8080/uploads/" + nomeArquivo;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar imagem: " + e.getMessage());
        }
    }
}
