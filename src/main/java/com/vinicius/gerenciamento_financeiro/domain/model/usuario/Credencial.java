package com.vinicius.gerenciamento_financeiro.domain.model.usuario;

import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.Email;

/**
 * Value Object que representa as credenciais de acesso do usuário
 */
public final class Credencial {
    
    private final Email email;
    private final String senha;
    
    public Credencial(Email email, String senha) {
        if (email == null) {
            throw new IllegalArgumentException("Email não pode ser nulo");
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser vazia");
        }
        
        this.email = email;
        this.senha = senha;
    }
    
    public static Credencial criar(String email, String senha) {
        return new Credencial(new Email(email), senha);
    }
    
    public Email getEmail() {
        return email;
    }
    
    public String getSenha() {
        return senha;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Credencial that = (Credencial) o;
        return email.equals(that.email);
    }
    
    @Override
    public int hashCode() {
        return email.hashCode();
    }
    
    @Override
    public String toString() {
        return "Credencial{email=" + email + "}";
    }
}