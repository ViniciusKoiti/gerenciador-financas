package com.vinicius.gerenciamento_financeiro.adapter.out.grafico;

import com.vinicius.gerenciamento_financeiro.domain.model.grafico.EvolucaoFinanceira;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.GraficoCategoria;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.ResumoFinanceiro;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MoedaId;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MontanteMonetario;
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
        return List.of();
    }

    @Override
    public List<GraficoCategoria> gerarGraficoPorCategoriaReceitas(UsuarioId usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        // TODO: Implementar mapeamento de GraficoResponse para GraficoCategoria
        return List.of();
    }

    @Override
    public List<GraficoCategoria> gerarGraficoPorCategoriaDespesas(UsuarioId usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        // TODO: Implementar mapeamento de GraficoResponse para GraficoCategoria
        return List.of();
    }

    @Override
    public List<EvolucaoFinanceira> gerarEvolucaoFinanceira(UsuarioId usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        // TODO: Implementar mapeamento de TransacaoPorPeriodoResponse para EvolucaoFinanceira
        return List.of();
    }

    @Override
    public ResumoFinanceiro gerarResumoFinanceiro(UsuarioId usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        // TODO: Implementar mapeamento de ResumoFinanceiroResponse para ResumoFinanceiro

       MoedaId moedaBrl =
            MoedaId.of("BRL");
            
        return ResumoFinanceiro.criar(
            dataInicio, 
            dataFim, 
            MontanteMonetario.zero(moedaBrl),
            MontanteMonetario.zero(moedaBrl),
            MontanteMonetario.zero(moedaBrl),
            0
        );
    }
}