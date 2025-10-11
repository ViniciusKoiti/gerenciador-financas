package com.vinicius.gerenciamento_financeiro.port.out.grafico;

import com.vinicius.gerenciamento_financeiro.domain.model.grafico.EvolucaoFinanceira;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.GraficoCategoria;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.ResumoFinanceiro;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Port de saída para operações de gráficos e relatórios.
 * Define contratos usando APENAS tipos de domínio.
 */
public interface GraficoRepository {
    
    /**
     * Gera gráfico de gastos por categoria
     */
    List<GraficoCategoria> gerarGraficoPorCategoria(UsuarioId usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Gera gráfico de receitas por categoria
     */
    List<GraficoCategoria> gerarGraficoPorCategoriaReceitas(UsuarioId usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Gera gráfico de despesas por categoria
     */
    List<GraficoCategoria> gerarGraficoPorCategoriaDespesas(UsuarioId usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim);
    
    /**
     * Gera evolução financeira ao longo do tempo
     */
    List<EvolucaoFinanceira> gerarEvolucaoFinanceira(UsuarioId usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Gera resumo financeiro consolidado
     */
    ResumoFinanceiro gerarResumoFinanceiro(UsuarioId usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim);
}
