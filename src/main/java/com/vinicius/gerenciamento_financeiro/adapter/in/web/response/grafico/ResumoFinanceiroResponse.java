package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumoFinanceiroResponse {
    private BigDecimal totalReceitas;
    private BigDecimal totalDespesas;
    private BigDecimal saldo;
    public BigDecimal getSaldoTotal() {
        if (totalReceitas == null) totalReceitas = BigDecimal.ZERO;
        if (totalDespesas == null) totalDespesas = BigDecimal.ZERO;

        return totalReceitas.subtract(totalDespesas);
    }

    ResumoFinanceiroResponse(Number totalReceita, Number totalDespesa){
        this.totalReceitas = totalReceitas != null ? new BigDecimal(totalReceitas.toString()) : BigDecimal.ZERO;
        this.totalDespesas = totalDespesas != null ? new BigDecimal(totalDespesas.toString()) : BigDecimal.ZERO;
        this.saldo = this.totalReceitas.subtract(this.totalDespesas);
    }


    public ResumoFinanceiroResponse(Number totalReceita, Number totalDespesa, Number saldo) {
        this.totalReceitas = totalReceita != null ? new BigDecimal(totalReceita.toString()) : BigDecimal.ZERO;
        this.totalDespesas = totalDespesa != null ? new BigDecimal(totalDespesa.toString()) : BigDecimal.ZERO;
        this.saldo = saldo != null ? new BigDecimal(saldo.toString()) : this.totalReceitas.subtract(this.totalDespesas);
    }
}
