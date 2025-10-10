package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria;

public record CategoriaResponse(
        Long id,
        String name,
        String description,
        boolean isActive,
        String icon,

        CategoriaResponse categoriaResponseFather
) {
}