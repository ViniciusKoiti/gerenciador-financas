package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPut;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class CategoriaMapper {

    public Categoria toEntity(CategoriaPost request, UsuarioId usuarioId) {
        if (request == null) {
            return null;
        }

        return Categoria.criar(
                request.name(),
                request.description(),
                request.icon(),
                usuarioId
        );
    }

    public Categoria toEntity(CategoriaPut request, UsuarioId usuarioId) {
        if (request == null) {
            return null;
        }
        throw new UnsupportedOperationException(
                "Use o método de atualização específico no service"
        );
    }

    public CategoriaResponse toResponse(Categoria categoria) {
        if (categoria == null) {
            return null;
        }

        return new CategoriaResponse(
                categoria.getId() != null ? categoria.getId().getValue() : null,
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.isAtiva(),
                categoria.getIcone(),
                categoria.getCategoriaPaiId() != null ?
                        new CategoriaResponse(
                                categoria.getCategoriaPaiId().getValue(),
                                null, null, true, null, null
                        ) : null
        );
    }

    public List<CategoriaResponse> toResponseList(List<Categoria> categorias) {
        if (categorias == null) {
            return Collections.emptyList();
        }

        return categorias.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}