package com.codeUp.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario author;

    @ManyToOne
    @JoinColumn(name = "publicacao_id") // 🔥 CORRIGIDO
    private Publicacao publicacao;

    private LocalDateTime createdAt;

    public Comentario(){}

    public Comentario(String conteudo, Usuario author, Publicacao publicacao){
        this.conteudo = conteudo;
        this.author = author;
        this.publicacao = publicacao;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public String getConteudo() { return conteudo; }

    public Usuario getAuthor() { return author; }

    public Publicacao getPublicacao() { return publicacao; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }

    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public void setAuthor(Usuario author) { this.author = author; }

    public void setPublicacao(Publicacao publicacao) { this.publicacao = publicacao; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
