package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria;

import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoriaResponseMapper {

    /**
     * Converte modelo de DOMÍNIO para Response DTO
     *
     * @param categoria Entidade de domínio
     * @return DTO de resposta para a API
     */
    public CategoriaResponse fromDomain(Categoria categoria) {
        if (categoria == null) {
            return null;
        }

        return new CategoriaResponse(
                categoria.getId() != null ? categoria.getId().getValue() : null,
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.isAtiva(),
                categoria.getIcone(),
                null
        );
    }

    /**
     * Converte categoria com categoria pai aninhada
     * Usado quando você tem a categoria pai carregada
     *
     * @param categoria Categoria (pode ser subcategoria)
     * @param categoriaPai Categoria pai (se existir)
     */
    public CategoriaResponse fromDomainComPai(Categoria categoria, Categoria categoriaPai) {
        if (categoria == null) {
            return null;
        }

        CategoriaResponse categoriaPaiResponse = categoriaPai != null
                ? fromDomain(categoriaPai)  // ← Recursivamente converte o pai
                : null;

        return new CategoriaResponse(
                categoria.getId() != null ? categoria.getId().getValue() : null,
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.isAtiva(),
                categoria.getIcone(),
                categoriaPaiResponse  // ← Categoria pai aninhada
        );
    }

    /**
     */
    public List<CategoriaResponse> fromDomainList(List<Categoria> categorias) {
        if (categorias == null) {
            return List.of();
        }

        return categorias.stream()
                .map(this::fromDomain)
                .toList();
    }
}