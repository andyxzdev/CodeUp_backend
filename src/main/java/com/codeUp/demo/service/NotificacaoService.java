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

    public void criarNotificacao(Usuario usuario, String mensagem) {
        Notificacao notificacao = new Notificacao(mensagem, usuario);
        repo.save(notificacao);
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
