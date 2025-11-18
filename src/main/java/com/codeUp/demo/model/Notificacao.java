package com.codeUp.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mensagem;

    private boolean lida = false; // true quando o usuário visualizar

    private LocalDateTime createdAt;

    // Usuário que RECEBE a notificação
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Notificacao() {}

    public Notificacao(String mensagem, Usuario usuario) {
        this.mensagem = mensagem;
        this.usuario = usuario;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public boolean isLida() {
        return lida;
    }

    public void setLida(boolean lida) {
        this.lida = lida;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
