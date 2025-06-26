package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPut;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.adapter.out.categoria.entity.CategoriaJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = TransacaoMapper.class)
public interface CategoriaMapper {

    @Autowired
    TransacaoMapper transacaoMapper = new TransacaoMapperImpl();

    default CategoriaJpaEntity toEntity(CategoriaPost dto) {
        return CategoriaJpaEntity.builder()
                .nome(dto.name())
                .descricao(dto.description())
                .icone(dto.icon())
                .auditoria(new Auditoria())
                .build();
    }

    default CategoriaJpaEntity toEntity(CategoriaPut dto) {
        return CategoriaJpaEntity.builder()
                .id(dto.id())
                .nome(dto.nome())
                .descricao(dto.descricao())
                .icone(dto.icone())
                .ativa(dto.ativa())
                .build();
    }

    default CategoriaResponse toResponse(CategoriaJpaEntity categoriaJpaEntity) {
        if (categoriaJpaEntity == null) {
            return null;
        }

        return new CategoriaResponse(
                categoriaJpaEntity.getId(),
                categoriaJpaEntity.getNome(),
                categoriaJpaEntity.getDescricao(),
                categoriaJpaEntity.getAtiva(),
                categoriaJpaEntity.getIcone(),
                categoriaJpaEntity.getCategoriaJpaEntityPai() != null ? toResponse(categoriaJpaEntity.getCategoriaJpaEntityPai()) : null
        );
    }
}