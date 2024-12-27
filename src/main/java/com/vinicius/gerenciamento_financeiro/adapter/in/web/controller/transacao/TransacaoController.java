package com.vinicius.gerenciamento_financeiro.adapter.in.web.controller.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.ApiResponseSistema;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.vinicius.gerenciamento_financeiro.port.in.GerenciarTransacaoUseCase;

import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    private final GerenciarTransacaoUseCase gerenciarTransacaoUseCase;

    public TransacaoController(GerenciarTransacaoUseCase gerenciarTransacaoUseCase) {
        this.gerenciarTransacaoUseCase = gerenciarTransacaoUseCase;
    }

    @Operation(summary = "Adicionar uma nova transação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<ApiResponseSistema<Void>> adicionarTransacao(@Valid @RequestBody TransacaoPost transacaoRequest) {
        gerenciarTransacaoUseCase.adicionarTransacao(transacaoRequest);
        ApiResponseSistema<Void> response = new ApiResponseSistema<>(null, "Transação adicionada com sucesso.", HttpStatus.CREATED.value());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obter todas as transações")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transações obtidas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso não autorizado"
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponseSistema<List<TransacaoResponse>>> obterTransacoes() {
        List<TransacaoResponse> transacoes = gerenciarTransacaoUseCase.obterTodasTransacoes();
        ApiResponseSistema<List<TransacaoResponse>> response = new ApiResponseSistema<>(transacoes, "Transações obtidas com sucesso.", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

}
