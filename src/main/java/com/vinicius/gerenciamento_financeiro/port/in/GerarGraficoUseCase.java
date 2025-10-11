package com.vinicius.gerenciamento_financeiro.port.in;

import com.vinicius.gerenciamento_financeiro.domain.model.grafico.EvolucaoFinanceira;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.GraficoCategoria;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.ResumoFinanceiro;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Port de entrada para geração de gráficos.
 * Define contratos usando APENAS tipos de domínio.
 */
public interface GerarGraficoUseCase {

    /**
     * Gera gráfico de despesas por categoria
     */
    List<GraficoCategoria> gerarGraficoTotalPorCategoria(ZonedDateTime dataInicio, ZonedDateTime dataFim);

    /**
     * Gera dados de evolução financeira (receitas/despesas) ao longo do tempo
     */
    List<EvolucaoFinanceira> gerarEvolucaoFinanceira(ZonedDateTime dataInicio, ZonedDateTime dataFim);

    /**
     * Gera resumo financeiro com totais de receitas e despesas
     */
    ResumoFinanceiro gerarResumoFinanceiro(ZonedDateTime dataInicio, ZonedDateTime dataFim);

    /**
     * Gera gráfico de despesas por categoria
     */
    List<GraficoCategoria> gerarGraficoTotalPorCategoriaDespesa(ZonedDateTime dataInicio, ZonedDateTime dataFim);
}