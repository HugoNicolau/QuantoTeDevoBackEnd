package com.example.demo.service;

import com.example.demo.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    
    @Value("${jwt.secret:quantotedevo_secret_key_muito_segura_para_desenvolvimento_local_123456789}")
    private String secret;
    
    @Value("${jwt.expiration:86400000}") // 24 horas em milliseconds
    private long jwtExpiration;

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(Usuario usuario) {
        return generateToken(new HashMap<>(), usuario);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            Usuario usuario
    ) {
        return buildToken(extraClaims, usuario, jwtExpiration);
    }

    public String refreshToken(String oldToken) {
        final String email = extractEmail(oldToken);
        if (email != null && !isTokenExpired(oldToken)) {
            // Em um cenário real, você buscaria o usuário do banco
            Map<String, Object> claims = new HashMap<>();
            claims.put("email", email);
            
            return Jwts
                    .builder()
                    .claims(claims)
                    .subject(email)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(getSignInKey())
                    .compact();
        }
        throw new RuntimeException("Token inválido para refresh");
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            Usuario usuario,
            long expiration
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(usuario.getEmail())
                .claim("userId", usuario.getId())
                .claim("nome", usuario.getNome())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, Usuario usuario) {
        final String email = extractEmail(token);
        return (email.equals(usuario.getEmail())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
