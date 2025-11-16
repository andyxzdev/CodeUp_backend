package com.codeUp.demo.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class PublicacaoDTO {
    private Long id;

    @NotBlank(message = "o conteúdo não pode estar vazio")
    private String conteudo;
    private LocalDateTime createdAt;
    private int curtidasCount;
    @NotBlank(message = "o id do autor não pode estar vazio")
    private Long authorId;
    private String authorName;

    public PublicacaoDTO(Long id, String conteudo, LocalDateTime createdAt, int curtidasCount, Long aLong, String nome){}

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

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
