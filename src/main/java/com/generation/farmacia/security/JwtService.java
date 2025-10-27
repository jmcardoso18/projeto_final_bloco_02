
package com.generation.farmacia.security;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
 
@Component
public class JwtService {
 
    private static final String SECRET = loadSecret();
    private static final Duration EXPIRATION_DURATION = Duration.ofMinutes(60);
    private final SecretKey signingKey;
 
    public JwtService() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }
 
    private static String loadSecret() {
        // 1️⃣ Tenta ler do ambiente do sistema (produção)
        String secret = System.getenv("JWT_SECRET");
 
        // 2️⃣ Se não existir, tenta ler do .env (desenvolvimento local)
        if (secret == null || secret.isBlank()) {
            try {
                Dotenv dotenv = Dotenv.configure()
                        .ignoreIfMissing()
                        .load();
                secret = dotenv.get("JWT_SECRET");
            } catch (Exception e) {
                secret = null;
            }
        }
 
        // 3️⃣ Se ainda não encontrou, lança erro claro
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET não foi configurado no ambiente.");
        }
 
        return secret;
    }
 
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
 
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }
 
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
 
    public boolean validateToken(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject().equals(userDetails.getUsername()) &&
               claims.getExpiration().after(new Date());
    }
 
    public String generateToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(EXPIRATION_DURATION)))
            .signWith(signingKey)
            .compact();
    }
}