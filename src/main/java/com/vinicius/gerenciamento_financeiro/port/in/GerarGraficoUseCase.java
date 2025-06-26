package com.vinicius.gerenciamento_financeiro.port.in;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.ResumoFinanceiroResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.TransacaoPorPeriodoResponse;

import java.time.ZonedDateTime;
import java.util.List;

public interface GerarGraficoUseCase {

    /**
     * Gera gráfico de despesas por categoriaJpaEntity
     */
    List<GraficoResponse> gerarGraficoPorCategoria(ZonedDateTime dataInicio, ZonedDateTime dataFim);

    /**
     * Gera dados de evolução financeira (receitas/despesas) ao longo do tempo
     */
    List<TransacaoPorPeriodoResponse> gerarEvolucaoFinanceira(ZonedDateTime dataInicio, ZonedDateTime dataFim);

    /**
     * Gera resumo financeiro com totais de receitas e despesas
     */
    ResumoFinanceiroResponse gerarResumoFinanceiro(ZonedDateTime dataInicio, ZonedDateTime dataFim);
}