package com.codeUp.demo.controller;

import com.codeUp.demo.RespostaPadrao;
import com.codeUp.demo.dto.PublicacaoDTO;
import com.codeUp.demo.model.Publicacao;
import com.codeUp.demo.service.NotificacaoService;
import com.codeUp.demo.service.PublicacaoService;
import com.codeUp.demo.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final NotificacaoService notificacaoService;

    public PublicacaoController(PublicacaoService publicacaoService, UsuarioService usuarioService, NotificacaoService notificacaoService) {
        this.publicacaoService = publicacaoService;
        this.usuarioService = usuarioService;
        this.notificacaoService = notificacaoService;
    }

    // Criar publicação autenticada
    @PostMapping
    public ResponseEntity<?> criarPublicacao(
            HttpServletRequest request,
            @Valid @RequestBody PublicacaoDTO dto) {

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null)
            return ResponseEntity.status(401)
                    .body(new RespostaPadrao<>(false, "Token inválido ou ausente", null));

        var usuario = usuarioService.findById(userId);
        if (usuario.isEmpty())
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Usuário não encontrado", null));

        Publicacao pub = new Publicacao(dto.getConteudo(), usuario.get());
        pub.setCreatedAt(LocalDateTime.now());

        Publicacao criada = publicacaoService.criar(pub);
        PublicacaoDTO out = toDTO(criada);

        return ResponseEntity.created(URI.create("/api/publicacoes/" + criada.getId()))
                .body(new RespostaPadrao<>(true, "Publicação criada com sucesso", out));
    }

    // Criar publicação temporária (debug)
    @PostMapping("/temp")
    public ResponseEntity<?> criarPublicacaoTemp(@RequestBody PublicacaoDTO dto) {

        try {
            var todosUsuarios = usuarioService.findAll();

            if (todosUsuarios.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new RespostaPadrao<>(false, "Nenhum usuário cadastrado", null));
            }

            var usuario = todosUsuarios.get(0);

            Publicacao pub = new Publicacao(dto.getConteudo(), usuario);
            pub.setCreatedAt(LocalDateTime.now());

            Publicacao criada = publicacaoService.criar(pub);
            PublicacaoDTO out = toDTO(criada);

            return ResponseEntity.ok(
                    new RespostaPadrao<>(true, "Publicação criada com sucesso", out)
            );

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new RespostaPadrao<>(false, "Erro interno", null));
        }
    }

    // Feed
    @GetMapping("/feed")
    public ResponseEntity<?> feed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Publicacao> pagina = publicacaoService.feed(pageable);

        Page<PublicacaoDTO> resposta = pagina.map(this::toDTO);

        return ResponseEntity.ok(
                new RespostaPadrao<>(true, "Feed carregado", resposta)
        );
    }

    // Publicações de um usuário
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> publicacoesDoUsuario(@PathVariable Long usuarioId) {

        var lista = publicacaoService.findByAuthor(usuarioId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new RespostaPadrao<>(true, "Publicações do usuário", lista));
    }

    // Curtir publicação
    @PostMapping("/{id}/curtida")
    public ResponseEntity<?> curtir(
            @PathVariable Long id,
            @RequestParam Long usuarioId
    ) {

        var usuario = usuarioService.findById(usuarioId);
        if (usuario.isEmpty())
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Usuário inválido", null));

        return publicacaoService.findById(id).map(pub -> {

            pub.setCurtidasCount(pub.getCurtidasCount() + 1);
            publicacaoService.salvar(pub);

            notificacaoService.criarNotificacao(
                    pub.getAuthor(),
                    usuario.get().getNome() + " curtiu sua publicação"
            );

            return ResponseEntity.ok(
                    new RespostaPadrao<>(true, "Publicação curtida", null)
            );

        }).orElse(ResponseEntity.status(404)
                .body(new RespostaPadrao<>(false, "Publicação não encontrada", null)));
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return publicacaoService.findById(id)
                .map(pub -> ResponseEntity.ok(
                        new RespostaPadrao<>(true, "Publicação encontrada", toDTO(pub))
                ))
                .orElse(ResponseEntity.status(404)
                        .body(new RespostaPadrao<>(false, "Publicação não encontrada", null)));
    }

    // Salvar publicação (favorito)
    @PostMapping("/{id}/salvar")
    public ResponseEntity<?> salvar(
            HttpServletRequest request,
            @PathVariable Long id) {

        Long userId = (Long) request.getAttribute("userId");

        var usuario = usuarioService.findById(userId);
        if (usuario.isEmpty())
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Usuário inválido", null));

        var publicacao = publicacaoService.findById(id);
        if (publicacao.isEmpty())
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Publicação inválida", null));

        usuarioService.salvarPublicacao(usuario.get(), publicacao.get());

        var pub = publicacao.get();
        pub.setSalvosCount(pub.getSalvosCount() + 1);
        publicacaoService.salvar(pub);

        return ResponseEntity.ok(
                new RespostaPadrao<>(true, "Publicação salva", null)
        );
    }

    // Remover dos salvos
    @DeleteMapping("/{id}/salvar")
    public ResponseEntity<?> removerSalvo(
            HttpServletRequest request,
            @PathVariable Long id) {

        Long userId = (Long) request.getAttribute("userId");

        var usuario = usuarioService.findById(userId);
        if (usuario.isEmpty())
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Usuário inválido", null));

        var publicacao = publicacaoService.findById(id);
        if (publicacao.isEmpty())
            return ResponseEntity.badRequest()
                    .body(new RespostaPadrao<>(false, "Publicação inválida", null));

        usuarioService.removerPublicacaoSalva(usuario.get(), publicacao.get());

        var pub = publicacao.get();
        pub.setSalvosCount(Math.max(0, pub.getSalvosCount() - 1));
        publicacaoService.salvar(pub);

        return ResponseEntity.ok(
                new RespostaPadrao<>(true, "Publicação removida dos salvos", null)
        );
    }

    // Conversão para DTO
    private PublicacaoDTO toDTO(Publicacao publicacao) {

        return new PublicacaoDTO(
                publicacao.getId(),
                publicacao.getConteudo(),
                publicacao.getCreatedAt(),
                publicacao.getCurtidasCount(),
                publicacao.getAuthor() != null ? publicacao.getAuthor().getId() : null,
                publicacao.getAuthor() != null ? publicacao.getAuthor().getNome() : null
        );
    }
}
