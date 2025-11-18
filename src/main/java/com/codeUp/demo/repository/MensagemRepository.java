package com.codeUp.demo.repository;

import com.codeUp.demo.model.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {

    List<Mensagem> findByRemetenteIdAndDestinatarioIdOrderByEnviadoEm(
            Long remetenteId,
            Long destinatarioId
    );

    List<Mensagem> findByRemetenteIdOrDestinatarioIdOrderByEnviadoEmDesc(
            Long remetenteId,
            Long destinatarioId
    );

    List<Mensagem> findByRemetenteIdOrDestinatarioId(Long rId, Long dId);
}
