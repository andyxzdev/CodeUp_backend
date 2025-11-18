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

    // Criar publicação (USUÁRIO AUTENTICADO)
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

    @PostMapping("/temp")
    public ResponseEntity<?> criarPublicacaoTemp(@RequestBody PublicacaoDTO dto) {

        try {
            System.out.println("🎯 Endpoint /temp chamado!");
            System.out.println("📦 DTO recebido: " + dto);
            System.out.println("📝 Conteúdo: " + dto.getConteudo());
            System.out.println("👤 AuthorId: " + dto.getAuthorId());

            // Buscar QUALQUER usuário existente
            var todosUsuarios = usuarioService.findAll();
            System.out.println("🔍 Total de usuários encontrados: " + todosUsuarios.size());

            if (todosUsuarios.isEmpty()) {
                System.out.println("❌ Nenhum usuário encontrado no banco!");
                return ResponseEntity.badRequest()
                        .body(new RespostaPadrao<>(false, "Nenhum usuário cadastrado", null));
            }

            // Listar todos os usuários para debug
            for (var user : todosUsuarios) {
                System.out.println("   👤 Usuário: " + user.getId() + " - " + user.getNome());
            }

            // Usar o primeiro usuário disponível
            var usuario = todosUsuarios.get(0);
            System.out.println("✅ Usando usuário: " + usuario.getNome() + " (ID: " + usuario.getId() + ")");

            Publicacao pub = new Publicacao(dto.getConteudo(), usuario);
            pub.setCreatedAt(LocalDateTime.now());

            Publicacao criada = publicacaoService.criar(pub);
            PublicacaoDTO out = toDTO(criada);

            System.out.println("📝 Publicação criada com ID: " + criada.getId());

            return ResponseEntity.ok()
                    .body(new RespostaPadrao<>(true, "Publicação criada com sucesso", out));

        } catch (Exception e) {
            System.out.println("💥 ERRO no endpoint /temp: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new RespostaPadrao<>(false, "Erro interno: " + e.getMessage(), null));
        }
    }

    // FEED
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

    // Lista publicações de um usuário
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

            // atualiza curtidas
            pub.setCurtidasCount(pub.getCurtidasCount() + 1);
            publicacaoService.salvar(pub);

            // 🔔 cria a notificação para o dono da publicação
            notificacaoService.criarNotificacao(
                    pub.getAuthor(),
                    usuario.get().getNome() + " curtiu sua publicação"
            );

            notificacaoService.criarNotificacao(
                    pub.getAuthor(),
                    usuario.get().getNome() + " comentou sua publicação"
            );

            notificacaoService.criarNotificacao(
                    pub.getAuthor(),
                    usuario.get().getNome() + " salvou sua publicação"
            );


            return ResponseEntity.ok(
                    new RespostaPadrao<>(true, "Publicação curtida", null)
            );

        }).orElse(ResponseEntity.status(404)
                .body(new RespostaPadrao<>(false, "Publicação não encontrada", null)));
    }



    // Buscar publicação por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return publicacaoService.findById(id)
                .map(pub -> ResponseEntity.ok(
                        new RespostaPadrao<>(true, "Publicação encontrada", toDTO(pub))
                ))
                .orElse(ResponseEntity.status(404)
                        .body(new RespostaPadrao<>(false, "Publicação não encontrada", null)));
    }

    // Salvar publicação
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

    // Remover publicação dos salvos
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

    // Conversão Entity → DTO
    // No PublicacaoController.java
    private PublicacaoDTO toDTO(Publicacao publicacao) {
        System.out.println("🔍 DEBUG toDTO - Iniciando conversão:");
        System.out.println("   Publicacao ID: " + publicacao.getId());
        System.out.println("   Conteúdo: " + publicacao.getConteudo());
        System.out.println("   CreatedAt: " + publicacao.getCreatedAt());
        System.out.println("   CurtidasCount: " + publicacao.getCurtidasCount());

        // Verifique se o autor está carregado
        if (publicacao.getAuthor() != null) {
            System.out.println("   Autor: " + publicacao.getAuthor().getNome());
            System.out.println("   Autor ID: " + publicacao.getAuthor().getId());
        } else {
            System.out.println("❌ ERRO: Autor da publicação é NULL!");
        }

        // Crie o DTO com os valores
        PublicacaoDTO dto = new PublicacaoDTO(
                publicacao.getId(),
                publicacao.getConteudo(),
                publicacao.getCreatedAt(),
                publicacao.getCurtidasCount(),
                publicacao.getAuthor() != null ? publicacao.getAuthor().getId() : null,
                publicacao.getAuthor() != null ? publicacao.getAuthor().getNome() : null
        );

        System.out.println("✅ DTO criado: " + dto.toString());
        return dto;
    }
}
