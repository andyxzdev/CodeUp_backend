package com.codeUp.demo.service;

import com.codeUp.demo.dto.ConversaResumoDTO;
import com.codeUp.demo.model.Mensagem;
import com.codeUp.demo.model.Usuario;
import com.codeUp.demo.repository.MensagemRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MensagemService {

    private final MensagemRepository repository;

    public MensagemService(MensagemRepository repository){
        this.repository = repository;
    }

    public Mensagem enviar(Mensagem mensagem){
        return repository.save(mensagem);
    }

    public List<Mensagem> conversa(Long usuario1, Long usuario2){
        return repository.findByRemetenteIdAndDestinatarioIdOrderByEnviadoEm(usuario1, usuario2);
    }

    public List<Mensagem> listarMensagensDoUsuario(Long usuarioId) {
        return repository.findByRemetenteIdOrDestinatarioIdOrderByEnviadoEmDesc(usuarioId, usuarioId);
    }

    public List<ConversaResumoDTO> conversasRecentes(Long usuarioId) {

        List<Mensagem> mensagens = listarMensagensDoUsuario(usuarioId);

        Map<Long, Mensagem> ultimoPorUsuario = new HashMap<>();

        for (Mensagem msg : mensagens) {

            Long outroId = msg.getRemetente().getId().equals(usuarioId) ?
                    msg.getDestinatario().getId() :
                    msg.getRemetente().getId();

            if (!ultimoPorUsuario.containsKey(outroId)) {
                ultimoPorUsuario.put(outroId, msg);
            }
        }

        return ultimoPorUsuario.entrySet().stream()
                .map(entry -> {
                    Mensagem msg = entry.getValue();
                    Usuario outro = msg.getRemetente().getId().equals(usuarioId)
                            ? msg.getDestinatario()
                            : msg.getRemetente();

                    return new ConversaResumoDTO(
                            outro.getId(),
                            outro.getNome(),
                            msg.getConteudo(),
                            msg.getEnviadoEm()
                    );
                })
                .sorted(Comparator.comparing(ConversaResumoDTO::getEnviadoEm).reversed())
                .collect(Collectors.toList());
    }


}
