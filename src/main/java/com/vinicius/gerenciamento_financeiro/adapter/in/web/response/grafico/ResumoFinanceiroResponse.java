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

    public BigDecimal getSaldoTotal() {
        if (totalReceitas == null) totalReceitas = BigDecimal.ZERO;
        if (totalDespesas == null) totalDespesas = BigDecimal.ZERO;

        return totalReceitas.subtract(totalDespesas);
    }

    ResumoFinanceiroResponse(Number totalReceita, Number totalDespesa){
        this.totalReceitas = totalReceitas != null ? new BigDecimal(totalReceitas.toString()) : BigDecimal.ZERO;
        this.totalDespesas = totalDespesas != null ? new BigDecimal(totalDespesas.toString()) : BigDecimal.ZERO;
    }
}
