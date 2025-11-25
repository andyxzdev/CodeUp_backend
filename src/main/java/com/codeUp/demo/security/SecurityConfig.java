package com.codeUp.demo.security;

import com.codeUp.demo.service.UsuarioService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    public SecurityConfig(JwtUtil jwtUtil, UsuarioService usuarioService) {
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    // JWT Filter
    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtUtil, usuarioService);
    }

    // Registrar filtro JWT (EXCLUINDO login e registro)
    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterRegistration() {
        FilterRegistrationBean<JwtFilter> bean = new FilterRegistrationBean<>();

        bean.setFilter(jwtFilter());

        // 🔥 NÃO FILTRAR LOGIN E REGISTRO
        bean.addUrlPatterns(
                "/api/*"
        );
        bean.addInitParameter("excludedUrls",
                "/api/auth/login,/api/usuarios/registrar,/api/publicacoes/temp"
        );

        return bean;
    }

    // Configuração CORS correta para React Native + Expo
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 🔥 PERMITIR TUDO NO DEV
        config.setAllowedOriginPatterns(Arrays.asList("*"));

        config.setAllowCredentials(false);

        config.setAllowedHeaders(Arrays.asList(
                "Origin",
                "Content-Type",
                "Accept",
                "Authorization"
        ));

        config.setExposedHeaders(Arrays.asList(
                "Authorization"
        ));

        config.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
