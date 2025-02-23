package com.vinicius.gerenciamento_financeiro.domain.service.grafico;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;
import com.vinicius.gerenciamento_financeiro.port.in.GerarGraficoUseCase;

import java.time.ZonedDateTime;
import java.util.List;

public class GraficoService implements GerarGraficoUseCase {
    @Override
    public List<GraficoResponse> gerarGraficoPorCategoria(Long usuarioId, ZonedDateTime dataInicio, ZonedDateTime dataFim) {
        return null;
    }
}
