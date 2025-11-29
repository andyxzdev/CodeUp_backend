package com.codeUp.demo.service;

import com.codeUp.demo.model.Notificacao;
import com.codeUp.demo.model.Usuario;
import com.codeUp.demo.repository.NotificacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository repo;

    public NotificacaoService(NotificacaoRepository repo) {
        this.repo = repo;
    }

    // Cria notificação com tipo e referência opcional
    public Notificacao criarNotificacao(Usuario usuario, String mensagem, String tipo, Long referenciaId) {
        Notificacao n = new Notificacao(mensagem, tipo, referenciaId, usuario);
        return repo.save(n);
    }

    // Compatível com anteriores (mensagem simples)
    public void criarNotificacao(Usuario usuario, String mensagem) {
        criarNotificacao(usuario, mensagem, "sistema", null);
    }

    public List<Notificacao> listarNotificacoes(Usuario usuario) {
        return repo.findByUsuarioOrderByCreatedAtDesc(usuario);
    }

    public void marcarComoLida(Long id) {
        repo.findById(id).ifPresent(n -> {
            n.setLida(true);
            repo.save(n);
        });
    }
}
