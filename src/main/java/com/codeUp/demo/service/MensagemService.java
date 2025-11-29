package com.codeUp.demo.service;

import com.codeUp.demo.model.Mensagem;
import com.codeUp.demo.repository.MensagemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MensagemService {

    private final MensagemRepository repository;

    public MensagemService(MensagemRepository repository) {
        this.repository = repository;
    }

    public Mensagem enviar(Mensagem mensagem) {
        mensagem.setEnviadoEm(LocalDateTime.now());
        return repository.save(mensagem);
    }

    public List<Mensagem> conversa(Long u1, Long u2) {
        return repository.buscarConversa(u1, u2);
    }

    public List<Map<String, Object>> conversasRecentes(Long usuarioId) {

        var mensagens = repository.buscarMensagensRecentes(usuarioId);

        if (mensagens == null) mensagens = new ArrayList<>();

        // Agrupar por usuário (quem conversa comigo)
        Map<Long, Mensagem> ultimaMensagemPorUsuario = new HashMap<>();

        for (Mensagem m : mensagens) {

            Long outroId = m.getRemetente().getId().equals(usuarioId)
                    ? m.getDestinatario().getId()
                    : m.getRemetente().getId();

            // adiciona apenas a última mensagem de cada conversa
            ultimaMensagemPorUsuario.putIfAbsent(outroId, m);
        }

        // Converter para DTO simples
        return ultimaMensagemPorUsuario.values().stream()
                .map(msg -> {
                    Long outroId = msg.getRemetente().getId().equals(usuarioId)
                            ? msg.getDestinatario().getId()
                            : msg.getRemetente().getId();

                    String outroNome = msg.getRemetente().getId().equals(usuarioId)
                            ? msg.getDestinatario().getNome()
                            : msg.getRemetente().getNome();

                    Map<String, Object> map = new HashMap<>();
                    map.put("usuarioId", outroId);
                    map.put("usuarioNome", outroNome);
                    map.put("ultimaMensagem", msg.getConteudo());
                    map.put("enviadoEm", msg.getEnviadoEm());
                    return map;
                })
                // ORDENAR POR LOCALDATETIME CORRETAMENTE 🔥
                .sorted(Comparator.comparing(
                        m -> (LocalDateTime) m.get("enviadoEm"),
                        Comparator.reverseOrder()
                ))
                .collect(Collectors.toList());
    }
}
