package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper para converter entidades de Domínio para DTOs de Response
 */
@Mapper(componentModel = "spring")
public interface CategoriaResponseMapper {
    
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "name", source = "nome")
    @Mapping(target = "description", source = "descricao")
    @Mapping(target = "isActive", source = "ativa")
    @Mapping(target = "icon", source = "icone")
    @Mapping(target = "categoriaResponseFather", ignore = true)
    CategoriaResponse toResponse(Categoria categoria);
    
    List<CategoriaResponse> toResponseList(List<Categoria> categorias);
}