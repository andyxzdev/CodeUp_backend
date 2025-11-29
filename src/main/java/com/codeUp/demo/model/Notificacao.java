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

    // Tipo: "mensagem", "curtida", "comentario", "seguir", "salvar", "sistema", ...
    private String tipo;

    // id de referência opcional (ex: id da publicação, id da mensagem, id do usuário que gerou)
    private Long referenciaId;

    private boolean lida = false;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Notificacao() {}

    public Notificacao(String mensagem, String tipo, Long referenciaId, Usuario usuario) {
        this.mensagem = mensagem;
        this.tipo = tipo;
        this.referenciaId = referenciaId;
        this.usuario = usuario;
        this.createdAt = LocalDateTime.now();
    }

    // getters / setters

    public Long getId() { return id; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Long getReferenciaId() { return referenciaId; }
    public void setReferenciaId(Long referenciaId) { this.referenciaId = referenciaId; }

    public boolean isLida() { return lida; }
    public void setLida(boolean lida) { this.lida = lida; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
