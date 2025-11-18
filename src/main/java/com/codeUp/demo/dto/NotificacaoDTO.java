package com.codeUp.demo.dto;

import com.codeUp.demo.model.Notificacao;

import java.time.LocalDateTime;

public class NotificacaoDTO {

    private Long id;
    private String mensagem;
    private boolean lida;
    private LocalDateTime createdAt;

    public NotificacaoDTO(Notificacao n) {
        this.id = n.getId();
        this.mensagem = n.getMensagem();
        this.lida = n.isLida();
        this.createdAt = n.getCreatedAt();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
