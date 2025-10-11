package com.vinicius.gerenciamento_financeiro.adapter.out.grafico;

import com.vinicius.gerenciamento_financeiro.domain.model.grafico.EvolucaoFinanceira;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.GraficoCategoria;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.ResumoFinanceiro;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.out.grafico.GraficoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GraficoPersistenceAdapter implements GraficoRepository {

    private final JpaGraficoRepository jpaGraficoRepository;

    @Override
    public List<GraficoCategoria> gerarGraficoPorCategoria(UsuarioId usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        // TODO: Implementar mapeamento de GraficoResponse para GraficoCategoria
        // Por enquanto, retorna lista vazia para compilar
        return List.of();
    }

    @Override
    public List<GraficoCategoria> gerarGraficoPorCategoriaReceitas(UsuarioId usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        // TODO: Implementar mapeamento de GraficoResponse para GraficoCategoria
        // Por enquanto, retorna lista vazia para compilar
        return List.of();
    }

    @Override
    public List<GraficoCategoria> gerarGraficoPorCategoriaDespesas(UsuarioId usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        // TODO: Implementar mapeamento de GraficoResponse para GraficoCategoria
        // Por enquanto, retorna lista vazia para compilar
        return List.of();
    }

    @Override
    public List<EvolucaoFinanceira> gerarEvolucaoFinanceira(UsuarioId usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        // TODO: Implementar mapeamento de TransacaoPorPeriodoResponse para EvolucaoFinanceira
        // Por enquanto, retorna lista vazia para compilar
        return List.of();
    }

    @Override
    public ResumoFinanceiro gerarResumoFinanceiro(UsuarioId usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        // TODO: Implementar mapeamento de ResumoFinanceiroResponse para ResumoFinanceiro
        // Por enquanto, retorna um objeto mock para compilar
        // Criar uma moeda padrão BRL para o mock
        com.vinicius.gerenciamento_financeiro.domain.model.moeda.MoedaId moedaBrl = 
            com.vinicius.gerenciamento_financeiro.domain.model.moeda.MoedaId.of("BRL");
            
        return ResumoFinanceiro.criar(
            dataInicio, 
            dataFim, 
            com.vinicius.gerenciamento_financeiro.domain.model.moeda.MontanteMonetario.zero(moedaBrl),
            com.vinicius.gerenciamento_financeiro.domain.model.moeda.MontanteMonetario.zero(moedaBrl),
            com.vinicius.gerenciamento_financeiro.domain.model.moeda.MontanteMonetario.zero(moedaBrl),
            0
        );
    }
}