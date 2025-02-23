package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico;

import java.math.BigDecimal;

public record GraficoResponse(
        String name,
        BigDecimal value
        ) {

}