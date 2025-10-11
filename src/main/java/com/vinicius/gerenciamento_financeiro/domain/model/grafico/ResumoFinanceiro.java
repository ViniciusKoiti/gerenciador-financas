package com.vinicius.gerenciamento_financeiro.domain.model.grafico;

import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MontanteMonetario;

import java.time.LocalDateTime;

/**
 * Value Object que representa um resumo financeiro consolidado
 */
public final class ResumoFinanceiro {
    
    private final LocalDateTime dataInicio;
    private final LocalDateTime dataFim;
    private final MontanteMonetario totalReceitas;
    private final MontanteMonetario totalDespesas;
    private final MontanteMonetario saldoFinal;
    private final Integer quantidadeTransacoes;
    
    public ResumoFinanceiro(LocalDateTime dataInicio, LocalDateTime dataFim,
                           MontanteMonetario totalReceitas, MontanteMonetario totalDespesas,
                           MontanteMonetario saldoFinal, Integer quantidadeTransacoes) {
        if (dataInicio == null) {
            throw new IllegalArgumentException("Data início não pode ser nula");
        }
        if (dataFim == null) {
            throw new IllegalArgumentException("Data fim não pode ser nula");
        }
        if (totalReceitas == null) {
            throw new IllegalArgumentException("Total de receitas não pode ser nulo");
        }
        if (totalDespesas == null) {
            throw new IllegalArgumentException("Total de despesas não pode ser nulo");
        }
        if (saldoFinal == null) {
            throw new IllegalArgumentException("Saldo final não pode ser nulo");
        }
        if (quantidadeTransacoes == null || quantidadeTransacoes < 0) {
            throw new IllegalArgumentException("Quantidade de transações deve ser não negativa");
        }
        
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.totalReceitas = totalReceitas;
        this.totalDespesas = totalDespesas;
        this.saldoFinal = saldoFinal;
        this.quantidadeTransacoes = quantidadeTransacoes;
    }
    
    public static ResumoFinanceiro criar(LocalDateTime dataInicio, LocalDateTime dataFim,
                                        MontanteMonetario totalReceitas, MontanteMonetario totalDespesas,
                                        MontanteMonetario saldoFinal, Integer quantidadeTransacoes) {
        return new ResumoFinanceiro(dataInicio, dataFim, totalReceitas, totalDespesas, 
                                   saldoFinal, quantidadeTransacoes);
    }
    
    public LocalDateTime getDataInicio() {
        return dataInicio;
    }
    
    public LocalDateTime getDataFim() {
        return dataFim;
    }
    
    public MontanteMonetario getTotalReceitas() {
        return totalReceitas;
    }
    
    public MontanteMonetario getTotalDespesas() {
        return totalDespesas;
    }
    
    public MontanteMonetario getSaldoFinal() {
        return saldoFinal;
    }
    
    public Integer getQuantidadeTransacoes() {
        return quantidadeTransacoes;
    }
    
    public boolean isResultadoPositivo() {
        return saldoFinal.getValor().compareTo(java.math.BigDecimal.ZERO) > 0;
    }
    
    public boolean temTransacoes() {
        return quantidadeTransacoes > 0;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ResumoFinanceiro that = (ResumoFinanceiro) o;
        return dataInicio.equals(that.dataInicio) && dataFim.equals(that.dataFim);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(dataInicio, dataFim);
    }
    
    @Override
    public String toString() {
        return "ResumoFinanceiro{" +
                "período=" + dataInicio + " a " + dataFim +
                ", receitas=" + totalReceitas +
                ", despesas=" + totalDespesas +
                ", saldo=" + saldoFinal +
                ", transações=" + quantidadeTransacoes +
                '}';
    }
}