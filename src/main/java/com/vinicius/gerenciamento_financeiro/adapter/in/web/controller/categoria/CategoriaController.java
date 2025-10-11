package com.vinicius.gerenciamento_financeiro.adapter.in.web.controller.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.ApiResponseSistema;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.CategoriaMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.port.in.CategoriaUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaMapper  categoriaMapper;
    private final CategoriaUseCase categoriaUseCase;

    @Operation(summary = "Salva Categoria ")
    @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "201",
                  description = "CategoriaJpaEntity criada com sucesso",
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
    public ResponseEntity<ApiResponseSistema<CategoriaResponse>> save(@Valid @RequestBody CategoriaPost categoriaPost){
        CategoriaResponse categoria = categoriaMapper.toResponse(categoriaUseCase.save(categoriaPost));
        ApiResponseSistema<CategoriaResponse> response = new ApiResponseSistema<>(
                categoria,
                "CategoriaJpaEntity criada com sucesso.",
                HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Busca todas as categoriaJpaEntities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Buscar todas as categoriaJpaEntities",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseSistema.class)
            )),
            @ApiResponse(responseCode = "400",description = "Erro ao buscar as categoriaJpaEntities",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseSistema.class)
                    ))
    })

   @GetMapping("/all")
    public ResponseEntity<ApiResponseSistema<List<CategoriaResponse>>> findAll(){
        List<CategoriaResponse> categoriaResponses = categoriaMapper.toResponseList(categoriaUseCase.findAll());
        ApiResponseSistema<List<CategoriaResponse>> response = new ApiResponseSistema<>(categoriaResponses, "Categorias obtidas com sucesso.", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Busca as categoriaJpaEntities de forma paginada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Buscou as categoriaJpaEntities",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseSistema.class)
            )),
            @ApiResponse(responseCode = "400",description = "Erro ao buscar as categoriaJpaEntities",
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


        Page<Categoria> categorias = categoriaUseCase.findAllPaginated(pageable);
        Page<CategoriaResponse> categoriaResponses = categorias.map(categoriaMapper::toResponse);


        ApiResponseSistema<Page<CategoriaResponse>> response = new ApiResponseSistema<>(categoriaResponses, "Categorias obtidas com sucesso.", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Busca a categoriaJpaEntity por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Encontrou a categoriaJpaEntity ",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseSistema.class)
            )),
            @ApiResponse(responseCode = "400",description = "Erro ao buscar as categoriaJpaEntities",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseSistema.class)
                    )),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso não autorizado"
            ),
            @ApiResponse(responseCode = "404",description = "CategoriaJpaEntity com esse ID não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseSistema.class)
                    )),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseSistema<CategoriaResponse>> findById(@PathVariable String id) {
        CategoriaResponse categoriaResponse = categoriaMapper.toResponse(categoriaUseCase.findById(id));
        ApiResponseSistema<CategoriaResponse> response = new ApiResponseSistema<>(categoriaResponse, "CategoriaJpaEntity obtida com sucesso.", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/usuarios/{userId}/categorias")
    public ResponseEntity<ApiResponseSistema<List<CategoriaResponse>>> findByUsuarioId(@PathVariable Long userId) {
        List<CategoriaResponse> categoriaResponse = categoriaMapper.toResponseList(categoriaUseCase.findCategoriasByUser(userId));
        ApiResponseSistema<List<CategoriaResponse>> response = new ApiResponseSistema<>(categoriaResponse, "Categorias obtidas com sucesso.", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }


}
