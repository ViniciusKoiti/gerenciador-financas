package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPut;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    default Categoria toEntity(CategoriaPost dto) {
        return Categoria.builder()
                .nome(dto.nome())
                .descricao(dto.descricao())
                .icone(dto.icone())
                .build();
    }

    default Categoria toEntity(CategoriaPut dto) {
        return Categoria.builder()
                .id(dto.id())
                .nome(dto.nome())
                .descricao(dto.descricao())
                .icone(dto.icone())
                .ativa(dto.ativa())
                .build();
    }

    CategoriaResponse toResponse(Categoria categoria);
}