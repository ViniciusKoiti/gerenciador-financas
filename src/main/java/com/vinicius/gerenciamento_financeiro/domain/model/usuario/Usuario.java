package com.vinicius.gerenciamento_financeiro.domain.model.usuario;

import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public final class Usuario implements UserDetails {

    private final UsuarioId id;
    private final Email email;
    private final String nome;
    private final String hashSenha;
    private final Auditoria auditoria;
    private final Set<CategoriaId> categoriaIds;

    public Usuario(UsuarioId id, String nome, Email email, String hashSenha,
                   Auditoria auditoria, Set<CategoriaId> categoriaIds) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.hashSenha = hashSenha;
        this.auditoria = auditoria;
        this.categoriaIds = categoriaIds != null ?
                Set.copyOf(categoriaIds) :
                Collections.emptySet();

        validarInvariantes();
    }

    public static Usuario criarNovo(String nome, Email email, String hashSenha) {
        validarParametrosCriacao(nome, email, hashSenha);

        return new Usuario(
                null,
                validarNome(nome),
                email,
                hashSenha,
                Auditoria.criarNova(),
                new HashSet<>()
        );
    }

    public static Usuario reconstituir(Long id, String nome, String email, String hashSenha,
                                       Auditoria auditoria, Set<CategoriaId> categoriaIds
                                       ) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo para reconstituição");
        }

        return new Usuario(
                UsuarioId.of(id),
                nome,
                new Email(email),
                hashSenha,
                auditoria != null ? auditoria : Auditoria.criarNova(),
                categoriaIds
        );
    }

    public UsuarioId getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Email getEmail() {
        return email;
    }

    public String getHashSenha() {
        return hashSenha;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public Set<CategoriaId> getCategoriaIds() {
        return categoriaIds;
    }


    public Usuario atualizarDados(String novoNome, Email novoEmail) {
        return new Usuario(
                this.id,
                validarNome(novoNome),
                novoEmail,
                this.hashSenha,
                this.auditoria.marcarComoAtualizado(),
                this.categoriaIds
        );
    }

    public Usuario alterarSenha(String novoHashSenha) {
        if (novoHashSenha == null || novoHashSenha.trim().isEmpty()) {
            throw new IllegalArgumentException("Hash da senha não pode ser vazio");
        }

        return new Usuario(
                this.id,
                this.nome,
                this.email,
                novoHashSenha,
                this.auditoria.marcarComoAtualizado(),
                this.categoriaIds
        );
    }

    public Usuario adicionarCategoria(CategoriaId categoriaId) {
        if (categoriaId == null) {
            throw new IllegalArgumentException("CategoriaId não pode ser nulo");
        }

        if (categoriaIds.contains(categoriaId)) {
            return this;
        }

        Set<CategoriaId> novasCategorias = new HashSet<>(categoriaIds);
        novasCategorias.add(categoriaId);

        return new Usuario(
                this.id,
                this.nome,
                this.email,
                this.hashSenha,
                this.auditoria.marcarComoAtualizado(),
                novasCategorias
        );
    }

    public Usuario removerCategoria(CategoriaId categoriaId) {
        if (categoriaId == null || !categoriaIds.contains(categoriaId)) {
            return this;
        }

        Set<CategoriaId> novasCategorias = new HashSet<>(categoriaIds);
        novasCategorias.remove(categoriaId);

        return new Usuario(
                this.id,
                this.nome,
                this.email,
                this.hashSenha,
                this.auditoria.marcarComoAtualizado(),
                novasCategorias
        );
    }

    public Usuario notificarNovaTransacao() {
        return new Usuario(
                this.id,
                this.nome,
                this.email,
                this.hashSenha,
                this.auditoria.marcarComoAtualizado(),
                this.categoriaIds
        );
    }

    public boolean isNovo() {
        return this.id == null;
    }

    public boolean temCategorias() {
        return !this.categoriaIds.isEmpty();
    }

    public boolean possuiCategoria(CategoriaId categoriaId) {
        return this.categoriaIds.contains(categoriaId);
    }

    public boolean podeAcessarRecurso(UsuarioId proprietarioRecurso) {
        return this.id != null && this.id.equals(proprietarioRecurso);
    }

    // Métodos UserDetails para Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.hashSenha;
    }

    public String getUsername() {
        return this.email.getEndereco();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    private static void validarParametrosCriacao(String nome, Email email, String hashSenha) {
        if (hashSenha == null || hashSenha.trim().isEmpty()) {
            throw new IllegalArgumentException("Hash da senha é obrigatório");
        }
    }

    private static String validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }

        String nomeValidado = nome.trim();

        if (nomeValidado.length() < 2) {
            throw new IllegalArgumentException("Nome deve ter pelo menos 2 caracteres");
        }
        if (nomeValidado.length() > 100) {
            throw new IllegalArgumentException("Nome não pode ter mais de 100 caracteres");
        }

        return nomeValidado;
    }



    private void validarInvariantes() {

        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalStateException("Usuario deve ter nome válido");
        }
        if (hashSenha == null || hashSenha.trim().isEmpty()) {
            throw new IllegalStateException("Usuario deve ter senha válida");
        }
        if (auditoria == null) {
            throw new IllegalStateException("Usuario deve ter auditoria");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Usuario usuario = (Usuario) o;

        if (id != null && usuario.id != null) {
            return Objects.equals(id, usuario.id);
        }

        return Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return String.format("Usuario{id=%s, email='%s', nome='%s', categorias=%d, transacoes=%d}",
                id != null ? id.getValue() : "novo",
                email,
                nome,
                categoriaIds.size());
    }
}