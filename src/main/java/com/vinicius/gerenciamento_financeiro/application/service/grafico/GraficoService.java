package com.vinicius.gerenciamento_financeiro.application.service.grafico;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.ResumoFinanceiroResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.TransacaoPorPeriodoResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.in.GerarGraficoUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.grafico.GraficoRepository;
import com.vinicius.gerenciamento_financeiro.port.in.UsuarioAutenticadoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraficoService implements GerarGraficoUseCase {

    private final GraficoRepository graficoRepository;
    private final UsuarioAutenticadoPort usuarioAutenticadoPort;

    @Override
    public List<GraficoResponse> gerarGraficoTotalPorCategoria(ZonedDateTime dataInicio, ZonedDateTime dataFim) {
        UsuarioId usuarioId = usuarioAutenticadoPort.obterUsuarioAtual();

        LocalDateTime dataInicial = dataInicio.toLocalDateTime();
        LocalDateTime dataFinal = dataFim.toLocalDateTime();

        log.debug("Gerando gráfico por categoria: usuário={}, período={} a {}",
                usuarioId.getValue(), dataInicial, dataFinal);

        return graficoRepository.gerarGraficoPorCategoria(usuarioId.getValue(), dataInicial, dataFinal);
    }

    @Override
    public List<TransacaoPorPeriodoResponse> gerarEvolucaoFinanceira(ZonedDateTime dataInicio, ZonedDateTime dataFim) {
        UsuarioId usuarioId = usuarioAutenticadoPort.obterUsuarioAtual();

        LocalDateTime dataInicial = dataInicio.toLocalDateTime();
        LocalDateTime dataFinal = dataFim.toLocalDateTime();

        log.debug("Gerando evolução financeira: usuário={}, período={} a {}",
                usuarioId.getValue(), dataInicial, dataFinal);

        return graficoRepository.gerarEvolucaoFinanceira(usuarioId.getValue(), dataInicial, dataFinal);
    }

    @Override
    public ResumoFinanceiroResponse gerarResumoFinanceiro(ZonedDateTime dataInicio, ZonedDateTime dataFim) {
        UsuarioId usuarioId = usuarioAutenticadoPort.obterUsuarioAtual();

        LocalDateTime dataInicial = dataInicio.toLocalDateTime();
        LocalDateTime dataFinal = dataFim.toLocalDateTime();

        log.debug("Gerando resumo financeiro: usuário={}, período={} a {}",
                usuarioId.getValue(), dataInicial, dataFinal);

        return graficoRepository.gerarResumoFinanceiro(usuarioId.getValue(), dataInicial, dataFinal);
    }

    @Override
    public List<GraficoResponse> gerarGraficoTotalPorCategoriaDespesa(ZonedDateTime dataInicio, ZonedDateTime dataFim) {
        UsuarioId usuarioId = usuarioAutenticadoPort.obterUsuarioAtual();

        LocalDateTime dataInicial = dataInicio.toLocalDateTime();
        LocalDateTime dataFinal = dataFim.toLocalDateTime();

        log.debug("Gerando gráfico por categoria: usuário={}, período={} a {}",
                usuarioId.getValue(), dataInicial, dataFinal);

        return graficoRepository.gerarGraficoPorCategoriaDespesas(usuarioId.getValue(), dataInicial, dataFinal);
    }
}