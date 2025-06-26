package com.vinicius.gerenciamento_financeiro.domain.model.categoria;

import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import java.time.LocalDateTime;
import java.util.Objects;

public class Categoria {
    private final CategoriaId id;
    private final String nome;
    private final String descricao;
    private final boolean ativa;
    private final String icone;
    private final UsuarioId usuarioId;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;

    private final Categoria categoriaPai;

    private Categoria(Builder builder) {
        this.id = builder.id;
        this.nome = validarNome(builder.nome);
        this.descricao = builder.descricao;
        this.ativa = builder.ativa;
        this.icone = builder.icone;
        this.usuarioId = Objects.requireNonNull(builder.usuarioId, "UsuarioId não pode ser nulo");
        this.criadoEm = builder.criadoEm != null ? builder.criadoEm : LocalDateTime.now();
        this.atualizadoEm = builder.atualizadoEm;
        this.categoriaPai = builder.categoriaPai;
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

    public static Categoria reconstituir(CategoriaId id, String nome, String descricao,
                                         boolean ativa, String icone, UsuarioId usuarioId,
                                         LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        return new Builder()
                .id(id)
                .nome(nome)
                .descricao(descricao)
                .ativa(ativa)
                .icone(icone)
                .usuarioId(usuarioId)
                .criadoEm(criadoEm)
                .atualizadoEm(atualizadoEm)
                .build();
    }

    public Categoria desativar() {
        if (!this.ativa) {
            throw new IllegalStateException("Categoria já está desativada");
        }

        return new Builder()
                .from(this)
                .ativa(false)
                .atualizadoEm(LocalDateTime.now())
                .build();
    }

    public Categoria atualizarInformacoes(String novoNome, String novaDescricao, String novoIcone) {
        return new Builder()
                .from(this)
                .nome(novoNome)
                .descricao(novaDescricao)
                .icone(novoIcone)
                .atualizadoEm(LocalDateTime.now())
                .build();
    }

    public boolean pertenceAoUsuario(UsuarioId usuarioId) {
        return this.usuarioId.equals(usuarioId);
    }

    private String validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria não pode ser vazio");
        }
        if (nome.length() > 50) {
            throw new IllegalArgumentException("Nome da categoria não pode ter mais de 50 caracteres");
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

        private Categoria categoriaPai;

        public Builder id(CategoriaId id) { this.id = id; return this; }
        public Builder nome(String nome) { this.nome = nome; return this; }
        public Builder descricao(String descricao) { this.descricao = descricao; return this; }
        public Builder ativa(boolean ativa) { this.ativa = ativa; return this; }
        public Builder icone(String icone) { this.icone = icone; return this; }
        public Builder usuarioId(UsuarioId usuarioId) { this.usuarioId = usuarioId; return this; }
        public Builder criadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; return this; }
        public Builder atualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; return this; }
        public Builder categoriaPai(Categoria categoria) { this.categoriaPai = categoriaPai; return this; }

        public Builder from(Categoria categoria) {
            this.id = categoria.id;
            this.nome = categoria.nome;
            this.descricao = categoria.descricao;
            this.ativa = categoria.ativa;
            this.icone = categoria.icone;
            this.usuarioId = categoria.usuarioId;
            this.criadoEm = categoria.criadoEm;
            this.atualizadoEm = categoria.atualizadoEm;
            return this;
        }

        public Categoria build() {
            return new Categoria(this);
        }
    }
}