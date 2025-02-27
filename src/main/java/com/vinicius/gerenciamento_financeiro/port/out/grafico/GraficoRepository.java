package com.vinicius.gerenciamento_financeiro.port.out.grafico;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
@Repository
public interface GraficoRepository {
    List<GraficoResponse> gerarGraficoPorCategoria(Long usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim);
}
