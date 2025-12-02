package com.codeUp.demo.controller;

import com.codeUp.demo.RespostaPadrao;
import com.codeUp.demo.dto.PublicacaoDTO;
import com.codeUp.demo.model.Comentario;
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
import java.util.Map;

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
            String imageUrl = publicacaoService.uploadImagem(imagem, request);
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

    // CURTIR
    @PostMapping("/{id}/curtida")
    public ResponseEntity<?> curtir(
            HttpServletRequest request,
            @PathVariable Long id
    ) {

        Long userId = (Long) request.getAttribute("userId");

        Map<String, Object> resultado = publicacaoService.curtir(id, userId);

        return ResponseEntity.ok(new RespostaPadrao<>(
                true,
                "Curtida atualizada!",
                resultado
        ));
    }

    @DeleteMapping("/{id}/curtida")
    public ResponseEntity<?> removerCurtida(
            HttpServletRequest request,
            @PathVariable Long id
    ) {
        Long userId = (Long) request.getAttribute("userId");
        publicacaoService.removerCurtida(id, userId);

        return ResponseEntity.ok(new RespostaPadrao<>(true, "Curtida removida!", null));
    }

    // SALVAR
    @PostMapping("/{id}/salvar")
    public ResponseEntity<?> salvar(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        publicacaoService.salvarPublicacao(id, userId);
        return ResponseEntity.ok(new RespostaPadrao<>(true, "Publicação salva!", null));
    }

    // REMOVER DOS SALVOS
    @DeleteMapping("/{id}/salvar")
    public ResponseEntity<?> removerSalvar(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        publicacaoService.removerDosSalvos(id, userId);
        return ResponseEntity.ok(new RespostaPadrao<>(true, "Removido dos salvos!", null));
    }

    // ENVIAR COMENTÁRIO
    @PostMapping("/{id}/comentarios")
    public ResponseEntity<?> comentar(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody String conteudo
    ) {
        Long userId = (Long) request.getAttribute("userId");
        Comentario comentario = publicacaoService.comentar(id, userId, conteudo);
        return ResponseEntity.ok(new RespostaPadrao<>(true, "Comentário enviado!", comentario));
    }

    // LISTAR COMENTÁRIOS
    @GetMapping("/{id}/comentarios")
    public ResponseEntity<?> listarComentarios(@PathVariable Long id) {
        return ResponseEntity.ok(
                new RespostaPadrao<>(true, "Comentários carregados", publicacaoService.listarComentarios(id))
        );
    }

    // EXCLUIR COMENTÁRIO
    @DeleteMapping("/comentarios/{comentarioId}")
    public ResponseEntity<?> apagarComentario(
            HttpServletRequest request,
            @PathVariable Long comentarioId
    ) {
        Long userId = (Long) request.getAttribute("userId");
        publicacaoService.apagarComentario(comentarioId, userId);
        return ResponseEntity.ok(new RespostaPadrao<>(true, "Comentário deletado!", null));
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
