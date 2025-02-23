package com.vinicius.gerenciamento_financeiro.adapter.out.grafico;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;
import com.vinicius.gerenciamento_financeiro.adapter.out.transacao.JpaTransacaoRepository;
import com.vinicius.gerenciamento_financeiro.port.out.grafico.GraficoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
@Component
@RequiredArgsConstructor
public class GraficoPersistenceAdapter implements GraficoRepository {

    private final JpaGraficoRepository jpaGraficoRepository;
    @Override
    public List<GraficoResponse> gerarGraficoPorCategoria(Long usuarioId, ZonedDateTime dataInicio, ZonedDateTime dataFim) {
        return jpaGraficoRepository.gerarGraficoPorCategoria(usuarioId, dataInicio, dataFim);
    }
}
