package com.codeUp.demo.security;

import com.codeUp.demo.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    public JwtFilter(JwtUtil jwtUtil, UsuarioService usuarioService) {
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // ================================================
        // 🔥 IGNORAR ROTAS PÚBLICAS (NÃO PRECISAM TOKEN)
        // ================================================
        if (path.startsWith("/api/auth/login") ||
                path.startsWith("/api/usuarios/registrar") ||
                path.startsWith("/api/publicacoes/temp")) {

            filterChain.doFilter(request, response);
            return;
        }

        // ================================================
        // 🔥 LER TOKEN JWT DO HEADER
        // ================================================
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                if (jwtUtil.validarToken(token)) {
                    Long userId = jwtUtil.getUserId(token);
                    request.setAttribute("userId", userId);

                    System.out.println("🔑 JWT válido — Usuário autenticado ID: " + userId);
                } else {
                    System.out.println("❌ JWT inválido!");
                }
            } catch (Exception e) {
                System.out.println("💥 Erro ao validar token: " + e.getMessage());
            }
        } else {
            System.out.println("⚠ Nenhum token encontrado no header");
        }

        // Segue para a próxima etapa do filtro
        filterChain.doFilter(request, response);
    }
}
