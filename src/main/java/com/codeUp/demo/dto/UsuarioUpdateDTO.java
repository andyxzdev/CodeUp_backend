package com.codeUp.demo.dto;

public class UsuarioUpdateDTO {

    private String nome;
    private String email;
    private String senha;
    private String bio;
    private String fotoPerfil;

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
