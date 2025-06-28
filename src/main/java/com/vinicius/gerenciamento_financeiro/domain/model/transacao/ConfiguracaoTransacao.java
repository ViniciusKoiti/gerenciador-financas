package com.vinicius.gerenciamento_financeiro.domain.model.transacao;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoRecorrencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object para configuração de transação - DOMÍNIO PURO
 */
public final class ConfiguracaoTransacao {

    private final Boolean pago;
    private final Boolean recorrente;
    private final Integer periodicidade;
    private final TipoRecorrencia tipoRecorrencia;
    private final Boolean ignorarLimiteCategoria;
    private final Boolean ignorarOrcamento;
    private final Boolean parcelado;
    private final LocalDateTime dataPagamento;
    private final LocalDate dataVencimento;

    private ConfiguracaoTransacao(Builder builder) {
        this.pago = builder.pago != null ? builder.pago : false;
        this.recorrente = builder.recorrente != null ? builder.recorrente : false;
        this.periodicidade = validarPeriodicidade(builder.periodicidade, this.recorrente);
        this.tipoRecorrencia = validarTipoRecorrencia(builder.tipoRecorrencia, this.recorrente);
        this.ignorarLimiteCategoria = builder.ignorarLimiteCategoria != null ? builder.ignorarLimiteCategoria : false;
        this.ignorarOrcamento = builder.ignorarOrcamento != null ? builder.ignorarOrcamento : false;
        this.parcelado = builder.parcelado != null ? builder.parcelado : false;
        this.dataPagamento = builder.dataPagamento;
        this.dataVencimento = builder.dataVencimento != null ? builder.dataVencimento : LocalDate.now();

        validarInvariantes();
    }

    /**
     * Cria configuração padrão para transações simples
     */
    public static ConfiguracaoTransacao padrao() {
        return new Builder().build();
    }

    /**
     * Cria configuração para transação recorrente
     */
    public static ConfiguracaoTransacao recorrente(TipoRecorrencia tipo, Integer periodicidade) {
        return new Builder()
                .recorrente(true)
                .tipoRecorrencia(tipo)
                .periodicidade(periodicidade)
                .build();
    }

    /**
     * Cria configuração para transação parcelada
     */
    public static ConfiguracaoTransacao parcelada(Integer numeroParcelas) {
        return new Builder()
                .parcelado(true)
                .periodicidade(numeroParcelas)
                .build();
    }

    /**
     * Cria configuração com data de vencimento específica
     */
    public static ConfiguracaoTransacao comVencimento(LocalDate dataVencimento) {
        return new Builder()
                .dataVencimento(dataVencimento)
                .build();
    }

    /**
     * Marca esta configuração como paga
     */
    public ConfiguracaoTransacao marcarComoPaga() {
        if (this.pago) {
            return this; // Já está pago
        }

        return new Builder()
                .from(this)
                .pago(true)
                .dataPagamento(LocalDateTime.now())
                .build();
    }

    /**
     * Remove o pagamento (marca como não pago)
     */
    public ConfiguracaoTransacao desmarcarPagamento() {
        if (!this.pago) {
            return this; // Já não está pago
        }

        return new Builder()
                .from(this)
                .pago(false)
                .dataPagamento(null)
                .build();
    }

    /**
     * Atualiza a data de vencimento
     */
    public ConfiguracaoTransacao alterarVencimento(LocalDate novaDataVencimento) {
        if (Objects.equals(this.dataVencimento, novaDataVencimento)) {
            return this; // Nada mudou
        }

        return new Builder()
                .from(this)
                .dataVencimento(novaDataVencimento)
                .build();
    }

    /**
     * Verifica se a transação está vencida
     */
    public boolean estaVencida() {
        if (pago || dataVencimento == null) {
            return false;
        }
        return LocalDate.now().isAfter(dataVencimento);
    }

    /**
     * Verifica se é uma transação simples (não recorrente, não parcelada)
     */
    public boolean ehSimples() {
        return !recorrente && !parcelado;
    }

    /**
     * Calcula próxima data de vencimento para recorrência
     */
    public LocalDate calcularProximoVencimento() {
        if (!recorrente || tipoRecorrencia == null || dataVencimento == null) {
            return dataVencimento;
        }

        return switch (tipoRecorrencia) {
            case DIARIO -> dataVencimento.plusDays(periodicidade != null ? periodicidade : 1);
            case SEMANAL -> dataVencimento.plusWeeks(periodicidade != null ? periodicidade : 1);
            case QUINZENAL -> dataVencimento.plusWeeks(2 * (periodicidade != null ? periodicidade : 1));
            case MENSAL -> dataVencimento.plusMonths(periodicidade != null ? periodicidade : 1);
            case BIMESTRAL -> dataVencimento.plusMonths(2 * (periodicidade != null ? periodicidade : 1));
            case TRIMESTRAL -> dataVencimento.plusMonths(3 * (periodicidade != null ? periodicidade : 1));
            case SEMESTRAL -> dataVencimento.plusMonths(6 * (periodicidade != null ? periodicidade : 1));
            case ANUAL -> dataVencimento.plusYears(periodicidade != null ? periodicidade : 1);
        };
    }

