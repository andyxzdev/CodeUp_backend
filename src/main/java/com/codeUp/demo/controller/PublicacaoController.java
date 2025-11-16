package com.codeUp.demo.controller;

import com.codeUp.demo.RespostaPadrao;
import com.codeUp.demo.dto.PublicacaoDTO;
import com.codeUp.demo.model.Publicacao;
import com.codeUp.demo.service.PublicacaoService;
import com.codeUp.demo.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
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
    public ResponseEntity<?> publicacaoCriada(@Valid @RequestBody PublicacaoDTO dto) {
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

        return ResponseEntity.created(URI.create("/api/publicacoes/"
                + criada.getId())).body(new RespostaPadrao<>(
                        true , "Publicação criada com sucesso", out));
    }

    // Feed (todos os posts ordenados)
    @GetMapping("/feed")
    public ResponseEntity<?> feed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<Publicacao> pagina = publicacaoService.feed(pageable);
        Page<PublicacaoDTO> resposta = pagina.map(this::toDTO);

        return ResponseEntity.ok(new RespostaPadrao<>(true,
                "Feed carregado", resposta));

    }

    // Publicaçoes de um usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> publicacoesDoUsuario(@PathVariable Long usuarioId){
        var lista = publicacaoService.findByAuthor(usuarioId).stream().map(this::toDTO).collect(Collectors.toList());

        return ResponseEntity.ok(new RespostaPadrao<>(true,
                "Publicações do usuario", lista));
    }
    // curtir publicação
    @PostMapping("/{Id}/curtida")
    public ResponseEntity<?> curtidaPublicacao(@PathVariable Long id){
        return publicacaoService.findById(id).map(publicacao -> {
            publicacao.setCurtidasCount(publicacao.getCurtidasCount() + 1 );
            publicacaoService.salvar(publicacao);
            return ResponseEntity.ok(new RespostaPadrao<>(true, "publicação curtida", null));
        }).orElse(ResponseEntity.status(404).body(new RespostaPadrao<>(false,
                "Publicação não encontrada", null)));
    }

    // busca publicação por id
    @GetMapping("/{Id}")
    public ResponseEntity<?> getById(@PathVariable Long Id){
        return publicacaoService.findById(Id).map(publicacao -> ResponseEntity.ok(
                new RespostaPadrao<>(true, "Publicação Encontrada", null)))
                .orElse(ResponseEntity.status(404)
                        .body(new RespostaPadrao<>(false,
                                "Publicação não encontrada",null)));
    }

    @PostMapping("/{id}/salvar")
    public ResponseEntity<?> salvarPublicacao(
            @PathVariable Long id,
            @RequestParam Long usuarioId
    ) {
        var usuario = usuarioService.findById(usuarioId);
        if (usuario.isEmpty())
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Usuário inválido", null));

        var publicacao = publicacaoService.findById(id);
        if (publicacao.isEmpty())
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Publicação inválida", null));

        usuarioService.salvarPublicacao(usuario.get(), publicacao.get());
        usuarioService.salvarPublicacao(usuario.get(), publicacao.get());

        // Atualizar contador
        var pub = publicacao.get();
        pub.setSalvosCount(pub.getSalvosCount() + 1);
        publicacaoService.salvar(pub);

        return ResponseEntity.ok(
                new RespostaPadrao<>(true, "Publicação salva", null)
        );
    }

    @DeleteMapping("/{id}/salvar")
    public ResponseEntity<?> removerSalva(
            @PathVariable Long id,
            @RequestParam Long usuarioId
    ) {
        var usuario = usuarioService.findById(usuarioId);
        if (usuario.isEmpty())
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Usuário inválido", null));

        var publicacao = publicacaoService.findById(id);
        if (publicacao.isEmpty())
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Publicação inválida", null));

        usuarioService.removerPublicacaoSalva(usuario.get(), publicacao.get());
        usuarioService.salvarPublicacao(usuario.get(), publicacao.get());

        var pub = publicacao.get();
        pub.setSalvosCount(Math.max(0, pub.getSalvosCount() - 1));
        publicacaoService.salvar(pub);

        return ResponseEntity.ok(
                new RespostaPadrao<>(true, "Publicação removida dos salvos", null)
        );
    }



    private PublicacaoDTO toDTO(Publicacao publicacao){
        PublicacaoDTO dto = new PublicacaoDTO(publicacao.getId(), publicacao.getConteudo(), publicacao.getCreatedAt(), publicacao.getCurtidasCount(), publicacao.getAuthor().getId(), publicacao.getAuthor().getNome());
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
