package com.codeUp.demo.controller;

import com.codeUp.demo.RespostaPadrao;
import com.codeUp.demo.dto.PublicacaoDTO;
import com.codeUp.demo.model.Publicacao;
import com.codeUp.demo.model.Usuario;
import com.codeUp.demo.service.PublicacaoService;
import com.codeUp.demo.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/publicacoes")
public class PublicacaoController {

    @Value("${server.address:0.0.0.0}")
    private String serverAddress;

    private final PublicacaoService publicacaoService;
    private final UsuarioService usuarioService;

    public PublicacaoController(
            PublicacaoService publicacaoService,
            UsuarioService usuarioService
    ) {
        this.publicacaoService = publicacaoService;
        this.usuarioService = usuarioService;
    }

    // --------------------------------------------------------------------
    // CRIAR PUBLICAÇÃO (TEXTO + IMAGEM)
    // --------------------------------------------------------------------
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> criarPublicacao(
            HttpServletRequest request,
            @RequestParam("conteudo") String conteudo,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem
    ) {

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(new RespostaPadrao<>(false, "Token inválido", null));
        }

        Usuario autor = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Publicacao pub = new Publicacao(conteudo, autor);
        pub.setCreatedAt(LocalDateTime.now());

        // PROCESSAR IMAGEM
        if (imagem != null && !imagem.isEmpty()) {
            String imageUrl = publicacaoService.uploadImagem(imagem, serverAddress);
            pub.setImageUrl(imageUrl);
        }

        Publicacao criada = publicacaoService.criar(pub);

        return ResponseEntity.created(URI.create("/api/publicacoes/" + criada.getId()))
                .body(new RespostaPadrao<>(true, "Publicação criada", toDTO(criada)));
    }

    // --------------------------------------------------------------------
    // FEED PAGINADO
    // --------------------------------------------------------------------
    @GetMapping("/feed")
    public ResponseEntity<?> feed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Publicacao> pagina = publicacaoService.feed(pageable);

        Page<PublicacaoDTO> resposta = pagina.map(this::toDTO);

        return ResponseEntity.ok(new RespostaPadrao<>(true, "Feed carregado", resposta));
    }

    // --------------------------------------------------------------------
    // PUBLICAÇÕES DE UM USUÁRIO
    // --------------------------------------------------------------------
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> publicacoesDoUsuario(@PathVariable Long usuarioId) {

        var lista = publicacaoService.findByAuthor(usuarioId)
                .stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(new RespostaPadrao<>(true, "OK", lista));
    }

    // --------------------------------------------------------------------
    // DTO
    // --------------------------------------------------------------------
    private PublicacaoDTO toDTO(Publicacao p) {
        return new PublicacaoDTO(
                p.getId(),
                p.getConteudo(),
                p.getCreatedAt(),
                p.getCurtidasCount(),
                p.getAuthor().getId(),
                p.getAuthor().getNome(),
                p.getComentariosCount(),
                p.getImageUrl()
        );
    }
}
