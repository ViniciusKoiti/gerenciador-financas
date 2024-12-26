package com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String chaveSecreta;

    @Value("${jwt.expiration}")
    private long tempoExpiracao;

    public String extrairUsername(String token) {
        return extrairAtributos(token, Claims::getSubject);
    }

    public Long extrairUsuarioId(String token) {
        return extrairAtributos(token, claims -> claims.get("userId", Long.class));
    }

    public Date extrairExpiracao(String token) {
        return extrairAtributos(token, Claims::getExpiration);
    }

    public <T> T extrairAtributos(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpirado(String token) {
        return extrairExpiracao(token).before(new Date());
    }

    public String gerarToken(UserDetails userDetails, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return criarToken(claims, userDetails);
    }

    private String criarToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tempoExpiracao))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validarToken(String token, UserDetails userDetails) {
        final String username = extrairUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpirado(token));
    }

    private Key getSignKey() {
        byte[] keyBytes = chaveSecreta.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}