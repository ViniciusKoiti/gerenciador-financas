package com.vinicius.gerenciamento_financeiro.domain.model.usuario;

import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "usuario")
@Builder
@Getter
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull
    private String email;

    @NotNull
    private String senha;

    @NotNull
    private String nome;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Transacao> transacoes = new ArrayList<>();

    @Embedded
    private Auditoria auditoria;

    @OneToMany(mappedBy = "usuario")
    private Set<Categoria> categorias;

    public Usuario() {
    }

    public Usuario(Long id) {
        this.id = id;
    }

    public Usuario(Long id,
                   String email,
                   String senha,
                   String nome,
                   List<Transacao> transacoes,
                   Auditoria auditoria,
                   Set<Categoria> categorias) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.nome = nome;
        this.transacoes = transacoes;
        this.auditoria = auditoria;
        this.categorias = categorias;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public void addCategoria(Categoria categoria) {
        if (categorias == null) {
            categorias = new HashSet<>();
        }
        categorias.add(categoria);
        categoria.setUsuario(this);
    }

    public void removeCategoria(Categoria categoria) {
        if (categorias != null) {
            categorias.remove(categoria);
            categoria.setUsuario(null);
        }
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
