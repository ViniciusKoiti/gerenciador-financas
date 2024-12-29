package com.vinicius.gerenciamento_financeiro.adapter.in.web.controller.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.ApiResponseSistema;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.port.in.CategoriaUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private CategoriaUseCase categoriaUseCase;

    @Operation(summary = "Salva Categoria ")
    @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "201",
                  description = "Categoria criada com sucesso",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ApiResponseSistema.class)
                  )
          ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseSistema.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponseSistema<CategoriaResponse>> save(CategoriaPost categoriaPost){
        CategoriaResponse categoria = categoriaUseCase.save(categoriaPost);
        ApiResponseSistema<CategoriaResponse> response = new ApiResponseSistema<>(
                categoria,
                "Categoria criada com sucesso.",
                HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Busca todas as categorias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Buscar todas as categorias",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseSistema.class)
            )),
            @ApiResponse(responseCode = "400",description = "Erro ao buscar as categorias",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseSistema.class)
                    ))
    })

   @GetMapping("/all")
    public ResponseEntity<ApiResponseSistema<List<CategoriaResponse>>> findAll(){
        List<CategoriaResponse> categoriaResponses = categoriaUseCase.findAll();
        ApiResponseSistema<List<CategoriaResponse>> response = new ApiResponseSistema<>(categoriaResponses, "Categorias obtidas com sucesso.", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Busca as categorias de forma paginada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Buscou as categorias",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseSistema.class)
            )),
            @ApiResponse(responseCode = "400",description = "Erro ao buscar as categorias",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseSistema.class)
                    )),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso não autorizado"
            ),
    })
    @GetMapping
    public ResponseEntity<ApiResponseSistema<Page<CategoriaResponse>>> findAllPaged( @PageableDefault(size = 10) Pageable pageable){
        Page<CategoriaResponse> categoriaResponses = categoriaUseCase.findAllPaginated(pageable);
        ApiResponseSistema<Page<CategoriaResponse>> response = new ApiResponseSistema<>(categoriaResponses, "Categorias obtidas com sucesso.", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Busca a categoria por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Encontrou a categoria ",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseSistema.class)
            )),
            @ApiResponse(responseCode = "400",description = "Erro ao buscar as categorias",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseSistema.class)
                    )),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso não autorizado"
            ),
            @ApiResponse(responseCode = "404",description = "Categoria com esse ID não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseSistema.class)
                    )),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseSistema<CategoriaResponse>> findById(@PathVariable String id) {
        CategoriaResponse categoriaResponse = categoriaUseCase.findById(id);
        ApiResponseSistema<CategoriaResponse> response = new ApiResponseSistema<>(categoriaResponse, "Categoria obtida com sucesso.", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}
