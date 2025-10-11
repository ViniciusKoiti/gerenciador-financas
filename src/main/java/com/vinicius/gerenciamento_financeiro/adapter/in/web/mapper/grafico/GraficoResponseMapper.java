package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.grafico;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.ResumoFinanceiroResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.TransacaoPorPeriodoResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.EvolucaoFinanceira;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.GraficoCategoria;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.ResumoFinanceiro;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GraficoResponseMapper {

    public GraficoResponse toResponse(GraficoCategoria graficoCategoria) {
        return new GraficoResponse(
                graficoCategoria.getNomeCategoria(),
                graficoCategoria.getValor().getValor()
        );
    }

    public List<GraficoResponse> toResponseList(List<GraficoCategoria> graficosCategorias) {
        return graficosCategorias.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TransacaoPorPeriodoResponse toTransacaoPorPeriodoResponse(EvolucaoFinanceira evolucaoFinanceira) {
        return new TransacaoPorPeriodoResponse(
                evolucaoFinanceira.getPeriodo().toString(),
                evolucaoFinanceira.getReceitas().getValor(),
                evolucaoFinanceira.getDespesas().getValor()
        );
    }

    public List<TransacaoPorPeriodoResponse> toTransacaoPorPeriodoResponseList(List<EvolucaoFinanceira> evolucaoFinanceiras) {
        return evolucaoFinanceiras.stream()
                .map(this::toTransacaoPorPeriodoResponse)
                .collect(Collectors.toList());
    }

    public ResumoFinanceiroResponse toResponse(ResumoFinanceiro resumoFinanceiro) {
        return new ResumoFinanceiroResponse(
                resumoFinanceiro.getTotalReceitas().getValor(),
                resumoFinanceiro.getTotalDespesas().getValor(),
                resumoFinanceiro.getSaldoFinal().getValor()
        );
    }
}