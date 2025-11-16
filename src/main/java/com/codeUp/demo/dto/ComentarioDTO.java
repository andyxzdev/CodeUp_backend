package com.codeUp.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ComentarioDTO {

    private Long id;

    @NotBlank(message = "O conteudo não pode estar vazio")
    private String conteudo;

    @NotNull(message = "O id do autor é obrigatório")
    private Long authorId;

    @NotNull(message = "O id da publicação é obrigatório")
    private Long publicacaoId;

    private String authorName;
    private LocalDateTime createdAt;

    public ComentarioDTO(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "O conteudo não pode estar vazio") String getConteudo() {
        return conteudo;
    }

    public void setConteudo(@NotBlank(message = "O conteudo não pode estar vazio") String conteudo) {
        this.conteudo = conteudo;
    }

    public @NotNull(message = "O id do autor é obrigatório") Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(@NotNull(message = "O id do autor é obrigatório") Long authorId) {
        this.authorId = authorId;
    }

    public @NotNull(message = "O id da publicação é obrigatório") Long getPublicacaoId() {
        return publicacaoId;
    }

    public void setPublicacaoId(@NotNull(message = "O id da publicação é obrigatório") Long publicacaoId) {
        this.publicacaoId = publicacaoId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
