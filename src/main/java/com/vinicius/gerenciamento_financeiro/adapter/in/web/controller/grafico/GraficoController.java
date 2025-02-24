package com.vinicius.gerenciamento_financeiro.adapter.in.web.controller.grafico;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.ApiResponseSistema;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;
import com.vinicius.gerenciamento_financeiro.domain.service.grafico.GraficoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
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
    @GetMapping("/total/categoria")
    public ResponseEntity<ApiResponseSistema<List<GraficoResponse>>> graficoPorCategoria(
            @RequestParam ZonedDateTime dataInicio,
            @RequestParam ZonedDateTime dataFim
    ){
        List<GraficoResponse> linhasDoGraficoPorCategoria = graficoService.gerarGraficoPorCategoria(dataInicio, dataFim);
        ApiResponseSistema<List<GraficoResponse>> response = new ApiResponseSistema<>(linhasDoGraficoPorCategoria, "Transações obtidas com sucesso.", HttpStatus.OK.value());
        return ResponseEntity.ok(response);

    }
}
