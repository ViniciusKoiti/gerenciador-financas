package com.vinicius.gerenciamento_financeiro.domain.model.transacao;

import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacao;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MontanteMonetario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MoedaId;

public final class Transacao {

    private final TransacaoId id;
    private final String descricao;

    @Deprecated(since = "3.5.0", forRemoval = true)
    private final BigDecimal valor;

    private final MontanteMonetario montante;

    private final TipoMovimentacao tipo;
    private final LocalDateTime data;
    private final UsuarioId usuarioId;
    private final ClienteId clienteId;
    private final CategoriaId categoriaId;
    private final ConfiguracaoTransacao configuracao;
    private final Auditoria auditoria;
    private final String observacoes;

    private Transacao(Builder b) {
        this.id = b.id;
        this.descricao = validarDescricao(b.descricao);
        MontanteMonetario resolved = b.montante;
        if (resolved == null && b.valor != null) {
            resolved = MontanteMonetario.of(b.valor, b.moedaPadrao != null ? b.moedaPadrao : MoedaId.of("BRL"));
        }
        this.montante = Objects.requireNonNull(resolved, "MontanteMonetario não pode ser nulo");

        this.valor = b.valor != null ? validarValor(b.valor) : this.montante.getValor();

        this.tipo = Objects.requireNonNull(b.tipo, "Tipo não pode ser nulo");
        this.data = Objects.requireNonNull(b.data, "Data não pode ser nula");
        this.usuarioId = Objects.requireNonNull(b.usuarioId, "UsuarioId não pode ser nulo");
        this.categoriaId = Objects.requireNonNull(b.categoriaId, "CategoriaId não pode ser nulo");
        this.clienteId = b.clienteId; // opcional
        this.configuracao = b.configuracao != null ? b.configuracao : ConfiguracaoTransacao.padrao();
        this.auditoria = b.auditoria != null ? b.auditoria : Auditoria.criarNova();
        this.observacoes = b.observacoes;
    }


    public static Transacao criarNova(String descricao, MontanteMonetario montante, TipoMovimentacao tipo,
                                      LocalDateTime data, CategoriaId categoriaId, UsuarioId usuarioId,
                                      ClienteId clienteId, ConfiguracaoTransacao configuracao, String observacoes) {
        return new Builder()
                .descricao(descricao)
                .montante(montante)
                .tipo(tipo)
                .data(data)
                .categoriaId(categoriaId)
                .usuarioId(usuarioId)
                .clienteId(clienteId)
                .configuracao(configuracao != null ? configuracao : ConfiguracaoTransacao.padrao())
                .auditoria(Auditoria.criarNova())
                .observacao(observacoes)
                .build();
    }

    public static Transacao criarNova(String descricao, MontanteMonetario montante, TipoMovimentacao tipo,
                                      LocalDateTime data, CategoriaId categoriaId, UsuarioId usuarioId) {
        return criarNova(descricao, montante, tipo, data, categoriaId, usuarioId, null, ConfiguracaoTransacao.padrao(), null);
    }


    @Deprecated(since = "3.5.0", forRemoval = true)
    public static Transacao criarNova(String descricao, BigDecimal valor, TipoMovimentacao tipo,
                                      LocalDateTime data, CategoriaId categoriaId, UsuarioId usuarioId,
                                      ClienteId clienteId, ConfiguracaoTransacao configuracao, String observacoes) {
        return new Builder()
                .descricao(descricao)
                .valor(valor)                 // legado
                .moedaPadrao(MoedaId.of("BRL"))
                .tipo(tipo)
                .data(data)
                .categoriaId(categoriaId)
                .usuarioId(usuarioId)
                .clienteId(clienteId)
                .configuracao(configuracao != null ? configuracao : ConfiguracaoTransacao.padrao())
                .auditoria(Auditoria.criarNova())
                .observacao(observacoes)
                .build();
    }


    public Transacao atualizarCategoria(CategoriaId novaCategoriaId) {
        if (novaCategoriaId == null) throw new IllegalArgumentException("Nova categoria não pode ser nula");
        if (this.categoriaId.equals(novaCategoriaId)) return this;

        return new Builder().from(this)
                .categoriaId(novaCategoriaId)
                .auditoria(this.auditoria.marcarComoAtualizado())
                .build();
    }

    public MontanteMonetario calcularValorComTaxa(BigDecimal percentualTaxa) {
        if (percentualTaxa == null || percentualTaxa.compareTo(BigDecimal.ZERO) < 0) return this.montante;
        var fator = BigDecimal.ONE.add(percentualTaxa.divide(BigDecimal.valueOf(100)));
        return this.montante.multiply(fator);
    }

    public boolean estaVencida() {
        if (configuracao.getDataVencimento() == null) return false;
        return LocalDateTime.now().isAfter(configuracao.getDataVencimento().atStartOfDay());
    }

    public boolean estaPaga() { return configuracao.getDataPagamento() != null; }

    public Transacao marcarComoPaga() {
        if (estaPaga()) return this;
        return new Builder().from(this)
                .configuracao(configuracao.marcarComoPaga())
                .auditoria(this.auditoria.marcarComoAtualizado())
                .build();
    }

