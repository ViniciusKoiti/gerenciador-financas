package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria;


import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria.entity.CategoriaJpaEntity;

public record CategoriaResponse(
        Long id,
        String name,
        String description,
        boolean isActive,
        String icon,

        CategoriaResponse categoriaResponseFather
) {
    public static CategoriaResponse fromEntity(CategoriaJpaEntity categoriaJpaEntity) {
        return new CategoriaResponse(
                categoriaJpaEntity.getId(),
                categoriaJpaEntity.getNome(),
                categoriaJpaEntity.getDescricao(),
                categoriaJpaEntity.getAtiva(),
                categoriaJpaEntity.getIcone(),
                categoriaJpaEntity.getCategoriaJpaEntityPai() != null ? CategoriaResponse.fromEntity(categoriaJpaEntity.getCategoriaJpaEntityPai()) : null
        );
    }
}