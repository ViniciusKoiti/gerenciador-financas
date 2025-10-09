package com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security;

import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.port.in.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtTokenService implements TokenService {

    @Value("${jwt.secret}")
    private String chaveSecreta;

    @Value("${jwt.expiration}")
    private long tempoExpiracao;

    @Override
    public String extrairUsername(String token) {
        return extrairAtributo(token, Claims::getSubject);
    }

    @Override
    public Long extrairUsuarioId(String token) {
        return extrairAtributo(token, claims -> claims.get("userId", Long.class));
    }

    @Override
    public boolean validarToken(String token, String username) {
        try {
            String tokenUsername = extrairUsername(token);
            return tokenUsername.equals(username) && !isTokenExpirado(token);
        } catch (Exception e) {
            log.warn("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String gerarToken(Usuario usuario) {
        Map<String, Object> claims = Map.of("userId", usuario.getId().getValue());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getEmail().getEndereco())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tempoExpiracao))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Métodos privados auxiliares

    private <T> T extrairAtributo(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extrairTodasClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extrairTodasClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpirado(String token) {
        Date expiracao = extrairAtributo(token, Claims::getExpiration);
        return expiracao.before(new Date());
    }

    private Key getSignKey() {
        byte[] keyBytes = chaveSecreta.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}