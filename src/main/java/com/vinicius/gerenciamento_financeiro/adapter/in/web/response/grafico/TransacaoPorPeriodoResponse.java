package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransacaoPorPeriodoResponse {
    private String dataPeriodo;
    private BigDecimal receitaValor;
    private BigDecimal despesaValor;

    public BigDecimal getSaldoValor() {
        return receitaValor.subtract(despesaValor);
    }
    public TransacaoPorPeriodoResponse(String dataPeriodo, Number receitaValor, Number despesaValor) {
        this.dataPeriodo = dataPeriodo;
        this.receitaValor = receitaValor != null ? new BigDecimal(receitaValor.toString()) : BigDecimal.ZERO;
        this.despesaValor = despesaValor != null ? new BigDecimal(despesaValor.toString()) : BigDecimal.ZERO;
    }
}
