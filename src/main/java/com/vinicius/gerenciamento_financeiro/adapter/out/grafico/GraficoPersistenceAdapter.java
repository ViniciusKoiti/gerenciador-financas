package com.vinicius.gerenciamento_financeiro.adapter.out.grafico;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.ResumoFinanceiroResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.TransacaoPorPeriodoResponse;
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
    public List<GraficoResponse> gerarGraficoPorCategoria(Long usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return jpaGraficoRepository.gerarGraficoPorCategoria(usuarioId, dataInicio, dataFim);
    }

    @Override
    public List<TransacaoPorPeriodoResponse> gerarEvolucaoFinanceira(Long usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return jpaGraficoRepository.gerarEvolucaoFinanceiraMensal(usuarioId, dataInicio, dataFim);
    }

    @Override
    public ResumoFinanceiroResponse gerarResumoFinanceiro(Long usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return jpaGraficoRepository.gerarResumoFinanceiro(usuarioId, dataInicio, dataFim);
    }
}