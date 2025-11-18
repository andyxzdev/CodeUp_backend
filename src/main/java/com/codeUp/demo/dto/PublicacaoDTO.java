package com.codeUp.demo.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class PublicacaoDTO {
    private Long id;

    @NotBlank(message = "o conteúdo não pode estar vazio")
    private String conteudo;
    private LocalDateTime createdAt;
    private int curtidasCount;
    private Long authorId;
    private String authorName;

    // 🔥 CONSTRUTOR CORRIGIDO - Atribua os valores!
    public PublicacaoDTO(Long id, String conteudo, LocalDateTime createdAt, int curtidasCount, Long authorId, String authorName) {
        this.id = id;
        this.conteudo = conteudo;
        this.createdAt = createdAt;
        this.curtidasCount = curtidasCount;
        this.authorId = authorId;
        this.authorName = authorName;

        // Debug no construtor
        System.out.println("✅ DTO Criado - ID: " + id + ", Conteúdo: " + conteudo + ", Autor: " + authorName);
    }

    // Construtor vazio (para frameworks)
    public PublicacaoDTO() {
        // Construtor vazio necessário para frameworks
    }

    // Getters e Setters (mantenha os que já tem)
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

    // 🔥 ADICIONE ESTE MÉTODO PARA DEBUG
    @Override
    public String toString() {
        return "PublicacaoDTO{" +
                "id=" + id +
                ", conteudo='" + conteudo + '\'' +
                ", createdAt=" + createdAt +
                ", curtidasCount=" + curtidasCount +
                ", authorId=" + authorId +
                ", authorName='" + authorName + '\'' +
                '}';
    }
}