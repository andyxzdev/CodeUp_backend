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
    @JoinColumn(name = "publicação_id")
    private Publicacao publicacao;

    private LocalDateTime createdAt;

    public Comentario(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Usuario getAuthor() {
        return author;
    }

    public void setAuthor(Usuario author) {
        this.author = author;
    }

    public Publicacao getPublicacao() {
        return publicacao;
    }

    public void setPublicacao(Publicacao publicacao) {
        this.publicacao = publicacao;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Comentario(String conteudo, Usuario author, Publicacao publicacao){
        this.conteudo = conteudo;
        this.author = author;
        this.publicacao = publicacao;
        this.createdAt = LocalDateTime.now();


    }
}
