package com.vinicius.gerenciamento_financeiro.domain.model.categoria;

import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Categoria {
    private final CategoriaId id;
    private final String nome;
    private final String descricao;
    private final boolean ativa;
    private final String icone;
    private final UsuarioId usuarioId;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;

    private final CategoriaId categoriaPaiId;
    private final Set<CategoriaId> subcategoriaIds;

    private Categoria(Builder builder) {
        this.id = builder.id;
        this.nome = validarNome(builder.nome);
        this.descricao = builder.descricao;
        this.ativa = builder.ativa;
        this.icone = builder.icone;
        this.usuarioId = Objects.requireNonNull(builder.usuarioId, "UsuarioId não pode ser nulo");
        this.criadoEm = builder.criadoEm != null ? builder.criadoEm : LocalDateTime.now();
        this.atualizadoEm = builder.atualizadoEm;
        this.categoriaPaiId = builder.categoriaPaiId;
        this.subcategoriaIds = builder.subcategoriaIds != null ?
                Set.copyOf(builder.subcategoriaIds) :
                Collections.emptySet();
    }

    public static Categoria criar(String nome, String descricao, String icone, UsuarioId usuarioId) {
        return new Builder()
                .nome(nome)
                .descricao(descricao)
                .icone(icone)
                .usuarioId(usuarioId)
                .ativa(true)
                .build();
    }

    public static Categoria criarSubcategoria(String nome, String descricao, String icone,
                                              UsuarioId usuarioId, CategoriaId categoriaPaiId) {
        return new Builder()
                .nome(nome)
                .descricao(descricao)
                .icone(icone)
                .usuarioId(usuarioId)
                .categoriaPaiId(categoriaPaiId)
                .ativa(true)
                .build();
    }

    public static Categoria reconstituir(CategoriaId id, String nome, String descricao,
                                         boolean ativa, String icone, UsuarioId usuarioId,
                                         LocalDateTime criadoEm, LocalDateTime atualizadoEm,
                                         CategoriaId categoriaPaiId, Set<CategoriaId> subcategoriaIds) {
        return new Builder()
                .id(id)
                .nome(nome)
                .descricao(descricao)
                .ativa(ativa)
                .icone(icone)
                .usuarioId(usuarioId)
                .criadoEm(criadoEm)
                .atualizadoEm(atualizadoEm)
                .categoriaPaiId(categoriaPaiId)
                .subcategoriaIds(subcategoriaIds)
                .build();
    }

    public boolean ehCategoriaPai() {
        return categoriaPaiId == null;
    }

    public boolean ehSubcategoria() {
        return categoriaPaiId != null;
    }

    public boolean temSubcategorias() {
        return !subcategoriaIds.isEmpty();
    }

    public Categoria adicionarSubcategoria(CategoriaId subcategoriaId) {
        if (subcategoriaId == null) {
            throw new IllegalArgumentException("ID da subcategoria não pode ser nulo");
        }

        Set<CategoriaId> novasSubcategorias = new HashSet<>(this.subcategoriaIds);
        novasSubcategorias.add(subcategoriaId);

        return new Builder()
                .from(this)
                .subcategoriaIds(novasSubcategorias)
                .atualizadoEm(LocalDateTime.now())
                .build();
    }

    public Categoria removerSubcategoria(CategoriaId subcategoriaId) {
        Set<CategoriaId> novasSubcategorias = new HashSet<>(this.subcategoriaIds);
        novasSubcategorias.remove(subcategoriaId);

        return new Builder()
                .from(this)
                .subcategoriaIds(novasSubcategorias)
                .atualizadoEm(LocalDateTime.now())
                .build();
    }

    public boolean podeSerDesativada() {
        return subcategoriaIds.isEmpty();
    }

    public Categoria desativar() {
        if (!this.ativa) {
            throw new IllegalStateException("CategoriaJpaEntity já está desativada");
        }

        if (!podeSerDesativada()) {
            throw new IllegalStateException("Não é possível desativar categoriaJpaEntity com subcategorias ativas");
        }

        return new Builder()
                .from(this)
                .ativa(false)
                .atualizadoEm(LocalDateTime.now())
                .build();
    }

    public boolean pertenceAoUsuario(UsuarioId usuarioId) {
        return this.usuarioId.equals(usuarioId);
    }

    private String validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoriaJpaEntity não pode ser vazio");
        }
        if (nome.length() > 50) {
            throw new IllegalArgumentException("Nome da categoriaJpaEntity não pode ter mais de 50 caracteres");
        }
        return nome.trim();
    }

    public CategoriaId getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public boolean isAtiva() { return ativa; }
    public String getIcone() { return icone; }
    public UsuarioId getUsuarioId() { return usuarioId; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public CategoriaId getCategoriaPaiId() { return categoriaPaiId; }
    public Set<CategoriaId> getSubcategoriaIds() { return subcategoriaIds; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return Objects.equals(id, categoria.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Builder {
        private CategoriaId id;
        private String nome;
        private String descricao;
        private boolean ativa = true;
        private String icone;
        private UsuarioId usuarioId;
        private LocalDateTime criadoEm;
        private LocalDateTime atualizadoEm;
        private CategoriaId categoriaPaiId;
        private Set<CategoriaId> subcategoriaIds;

        public Builder id(CategoriaId id) { this.id = id; return this; }
        public Builder nome(String nome) { this.nome = nome; return this; }
        public Builder descricao(String descricao) { this.descricao = descricao; return this; }
        public Builder ativa(boolean ativa) { this.ativa = ativa; return this; }
        public Builder icone(String icone) { this.icone = icone; return this; }
        public Builder usuarioId(UsuarioId usuarioId) { this.usuarioId = usuarioId; return this; }
        public Builder criadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; return this; }
        public Builder atualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; return this; }
        public Builder categoriaPaiId(CategoriaId categoriaPaiId) { this.categoriaPaiId = categoriaPaiId; return this; }
        public Builder subcategoriaIds(Set<CategoriaId> subcategoriaIds) { this.subcategoriaIds = subcategoriaIds; return this; }

        public Builder from(Categoria categoria) {
            this.id = categoria.id;
            this.nome = categoria.nome;
            this.descricao = categoria.descricao;
            this.ativa = categoria.ativa;
            this.icone = categoria.icone;
            this.usuarioId = categoria.usuarioId;
            this.criadoEm = categoria.criadoEm;
            this.atualizadoEm = categoria.atualizadoEm;
            this.categoriaPaiId = categoria.categoriaPaiId;
            this.subcategoriaIds = categoria.subcategoriaIds;
            return this;
        }

        public Categoria build() {
            return new Categoria(this);
        }
    }
}