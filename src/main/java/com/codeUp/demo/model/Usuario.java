package com.codeUp.demo.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true, nullable = false)
    private String email;
    private String senha;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Publicacao> publicacoes = new ArrayList<>();

    private String bio;
    private String fotoPerfil;

    @ManyToMany
    @JoinTable(
            name = "usuarios_publicacoes_salvas",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "publicacao_id")
    )
    private List<Publicacao> publicacoesSalvas = new ArrayList<>();

    // inverso opcional de curtidas (não obrigatório, mas útil)
    @ManyToMany(mappedBy = "curtidas")
    private Set<Publicacao> curtidas = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "seguidores",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "seguidor_id")
    )
    private Set<Usuario> seguidores = new HashSet<>();

    @ManyToMany(mappedBy = "seguidores")
    private Set<Usuario> seguindo = new HashSet<>();

    public Usuario(){}

    public Usuario(String nome,String email, String senha){
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    // getters / setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public List<Publicacao> getPublicacoes() {
        return publicacoes;
    }

    public void setPublicacoes(List<Publicacao> publicacoes) {
        this.publicacoes = publicacoes;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public List<Publicacao> getPublicacoesSalvas() {
        return publicacoesSalvas;
    }

    public void setPublicacoesSalvas(List<Publicacao> publicacoesSalvas) {
        this.publicacoesSalvas = publicacoesSalvas;
    }

    public Set<Usuario> getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(Set<Usuario> seguidores) {
        this.seguidores = seguidores;
    }

    public Set<Usuario> getSeguindo() {
        return seguindo;
    }

    public void setSeguindo(Set<Usuario> seguindo) {
        this.seguindo = seguindo;
    }

    public Set<Publicacao> getCurtidas() {
        return curtidas;
    }

    public void setCurtidas(Set<Publicacao> curtidas) {
        this.curtidas = curtidas;
    }
}
