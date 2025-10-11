package com.vinicius.gerenciamento_financeiro.adapter.in.web.controller.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.ApiResponseSistema;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.service.categoria.CategoriaWebService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operaÃ§Ãµes de Categoria.
 * ResponsÃ¡vel APENAS por adaptaÃ§Ã£o HTTP - delega tudo para Application Service.
 */
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaWebService categoriaWebService;

    @PostMapping
    @Operation(summary = "Cria uma nova categoria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados invÃ¡lidos")
    })
    public ResponseEntity<ApiResponseSistema<CategoriaResponse>> criar(
            @Valid @RequestBody CategoriaPost request) {
        
        CategoriaResponse response = categoriaWebService.criarCategoria(request);
        
        ApiResponseSistema<CategoriaResponse> apiResponse = new ApiResponseSistema<>(
            response,
            "Categoria criada com sucesso.",
            HttpStatus.CREATED.value()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/all")
    @Operation(summary = "Lista todas as categorias do usuÃ¡rio")
    public ResponseEntity<ApiResponseSistema<List<CategoriaResponse>>> listarTodas() {
        
        List<CategoriaResponse> responses = categoriaWebService.listarTodas();
        
        return ResponseEntity.ok(new ApiResponseSistema<>(
            responses,
            "Categorias listadas com sucesso.",
            HttpStatus.OK.value()
        ));
    }

    @GetMapping
    @Operation(summary = "Lista categorias paginadas")
    public ResponseEntity<ApiResponseSistema<Page<CategoriaResponse>>> listarPaginado(
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<CategoriaResponse> responses = categoriaWebService.listarPaginado(pageable);
        
        return ResponseEntity.ok(new ApiResponseSistema<>(
            responses,
            "Categorias listadas com sucesso.",
            HttpStatus.OK.value()
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca categoria por ID")
    public ResponseEntity<ApiResponseSistema<CategoriaResponse>> buscarPorId(
            @PathVariable String id) {
        
        CategoriaResponse response = categoriaWebService.buscarPorId(id);
        
        return ResponseEntity.ok(new ApiResponseSistema<>(
            response,
            "Categoria encontrada com sucesso.",
            HttpStatus.OK.value()
        ));
    }
    @GetMapping("/usuarios/{userId}/categorias")
    @Operation(summary = "Lista categorias por usuÃ¡rio")
    public ResponseEntity<ApiResponseSistema<List<CategoriaResponse>>> listarPorUsuario(
            @PathVariable Long userId) {
        
        List<CategoriaResponse> responses = categoriaWebService.listarPorUsuario(userId);
        
        return ResponseEntity.ok(new ApiResponseSistema<>(
            responses,
            "Categorias obtidas com sucesso.",
            HttpStatus.OK.value()
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma categoria")
    public ResponseEntity<ApiResponseSistema<Void>> deletar(@PathVariable String id) {
        
        categoriaWebService.deletarCategoria(id);
        
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
            new ApiResponseSistema<>(null, "Categoria removida com sucesso.", HttpStatus.NO_CONTENT.value())
        );
    }
}



