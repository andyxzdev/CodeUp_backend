package com.codeUp.demo.dto;

import java.time.LocalDateTime;

public class ConversaResumoDTO {

    private Long usuarioId;
    private String usuarioNome;
    private String ultimaMensagem;
    private LocalDateTime enviadoEm;

    public ConversaResumoDTO(){}

    public ConversaResumoDTO(Long usuarioId, String usuarioNome, String ultimaMensagem, LocalDateTime enviadoEm) {
        this.usuarioId = usuarioId;
        this.usuarioNome = usuarioNome;
        this.ultimaMensagem = ultimaMensagem;
        this.enviadoEm = enviadoEm;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public LocalDateTime getEnviadoEm() {
        return enviadoEm;
    }

    public void setEnviadoEm(LocalDateTime enviadoEm) {
        this.enviadoEm = enviadoEm;
    }
}
