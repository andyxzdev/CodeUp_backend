package com.codeUp.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "publicacoes")
public class Publicacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String conteudo;

    private LocalDateTime createdAt;

    // contador (mantido para compatibilidade com DTO/ front)
    private int curtidasCount = 0;
    private int comentariosCount = 0;
    private int compartilhamentoCount = 0;
    private int salvosCount = 0;

    @Column(nullable = true)
    private String imageUrl; // URL da imagem salva

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Usuario author;

    @OneToMany(mappedBy = "publicacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();

    // =========================
    // Curtidas (quem curtiu)
    // =========================
    @ManyToMany
    @JoinTable(
            name = "publicacao_curtidas",
            joinColumns = @JoinColumn(name = "publicacao_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private Set<Usuario> curtidas = new HashSet<>();

    public Publicacao() {}

    public Publicacao(String conteudo, Usuario author) {
        this.conteudo = conteudo;
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }

    // GETTERS & SETTERS

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getConteudo() { return conteudo; }

    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getCurtidasCount() { return curtidasCount; }

    public void setCurtidasCount(int curtidasCount) { this.curtidasCount = curtidasCount; }

    public int getComentariosCount() { return comentariosCount; }

    public void setComentariosCount(int comentariosCount) { this.comentariosCount = comentariosCount; }

    public int getCompartilhamentoCount() { return compartilhamentoCount; }

    public void setCompartilhamentoCount(int compartilhamentoCount) { this.compartilhamentoCount = compartilhamentoCount; }

    public int getSalvosCount() { return salvosCount; }

    public void setSalvosCount(int salvosCount) { this.salvosCount = salvosCount; }

    public Usuario getAuthor() { return author; }

    public void setAuthor(Usuario author) { this.author = author; }

    public List<Comentario> getComentarios() { return comentarios; }

    public void setComentarios(List<Comentario> comentarios) { this.comentarios = comentarios; }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Set<Usuario> getCurtidas() { return curtidas; }

    public void setCurtidas(Set<Usuario> curtidas) { this.curtidas = curtidas; }

    // helpers
    public void adicionarCurtida(Usuario u) {
        this.curtidas.add(u);
        this.curtidasCount = this.curtidas.size();
    }

    public void removerCurtidaPorUsuarioId(Long usuarioId) {
        this.curtidas.removeIf(u -> u.getId().equals(usuarioId));
        this.curtidasCount = this.curtidas.size();
    }

    public void incrementarComentariosCount() {
        this.comentariosCount = this.comentarios.size();
    }

    public void atualizarSalvosCount(int novoValor) {
        this.salvosCount = novoValor;
    }
}
