package com.vinicius.gerenciamento_financeiro.domain.model.transacao;

import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;


public final class Transacao {

    private final TransacaoId id;
    private final String descricao;
    private final BigDecimal valor;
    private final TipoMovimentacao tipo;
    private final LocalDateTime data;
    private final UsuarioId usuarioId;
    private final CategoriaId categoriaId;
    private final ConfiguracaoTransacao configuracao;
    private final Auditoria auditoria;

    private Transacao(Builder builder) {
        this.id = builder.id;
        this.descricao = validarDescricao(builder.descricao);
        this.valor = validarValor(builder.valor);
        this.tipo = Objects.requireNonNull(builder.tipo, "Tipo não pode ser nulo");
        this.data = Objects.requireNonNull(builder.data, "Data não pode ser nula");
        this.usuarioId = Objects.requireNonNull(builder.usuarioId, "UsuarioId não pode ser nulo");
        this.categoriaId = Objects.requireNonNull(builder.categoriaId, "CategoriaId não pode ser nulo");
        this.configuracao = builder.configuracao != null ? builder.configuracao : ConfiguracaoTransacao.padrao();
        this.auditoria = builder.auditoria != null ? builder.auditoria : Auditoria.criarNova();
    }

    public static Transacao criarNova(String descricao, BigDecimal valor, TipoMovimentacao tipo,
                                      LocalDateTime data, CategoriaId categoriaId, UsuarioId usuarioId) {
        return new Builder()
                .descricao(descricao)
                .valor(valor)
                .tipo(tipo)
                .data(data)
                .categoriaId(categoriaId)
                .usuarioId(usuarioId)
                .build();
    }

    public static Transacao reconstituir(Long id, String descricao, BigDecimal valor, TipoMovimentacao tipo,
                                         LocalDateTime data, UsuarioId usuarioId, CategoriaId categoriaId,
                                         ConfiguracaoTransacao configuracao, Auditoria auditoria) {
        return new Builder()
                .id(TransacaoId.of(id))
                .descricao(descricao)
                .valor(valor)
                .tipo(tipo)
                .data(data)
                .usuarioId(usuarioId)
                .categoriaId(categoriaId)
                .configuracao(configuracao)
                .auditoria(auditoria)
                .build();
    }

    public Transacao atualizarCategoria(CategoriaId novaCategoriaId) {
        if (novaCategoriaId == null) {
            throw new IllegalArgumentException("Nova categoria não pode ser nula");
        }

        if (this.categoriaId.equals(novaCategoriaId)) {
            return this; // Nada mudou
        }

        return new Builder()
                .from(this)
                .categoriaId(novaCategoriaId)
                .auditoria(this.auditoria.marcarComoAtualizado())
                .build();
    }

    public BigDecimal calcularValorComTaxa(BigDecimal percentualTaxa) {
        if (percentualTaxa == null || percentualTaxa.compareTo(BigDecimal.ZERO) < 0) {
            return this.valor;
        }

        BigDecimal taxa = this.valor.multiply(percentualTaxa.divide(BigDecimal.valueOf(100)));
        return this.valor.add(taxa);
    }


    public boolean estaVencida() {
        if (configuracao.getDataVencimento() == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(configuracao.getDataVencimento().atStartOfDay());
    }



    public boolean estaPaga() {
        return configuracao.getDataPagamento() != null;
    }


    public Transacao marcarComoPaga() {
        if (estaPaga()) {
            return this;
        }

        ConfiguracaoTransacao novaConfiguracao = configuracao.marcarComoPaga();

        return new Builder()
                .from(this)
                .configuracao(novaConfiguracao)
                .auditoria(this.auditoria.marcarComoAtualizado())
                .build();
    }

    public boolean pertenceAoUsuario(UsuarioId usuarioId) {
        return this.usuarioId.equals(usuarioId);
    }

    public boolean ehDaCategoria(CategoriaId categoriaId) {
        return this.categoriaId.equals(categoriaId);
    }

    public boolean isNova() {
        return this.id == null;
    }

    public BigDecimal getValorEfetivo() {
        return switch (this.tipo) {
            case RECEITA -> this.valor;
            case DESPESA -> this.valor.negate();
            case TRANSFERENCIA -> BigDecimal.ZERO;
        };
    }

    public TransacaoId getId() { return id; }
    public String getDescricao() { return descricao; }
    public BigDecimal getValor() { return valor; }
    public TipoMovimentacao getTipo() { return tipo; }
    public LocalDateTime getData() { return data; }
    public UsuarioId getUsuarioId() { return usuarioId; }
    public CategoriaId getCategoriaId() { return categoriaId; }
    public ConfiguracaoTransacao getConfiguracao() { return configuracao; }
    public Auditoria getAuditoria() { return auditoria; }

    private String validarDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição não pode ser vazia");
        }
        if (descricao.length() > 255) {
            throw new IllegalArgumentException("Descrição não pode ter mais de 255 caracteres");
        }
        return descricao.trim();
    }

    private BigDecimal validarValor(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Valor não pode ser nulo");
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transacao transacao = (Transacao) o;
        if (id != null && transacao.id != null) {
            return Objects.equals(id, transacao.id);
        }
        // Para transações sem ID, comparar por atributos únicos
        return Objects.equals(descricao, transacao.descricao) &&
                Objects.equals(valor, transacao.valor) &&
                Objects.equals(data, transacao.data) &&
                Objects.equals(usuarioId, transacao.usuarioId);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(descricao, valor, data, usuarioId);
    }

    @Override
    public String toString() {
        return String.format("Transacao{id=%s, descricao='%s', valor=%s, tipo=%s, usuario=%s}",
                id != null ? id.getValue() : "nova",
                descricao,
                valor,
                tipo,
                usuarioId.getValue());
    }

    public static class Builder {
        private TransacaoId id;
        private String descricao;
        private BigDecimal valor;
        private TipoMovimentacao tipo;
        private LocalDateTime data;
        private UsuarioId usuarioId;
        private CategoriaId categoriaId;
        private ConfiguracaoTransacao configuracao;
        private Auditoria auditoria;

        public Builder id(TransacaoId id) { this.id = id; return this; }
        public Builder descricao(String descricao) { this.descricao = descricao; return this; }
        public Builder valor(BigDecimal valor) { this.valor = valor; return this; }
        public Builder tipo(TipoMovimentacao tipo) { this.tipo = tipo; return this; }
        public Builder data(LocalDateTime data) { this.data = data; return this; }
        public Builder usuarioId(UsuarioId usuarioId) { this.usuarioId = usuarioId; return this; }
        public Builder categoriaId(CategoriaId categoriaId) { this.categoriaId = categoriaId; return this; }
        public Builder configuracao(ConfiguracaoTransacao configuracao) { this.configuracao = configuracao; return this; }
        public Builder auditoria(Auditoria auditoria) { this.auditoria = auditoria; return this; }

        public Builder from(Transacao transacao) {
            this.id = transacao.id;
            this.descricao = transacao.descricao;
            this.valor = transacao.valor;
            this.tipo = transacao.tipo;
            this.data = transacao.data;
            this.usuarioId = transacao.usuarioId;
            this.categoriaId = transacao.categoriaId;
            this.configuracao = transacao.configuracao;
            this.auditoria = transacao.auditoria;
            return this;
        }

        public Transacao build() {
            return new Transacao(this);
        }
    }
}