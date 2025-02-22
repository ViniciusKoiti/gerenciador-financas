package com.vinicius.gerenciamento_financeiro.controllers;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.ApiResponseSistema;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.controller.categoria.CategoriaController;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.port.in.CategoriaUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    @Mock
    private CategoriaUseCase categoriaUseCase;

    @InjectMocks
    private CategoriaController categoriaController;

    @Test
    void testFindByUsuarioId() {

        Long userId = 1L;
        List<CategoriaResponse> mockCategorias = List.of(
                new CategoriaResponse(1L, "Categoria 1", "Descrição 1", true, "icone1", null),
                new CategoriaResponse(2L, "Categoria 2", "Descrição 2", true, "icone2", null)
        );

        when(categoriaUseCase.findCategoriasByUser(userId)).thenReturn(mockCategorias);
        ResponseEntity<ApiResponseSistema<List<CategoriaResponse>>> response = categoriaController.findByUsuarioId(userId);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Categorias obtidas com sucesso.", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().size());
        assertEquals("Categoria 1", response.getBody().getData().get(0).name());

        verify(categoriaUseCase, times(1)).findCategoriasByUser(userId);
    }
}
