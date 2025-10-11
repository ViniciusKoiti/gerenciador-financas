package com.vinicius.gerenciamento_financeiro.domain.model.grafico;

import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MontanteMonetario;

import java.time.LocalDateTime;

/**
 * Value Object que representa a evolução financeira em um período
 */
public final class EvolucaoFinanceira {
    
    private final LocalDateTime periodo;
    private final MontanteMonetario receitas;
    private final MontanteMonetario despesas;
    private final MontanteMonetario saldo;
    
    public EvolucaoFinanceira(LocalDateTime periodo, MontanteMonetario receitas, 
                             MontanteMonetario despesas, MontanteMonetario saldo) {
        if (periodo == null) {
            throw new IllegalArgumentException("Período não pode ser nulo");
        }
        if (receitas == null) {
            throw new IllegalArgumentException("Receitas não podem ser nulas");
        }
        if (despesas == null) {
            throw new IllegalArgumentException("Despesas não podem ser nulas");
        }
        if (saldo == null) {
            throw new IllegalArgumentException("Saldo não pode ser nulo");
        }
        
        this.periodo = periodo;
        this.receitas = receitas;
        this.despesas = despesas;
        this.saldo = saldo;
    }
    
    public static EvolucaoFinanceira criar(LocalDateTime periodo, MontanteMonetario receitas, 
                                          MontanteMonetario despesas, MontanteMonetario saldo) {
        return new EvolucaoFinanceira(periodo, receitas, despesas, saldo);
    }
    
    public LocalDateTime getPeriodo() {
        return periodo;
    }
    
    public MontanteMonetario getReceitas() {
        return receitas;
    }
    
    public MontanteMonetario getDespesas() {
        return despesas;
    }
    
    public MontanteMonetario getSaldo() {
        return saldo;
    }
    
    public boolean isSaldoPositivo() {
        return saldo.getValor().compareTo(java.math.BigDecimal.ZERO) > 0;
    }
    
    public boolean isSaldoNegativo() {
        return saldo.getValor().compareTo(java.math.BigDecimal.ZERO) < 0;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        EvolucaoFinanceira that = (EvolucaoFinanceira) o;
        return periodo.equals(that.periodo);
    }
    
    @Override
    public int hashCode() {
        return periodo.hashCode();
    }
    
    @Override
    public String toString() {
        return "EvolucaoFinanceira{" +
                "periodo=" + periodo +
                ", receitas=" + receitas +
                ", despesas=" + despesas +
                ", saldo=" + saldo +
                '}';
    }
}