package com.vinicius.gerenciamento_financeiro.port.out.grafico;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;

import java.time.ZonedDateTime;
import java.util.List;

public interface GraficoRepository {
    List<GraficoResponse> gerarGraficoPorCategoria(Long usuarioId, ZonedDateTime dataInicio, ZonedDateTime dataFim);
}
