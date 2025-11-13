package com.codeUp.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "publicacoes")
public class Publicacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String conteudo;

    private LocalDateTime createdAt;

    private int curtidasCount = 0;
    private int comentariosCount = 0;
    private int compartilhamentoCount = 0;
    private int salvosCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Usuario author;

    public Publicacao(){}

    public Publicacao(String conteudo, Usuario author){
        this.conteudo = conteudo;
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getCurtidasCount() {
        return curtidasCount;
    }

    public void setCurtidasCount(int curtidasCount) {
        this.curtidasCount = curtidasCount;
    }

    public int getComentariosCount() {
        return comentariosCount;
    }

    public void setComentariosCount(int comentariosCount) {
        this.comentariosCount = comentariosCount;
    }

    public int getCompartilhamentoCount() {
        return compartilhamentoCount;
    }

    public void setCompartilhamentoCount(int compartilhamentoCount) {
        this.compartilhamentoCount = compartilhamentoCount;
    }

    public int getSalvosCount() {
        return salvosCount;
    }

    public void setSalvosCount(int salvosCount) {
        this.salvosCount = salvosCount;
    }

    public Usuario getAuthor() {
        return author;
    }

    public void setAuthor(Usuario author) {
        this.author = author;
    }
}