    // ========== VALIDAÇÕES ==========

    private Integer validarPeriodicidade(Integer periodicidade, Boolean recorrente) {
        if (recorrente && (periodicidade == null || periodicidade <= 0)) {
            throw new IllegalArgumentException("Periodicidade deve ser positiva para transações recorrentes");
        }
        return periodicidade;
    }

    private TipoRecorrencia validarTipoRecorrencia(TipoRecorrencia tipo, Boolean recorrente) {
        if (recorrente && tipo == null) {
            throw new IllegalArgumentException("Tipo de recorrência é obrigatório para transações recorrentes");
        }
        return tipo;
    }

    private void validarInvariantes() {
        if (pago && dataPagamento == null) {
            throw new IllegalStateException("Transação marcada como paga deve ter data de pagamento");
        }

        if (dataPagamento != null && !pago) {
            throw new IllegalStateException("Transação com data de pagamento deve estar marcada como paga");
        }

        if (dataVencimento != null && dataPagamento != null &&
                dataPagamento.toLocalDate().isBefore(dataVencimento)) {
            throw new IllegalStateException("Data de pagamento não pode ser anterior ao vencimento");
        }
    }

    public Boolean getPago() {
        return pago;
    }

    public Boolean getRecorrente() {
        return recorrente;
    }

    public Integer getPeriodicidade() {
        return periodicidade;
    }

    public TipoRecorrencia getTipoRecorrencia() {
        return tipoRecorrencia;
    }

    public Boolean getIgnorarLimiteCategoria() {
        return ignorarLimiteCategoria;
    }

    public Boolean getIgnorarOrcamento() {
        return ignorarOrcamento;
    }

    public Boolean getParcelado() {
        return parcelado;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfiguracaoTransacao that = (ConfiguracaoTransacao) o;
        return Objects.equals(pago, that.pago) &&
                Objects.equals(recorrente, that.recorrente) &&
                Objects.equals(periodicidade, that.periodicidade) &&
                tipoRecorrencia == that.tipoRecorrencia &&
                Objects.equals(dataVencimento, that.dataVencimento) &&
                Objects.equals(dataPagamento, that.dataPagamento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pago, recorrente, periodicidade, tipoRecorrencia, dataVencimento, dataPagamento);
    }

    public static class Builder {
        private Boolean pago;
        private Boolean recorrente;
        private Integer periodicidade;
        private TipoRecorrencia tipoRecorrencia;
        private Boolean ignorarLimiteCategoria;
        private Boolean ignorarOrcamento;
        private Boolean parcelado;
        private LocalDateTime dataPagamento;
        private LocalDate dataVencimento;

        public Builder pago(Boolean pago) {
            this.pago = pago;
            return this;
        }

        public Builder recorrente(Boolean recorrente) {
            this.recorrente = recorrente;
            return this;
        }

        public Builder periodicidade(Integer periodicidade) {
            this.periodicidade = periodicidade;
            return this;
        }

        public Builder tipoRecorrencia(TipoRecorrencia tipoRecorrencia) {
            this.tipoRecorrencia = tipoRecorrencia;
            return this;
        }

        public Builder ignorarLimiteCategoria(Boolean ignorar) {
            this.ignorarLimiteCategoria = ignorar;
            return this;
        }

        public Builder ignorarOrcamento(Boolean ignorar) {
            this.ignorarOrcamento = ignorar;
            return this;
        }

        public Builder parcelado(Boolean parcelado) {
            this.parcelado = parcelado;
            return this;
        }

        public Builder dataPagamento(LocalDateTime dataPagamento) {
            this.dataPagamento = dataPagamento;
            return this;
        }

        public Builder dataVencimento(LocalDate dataVencimento) {
            this.dataVencimento = dataVencimento;
            return this;
        }

        public Builder from(ConfiguracaoTransacao config) {
            this.pago = config.pago;
            this.recorrente = config.recorrente;
            this.periodicidade = config.periodicidade;
            this.tipoRecorrencia = config.tipoRecorrencia;
            this.ignorarLimiteCategoria = config.ignorarLimiteCategoria;
            this.ignorarOrcamento = config.ignorarOrcamento;
            this.parcelado = config.parcelado;
            this.dataPagamento = config.dataPagamento;
            this.dataVencimento = config.dataVencimento;
            return this;
        }

        public ConfiguracaoTransacao build() {
            return new ConfiguracaoTransacao(this);
        }
    }
}