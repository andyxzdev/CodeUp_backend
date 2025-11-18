package com.codeUp.demo.dto;

public class UsuarioDTO {
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String bio;
    private String fotoPerfil;

    public UsuarioDTO() {}

    public UsuarioDTO(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public UsuarioDTO(Long id, String nome, String email, String bio, String fotoPerfil) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.bio = bio;
        this.fotoPerfil = fotoPerfil;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
}