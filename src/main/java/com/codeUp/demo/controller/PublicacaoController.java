package com.codeUp.demo.controller;

import com.codeUp.demo.dto.PublicacaoDTO;
import com.codeUp.demo.model.Publicacao;
import com.codeUp.demo.service.PublicacaoService;
import com.codeUp.demo.service.UsuarioService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/publicacoes")
public class PublicacaoController {
    private final PublicacaoService publicacaoService;
    private final UsuarioService usuarioService;

    public PublicacaoController(PublicacaoService publicacaoService, UsuarioService usuarioService){
        this.publicacaoService = publicacaoService;
        this.usuarioService = usuarioService;
    }

    //Criar post
    @PostMapping
    public ResponseEntity<?> publicacaoCriada(@RequestBody PublicacaoDTO dto) {
        if (dto.getAuthorId() == null)
            return ResponseEntity.badRequest().body("authorId é obrigatório");

        var optionalAutor = usuarioService.findById(dto.getAuthorId());
        if (optionalAutor.isEmpty())
            return ResponseEntity.badRequest().body("authorId inválido");

        var autor = optionalAutor.get();
        Publicacao publicacao = new Publicacao(dto.getConteudo(), autor);
        publicacao.setCreatedAt(LocalDateTime.now());
        Publicacao criada = publicacaoService.criar(publicacao);
        PublicacaoDTO out = toDTO(criada);

        return ResponseEntity.created(URI.create("/api/publicacoes/" + criada.getId())).body(out);
    }

    // Feed (todos os posts ordenados)
    @GetMapping("/feed")
    public List<PublicacaoDTO> feed(){
        return publicacaoService.feed().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/usuario/usuario{Id}")
    public List<PublicacaoDTO> publicacoesDoUsuario(@PathVariable Long usuarioId){
        return publicacaoService.findByAuthor(usuarioId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @PostMapping("/publicacao{Id}/curtida")
    public ResponseEntity<?> curtidaPublicacao(@PathVariable Long publicacaoId){
        return publicacaoService.findById(publicacaoId).map(publicacao -> {
            publicacao.setCurtidasCount(publicacao.getCurtidasCount() + 1 );
            publicacaoService.salvar(publicacao);
            return ResponseEntity.ok().body("curtido");
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicacaoDTO> getById(@PathVariable Long id){
        return publicacaoService.findById(id).map(publicacao -> ResponseEntity.ok(toDTO(publicacao)))
                .orElse(ResponseEntity.notFound().build());
    }

    private PublicacaoDTO toDTO(Publicacao publicacao){
        PublicacaoDTO dto = new PublicacaoDTO();
        dto.setId(publicacao.getId());
        dto.setConteudo(publicacao.getConteudo());
        dto.setCreatedAt(publicacao.getCreatedAt());
        dto.setCurtidasCount(publicacao.getCurtidasCount());
        if (publicacao.getAuthor() != null){
            dto.setAuthorId(publicacao.getAuthor().getId());
            dto.setAuthorName(publicacao.getAuthor().getNome());
        }

        return dto;
    }
}