    public boolean pertenceAoUsuario(UsuarioId usuarioId) { return this.usuarioId.equals(usuarioId); }
    public boolean ehDaCategoria(CategoriaId categoriaId) { return this.categoriaId.equals(categoriaId); }
    public boolean isNova() { return this.id == null; }

    public MontanteMonetario getValorEfetivo() {
        return switch (this.tipo) {
            case RECEITA -> this.montante;
            case DESPESA -> this.montante.negate();
            case TRANSFERENCIA -> MontanteMonetario.zero(this.montante.getMoedaId());
        };
    }


    public TransacaoId getId() { return id; }
    public String getDescricao() { return descricao; }

    @Deprecated(since = "3.5.0", forRemoval = true)
    public BigDecimal getValor() { return valor; }

    public MontanteMonetario getMontante() { return montante; }

    public TipoMovimentacao getTipo() { return tipo; }
    public LocalDateTime getData() { return data; }
    public UsuarioId getUsuarioId() { return usuarioId; }
    public ClienteId getClienteId() { return clienteId; }
    public CategoriaId getCategoriaId() { return categoriaId; }
    public ConfiguracaoTransacao getConfiguracao() { return configuracao; }
    public Auditoria getAuditoria() { return auditoria; }
    public String getObservacoes() { return observacoes; }

    private String validarDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) throw new IllegalArgumentException("Descrição não pode ser vazia");
        if (descricao.length() > 255) throw new IllegalArgumentException("Descrição não pode ter mais de 255 caracteres");
        return descricao.trim();
    }

    private BigDecimal validarValor(BigDecimal valor) {
        if (valor == null) throw new IllegalArgumentException("Valor não pode ser nulo");
        if (valor.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Valor deve ser positivo");
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transacao that)) return false;
        if (id != null && that.id != null) return Objects.equals(id, that.id);
        return Objects.equals(descricao, that.descricao)
                && Objects.equals(montante, that.montante)
                && Objects.equals(data, that.data)
                && Objects.equals(usuarioId, that.usuarioId);
    }

    @Override
    public int hashCode() {
        if (id != null) return Objects.hash(id);
        return Objects.hash(descricao, montante, data, usuarioId);
    }

    @Override
    public String toString() {
        return String.format("Transacao{id=%s, descricao='%s', montante=%s, tipo=%s, usuario=%s}",
                id != null ? id.getValue() : "nova", descricao, montante, tipo, usuarioId.getValue());
    }

    public static class Builder {
        private TransacaoId id;
        private String descricao;

        /** LEGADO (deprecado) */
        @Deprecated(since = "3.5.0", forRemoval = true)
        private BigDecimal valor;

        /** NOVO */
        private MontanteMonetario montante;

        private TipoMovimentacao tipo;
        private LocalDateTime data;
        private UsuarioId usuarioId;
        private CategoriaId categoriaId;
        private ClienteId clienteId;
        private ConfiguracaoTransacao configuracao;
        private Auditoria auditoria;
        private String observacoes;

        /** moeda padrão para converter chamadas legadas em VO */
        private MoedaId moedaPadrao;

        public Builder id(TransacaoId id) { this.id = id; return this; }
        public Builder descricao(String descricao) { this.descricao = descricao; return this; }

        /** LEGADO: prefira montante(). Mantido só para compat. */
        @Deprecated(since = "3.5.0", forRemoval = true)
        public Builder valor(BigDecimal valor) { this.valor = valor; return this; }

        public Builder montante(MontanteMonetario montante) { this.montante = montante; return this; }
        public Builder tipo(TipoMovimentacao tipo) { this.tipo = tipo; return this; }
        public Builder data(LocalDateTime data) { this.data = data; return this; }
        public Builder usuarioId(UsuarioId usuarioId) { this.usuarioId = usuarioId; return this; }
        public Builder categoriaId(CategoriaId categoriaId) { this.categoriaId = categoriaId; return this; }
        public Builder clienteId(ClienteId clienteId) { this.clienteId = clienteId; return this; }
        public Builder configuracao(ConfiguracaoTransacao configuracao) { this.configuracao = configuracao; return this; }
        public Builder auditoria(Auditoria auditoria) { this.auditoria = auditoria; return this; }
        public Builder observacao(String observacoes) { this.observacoes = observacoes; return this; }

        /** Define moeda padrão para converter legado -> VO (ex.: BRL) */
        public Builder moedaPadrao(MoedaId moedaPadrao) { this.moedaPadrao = moedaPadrao; return this; }

        public Builder from(Transacao t) {
            this.id = t.id;
            this.descricao = t.descricao;
            this.montante = t.montante;
            this.valor = t.valor; // legado
            this.tipo = t.tipo;
            this.data = t.data;
            this.usuarioId = t.usuarioId;
            this.clienteId = t.clienteId;
            this.categoriaId = t.categoriaId;
            this.configuracao = t.configuracao;
            this.auditoria = t.auditoria;
            this.observacoes = t.observacoes;
            return this;
        }

        public Transacao build() { return new Transacao(this); }
    }
}
