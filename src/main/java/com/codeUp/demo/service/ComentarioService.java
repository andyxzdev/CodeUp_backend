package com.codeUp.demo.service;

import com.codeUp.demo.model.Comentario;
import com.codeUp.demo.model.Publicacao;
import com.codeUp.demo.repository.ComentarioRepository;
import com.codeUp.demo.repository.PublicacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final PublicacaoRepository publicacaoRepository;

    public ComentarioService(ComentarioRepository comentarioRepository,
                             PublicacaoRepository publicacaoRepository) {
        this.comentarioRepository = comentarioRepository;
        this.publicacaoRepository = publicacaoRepository;
    }

    // Criar comentário e atualizar contador
    public Comentario criar(Comentario comentario) {

        Comentario salvo = comentarioRepository.save(comentario);

        // Atualizar contador corretamente
        Publicacao pub = salvo.getPublicacao();
        pub.setComentariosCount(pub.getComentariosCount() + 1);
        publicacaoRepository.save(pub);

        return salvo;
    }

    public List<Comentario> listarPorPublicacao(Long publicacaoId) {
        return comentarioRepository.findByPublicacaoIdOrderByCreatedAtDesc(publicacaoId);
    }

    public Optional<Comentario> findById(Long id) {
        return comentarioRepository.findById(id);
    }

    public void deletar(Long id) {
        Optional<Comentario> comentarioOpt = comentarioRepository.findById(id);

        if (comentarioOpt.isPresent()) {
            Comentario c = comentarioOpt.get();
            Publicacao pub = c.getPublicacao();

            pub.setComentariosCount(Math.max(0, pub.getComentariosCount() - 1));
            publicacaoRepository.save(pub);

            comentarioRepository.deleteById(id);
        }
    }
}
