package com.vinicius.gerenciamento_financeiro.adapter.out.criptografia;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
class JwtParseService {

    @Value("${jwt.secret}")
    private String chaveSecreta;

    @Value("${jwt.expiration}")
    private long tempoExpiracao;

    String extrairSubject(String token) {
        return extrairClaims(token).getSubject();
    }

    Long extrairClaim(String token) {
        return extrairClaims(token).get("userId", Long.class);
    }

    Date extrairExpiracao(String token) {
        return extrairClaims(token).getExpiration();
    }

    String gerarToken(String subject, Map<String, Object> claims, Date expiracao) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiracao)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    long getTempoExpiracao() {
        return tempoExpiracao;
    }

    private Claims extrairClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = chaveSecreta.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}