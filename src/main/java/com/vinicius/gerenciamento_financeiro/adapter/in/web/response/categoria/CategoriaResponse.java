package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria;


import com.vinicius.gerenciamento_financeiro.adapter.out.categoria.entity.Categoria;

public record CategoriaResponse(
        Long id,
        String name,
        String description,
        boolean isActive,
        String icon,

        CategoriaResponse categoriaResponseFather
) {
    public static CategoriaResponse fromEntity(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.isAtiva(),
                categoria.getIcone(),
                categoria.getCategoriaPai() != null ? CategoriaResponse.fromEntity(categoria.getCategoriaPai()) : null
        );
    }
}