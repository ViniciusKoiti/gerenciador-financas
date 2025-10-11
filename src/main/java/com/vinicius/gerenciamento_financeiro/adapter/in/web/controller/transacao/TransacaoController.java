package com.vinicius.gerenciamento_financeiro.adapter.in.web.controller.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.ApiResponseSistema;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.transacao.TransacaoRequestMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPut;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponseMapper;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.TransacaoId;
import com.vinicius.gerenciamento_financeiro.port.in.UsuarioAutenticadoPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.vinicius.gerenciamento_financeiro.port.in.GerenciarTransacaoUseCase;

import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
@RequiredArgsConstructor
public class TransacaoController {

    private final GerenciarTransacaoUseCase gerenciarTransacaoUseCase;
    private final TransacaoRequestMapper requestMapper;
    private final TransacaoResponseMapper responseMapper;
    private final UsuarioAutenticadoPort usuarioAutenticadoPort;

    @Operation(summary = "Adicionar uma nova transação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<ApiResponseSistema<TransacaoResponse>> adicionarTransacao(@Valid @RequestBody TransacaoPost transacaoRequest) {
        var usuarioId = usuarioAutenticadoPort.obterUsuarioAtual();
        var transacao = requestMapper.toDomain(transacaoRequest, usuarioId);
        var transacaoSalva = gerenciarTransacaoUseCase.adicionarTransacao(transacao);
        var response = responseMapper.toResponse(transacaoSalva);
        ApiResponseSistema<TransacaoResponse> apiResponse = new ApiResponseSistema<>(response, "Transação adicionada com sucesso.", HttpStatus.CREATED.value());
        return ResponseEntity.ok(apiResponse);
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
        var usuarioId = usuarioAutenticadoPort.obterUsuarioAtual();
        var transacoes = gerenciarTransacaoUseCase.obterTodasTransacoes(usuarioId);
        List<TransacaoResponse> transacoesResponse = responseMapper.toResponseList(transacoes);
        ApiResponseSistema<List<TransacaoResponse>> response = new ApiResponseSistema<>(transacoesResponse, "Transações obtidas com sucesso.", HttpStatus.OK.value());
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
    @GetMapping("categorias/{categoriaId}")
    public ResponseEntity<ApiResponseSistema<List<TransacaoResponse>>> buscarTransacoesPorCategoriaId(@PathVariable Long categoriaId){
        var categoriaIdDomain = CategoriaId.of(categoriaId);
        var transacoes = gerenciarTransacaoUseCase.buscarTransacoesPorCategoriaId(categoriaIdDomain);
        List<TransacaoResponse> transacaoResponses = responseMapper.toResponseList(transacoes);
        ApiResponseSistema<List<TransacaoResponse>> response = new ApiResponseSistema<>(transacaoResponses, "Transações obtidas com sucesso.", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualiza CategoriaJpaEntity da Transação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Atualizado com sucesso")
    })
    @PatchMapping("/{transacaoId}")
    public ResponseEntity<ApiResponseSistema<Void>> atualizarTransacaoCategoria(@PathVariable Long transacaoId, @RequestBody CategoriaPut categoriaId){
        var transacaoIdDomain = TransacaoId.of(transacaoId);
        var categoriaIdDomain = CategoriaId.of(categoriaId.id());
        gerenciarTransacaoUseCase.atualizarTransacaoCategoria(categoriaIdDomain, transacaoIdDomain);
        ApiResponseSistema<Void> response = new ApiResponseSistema<>(null, "Transação atualizada com sucesso.", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

}
