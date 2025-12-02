package com.codeUp.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET;

    private final long EXPIRATION = 1000 * 60 * 60 * 24; // 24h

    private byte[] getSigningKey() {
        return SECRET.getBytes();
    }

    public String gerarToken(Long userId, String email) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(Keys.hmacShaKeyFor(getSigningKey()), SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserId(String token) {
        return Long.valueOf(
                Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(getSigningKey()))
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject()
        );
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(getSigningKey()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
