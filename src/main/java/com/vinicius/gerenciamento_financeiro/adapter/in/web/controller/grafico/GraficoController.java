package com.vinicius.gerenciamento_financeiro.adapter.in.web.controller.grafico;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.ApiResponseSistema;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.ResumoFinanceiroResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.TransacaoPorPeriodoResponse;
import com.vinicius.gerenciamento_financeiro.domain.service.grafico.GraficoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/grafico")
@RequiredArgsConstructor
public class GraficoController {

    private final GraficoService graficoService;
    @Operation(summary = "Grafico de total por Categorias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "")
    })
    @GetMapping("/categoria")
    public ResponseEntity<ApiResponseSistema<List<GraficoResponse>>> graficoPorCategoria(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dataFim
    ){
        List<GraficoResponse> linhasDoGraficoPorCategoria = graficoService.gerarGraficoPorCategoria(dataInicio, dataFim);
        ApiResponseSistema<List<GraficoResponse>> response = new ApiResponseSistema<>(linhasDoGraficoPorCategoria, "Transações obtidas com sucesso.", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/evolucao")
    public ResponseEntity<ApiResponseSistema<List<TransacaoPorPeriodoResponse>>> gerarEvolucaoFinanceira(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dataFim) {

        List<TransacaoPorPeriodoResponse> resultado = graficoService.gerarEvolucaoFinanceira(dataInicio, dataFim);
        ApiResponseSistema<List<TransacaoPorPeriodoResponse>> response = new ApiResponseSistema<>(resultado, "Transações obtidas com sucesso.", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/resumo")
    public ResponseEntity<ApiResponseSistema<ResumoFinanceiroResponse>> gerarResumoFinanceiro(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dataFim) {

        ResumoFinanceiroResponse resultado = graficoService.gerarResumoFinanceiro(dataInicio, dataFim);
        ApiResponseSistema<ResumoFinanceiroResponse> response = new ApiResponseSistema<>(resultado, "Resumo financeiro obtido com sucesso.", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}
