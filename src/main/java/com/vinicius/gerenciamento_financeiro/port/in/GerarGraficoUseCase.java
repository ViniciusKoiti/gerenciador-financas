package com.vinicius.gerenciamento_financeiro.port.in;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;

import java.time.ZonedDateTime;
import java.util.List;

public interface GerarGraficoUseCase {
    List<GraficoResponse> gerarGraficoPorCategoria(ZonedDateTime dataInicio, ZonedDateTime dataFim);
}
