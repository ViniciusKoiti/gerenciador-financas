package com.vinicius.gerenciamento_financeiro.domain.service.grafico;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;
import com.vinicius.gerenciamento_financeiro.port.in.GerarGraficoUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.grafico.GraficoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class GraficoService implements GerarGraficoUseCase {

    private final GraficoRepository graficoRepository;



    @Override
    public List<GraficoResponse> gerarGraficoPorCategoria(ZonedDateTime dataInicio, ZonedDateTime dataFim) {
        return null;
    }
}
