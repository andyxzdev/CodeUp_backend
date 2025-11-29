package com.codeUp.demo.repository;

import com.codeUp.demo.model.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {

    @Query("""
        SELECT m FROM Mensagem m
        WHERE (m.remetente.id = :u1 AND m.destinatario.id = :u2)
           OR (m.remetente.id = :u2 AND m.destinatario.id = :u1)
        ORDER BY m.enviadoEm ASC
    """)
    List<Mensagem> buscarConversa(Long u1, Long u2);

    // Últimas conversas (para tela de lista)
    @Query("""
        SELECT m FROM Mensagem m
        WHERE m.remetente.id = :usuarioId OR m.destinatario.id = :usuarioId
        ORDER BY m.enviadoEm DESC
    """)
    List<Mensagem> buscarMensagensRecentes(Long usuarioId);
}
