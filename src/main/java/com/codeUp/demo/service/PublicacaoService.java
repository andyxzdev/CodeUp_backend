package com.codeUp.demo.service;

import com.codeUp.demo.model.Comentario;
import com.codeUp.demo.model.Publicacao;
import com.codeUp.demo.model.Usuario;
import com.codeUp.demo.repository.PublicacaoRepository;
import com.codeUp.demo.repository.UsuarioRepository;
import com.codeUp.demo.repository.ComentarioRepository;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PublicacaoService {

    private final PublicacaoRepository publicacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ComentarioRepository comentarioRepository;

    public PublicacaoService(
            PublicacaoRepository repo,
            UsuarioRepository usuarioRepo,
            ComentarioRepository comentarioRepo
    ) {
        this.publicacaoRepository = repo;
        this.usuarioRepository = usuarioRepo;
        this.comentarioRepository = comentarioRepo;
    }

    // ====================================================================
    // CRUD BÁSICO
    // ====================================================================
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

    // ====================================================================
// UPLOAD DE IMAGEM (CORRIGIDO, FUNCIONA EM NGROK E LOCAL)
// ====================================================================
    public String uploadImagem(MultipartFile imagem, HttpServletRequest request) {
        try {
            // Nome único
            String nomeArquivo = UUID.randomUUID() + "-" + imagem.getOriginalFilename();

            // Pasta onde vai salvar
            String pasta = "C:/Users/Andy/Documents/0 - WEB DEV PROJECTS/uploads/";
            File dir = new File(pasta);
            if (!dir.exists()) dir.mkdirs();

            // Salvar arquivo no disco
            File destino = new File(dir, nomeArquivo);
            imagem.transferTo(destino);

            // Montar URL base (funciona com NGROK)
            String baseUrl = request.getRequestURL()
                    .toString()
                    .replace(request.getRequestURI(), "");

            // URL pública final
            return baseUrl + "/uploads/" + nomeArquivo;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar imagem: " + e.getMessage());
        }
    }

    // ====================================================================
    // CURTIR — TOGGLE REAL
    // ====================================================================
    public Map<String, Object> curtir(Long publicacaoId, Long usuarioId) {

        Publicacao pub = publicacaoRepository.findById(publicacaoId)
                .orElseThrow(() -> new RuntimeException("Publicação não encontrada"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        boolean jaCurtiu = pub.getCurtidas().stream()
                .anyMatch(u -> u.getId().equals(usuarioId));

        if (jaCurtiu) {
            pub.removerCurtidaPorUsuarioId(usuarioId);
        } else {
            pub.adicionarCurtida(usuario);
        }

        // atualizar contador corretamente
        pub.setCurtidasCount(pub.getCurtidas().size());
        publicacaoRepository.save(pub);

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("novaQtdCurtidas", pub.getCurtidas().size());
        resposta.put("curtido", !jaCurtiu);

        return resposta;
    }

    public void removerCurtida(Long publicacaoId, Long usuarioId) {
        Publicacao pub = publicacaoRepository.findById(publicacaoId)
                .orElseThrow(() -> new RuntimeException("Publicação não encontrada"));

        pub.removerCurtidaPorUsuarioId(usuarioId);
        pub.setCurtidasCount(pub.getCurtidas().size());

        publicacaoRepository.save(pub);
    }

    // ====================================================================
    // SALVAR / REMOVER DOS SALVOS
    // ====================================================================
    public void salvarPublicacao(Long publicacaoId, Long usuarioId) {
        Publicacao pub = publicacaoRepository.findById(publicacaoId)
                .orElseThrow(() -> new RuntimeException("Publicação não encontrada"));

        Usuario user = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        boolean jaSalvo = user.getPublicacoesSalvas().stream()
                .anyMatch(p -> p.getId().equals(publicacaoId));

        if (!jaSalvo) {
            user.getPublicacoesSalvas().add(pub);
            usuarioRepository.save(user);
        }

        // contador
        pub.setSalvosCount(pub.getSalvosCount() + 1);
        publicacaoRepository.save(pub);
    }

    public void removerDosSalvos(Long publicacaoId, Long usuarioId) {
        Publicacao pub = publicacaoRepository.findById(publicacaoId)
                .orElseThrow(() -> new RuntimeException("Publicação não encontrada"));

        Usuario user = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.getPublicacoesSalvas().removeIf(p -> p.getId().equals(publicacaoId));
        usuarioRepository.save(user);

        int current = pub.getSalvosCount();
        pub.setSalvosCount(current > 0 ? current - 1 : 0);

        publicacaoRepository.save(pub);
    }

    // ====================================================================
    // COMENTÁRIOS
    // ====================================================================
    public Comentario comentar(Long publicacaoId, Long usuarioId, String conteudo) {
        Publicacao pub = publicacaoRepository.findById(publicacaoId)
                .orElseThrow(() -> new RuntimeException("Publicação não encontrada"));

        Usuario autor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Comentario c = new Comentario();
        c.setConteudo(conteudo);
        c.setAuthor(autor);
        c.setPublicacao(pub);
        c.setCreatedAt(LocalDateTime.now());

        Comentario salvo = comentarioRepository.save(c);

        pub.setComentariosCount(pub.getComentarios().size());
        publicacaoRepository.save(pub);

        return salvo;
    }

    public List<Comentario> listarComentarios(Long publicacaoId) {
        return comentarioRepository.findByPublicacaoIdOrderByCreatedAtDesc(publicacaoId);
    }

    public void apagarComentario(Long comentarioId, Long usuarioId) {

        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado"));

        if (!comentario.getAuthor().getId().equals(usuarioId))
            throw new RuntimeException("Você não pode apagar este comentário");

        Publicacao pub = comentario.getPublicacao();

        comentarioRepository.delete(comentario);

        pub.setComentariosCount(pub.getComentarios().size());
        publicacaoRepository.save(pub);
    }
}
