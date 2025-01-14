package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import java.util.List;
public record CategoriaResponse(
        Long id,
        String name,
        String description,
        boolean isActive,
        String icon,

        CategoriaResponse categoriaResponseFather,
        List<TransacaoResponse> transactions
) {
    public static CategoriaResponse fromEntity(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.isAtiva(),
                categoria.getIcone(),
                categoria.getCategoriaPai() != null ? CategoriaResponse.fromEntity(categoria.getCategoriaPai()) : null,
                categoria.getTransacoes().stream().map(TransacaoResponse::fromEntity).toList()
        );
    }
}