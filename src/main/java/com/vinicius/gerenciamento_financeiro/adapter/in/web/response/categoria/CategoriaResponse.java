package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria;

import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import lombok.Getter;

public record CategoriaResponse(
        Long id,
        String nome,
        String descricao,
        boolean ativa,
        String icone,
        Long categoriaPaiId
) {
    public static CategoriaResponse fromEntity(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.isAtiva(),
                categoria.getIcone(),
                categoria.getCategoriaPai() != null ? categoria.getCategoriaPai().getId() : null
        );
    }
}