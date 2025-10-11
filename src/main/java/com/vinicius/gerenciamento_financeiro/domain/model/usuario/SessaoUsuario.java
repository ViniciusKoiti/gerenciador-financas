package com.vinicius.gerenciamento_financeiro.domain.model.usuario;

import java.time.LocalDateTime;

/**
 * Value Object que representa uma sessão autenticada do usuário
 */
public final class SessaoUsuario {
    
    private final Usuario usuario;
    private final String token;
    private final LocalDateTime dataExpiracao;
    
    public SessaoUsuario(Usuario usuario, String token, LocalDateTime dataExpiracao) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo");
        }
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token não pode ser vazio");
        }
        if (dataExpiracao == null) {
            throw new IllegalArgumentException("Data de expiração não pode ser nula");
        }
        
        this.usuario = usuario;
        this.token = token;
        this.dataExpiracao = dataExpiracao;
    }
    
    public static SessaoUsuario criar(Usuario usuario, String token, LocalDateTime dataExpiracao) {
        return new SessaoUsuario(usuario, token, dataExpiracao);
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public String getToken() {
        return token;
    }
    
    public LocalDateTime getDataExpiracao() {
        return dataExpiracao;
    }
    
    public boolean isExpirada() {
        return LocalDateTime.now().isAfter(dataExpiracao);
    }
    
    public boolean isValida() {
        return !isExpirada();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        SessaoUsuario that = (SessaoUsuario) o;
        return token.equals(that.token);
    }
    
    @Override
    public int hashCode() {
        return token.hashCode();
    }
    
    @Override
    public String toString() {
        return "SessaoUsuario{usuario=" + usuario.getId() + ", expirada=" + isExpirada() + "}";
    }
}