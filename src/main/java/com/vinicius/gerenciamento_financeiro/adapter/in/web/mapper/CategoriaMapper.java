package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.TransacaoMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPut;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = TransacaoMapper.class)
public interface CategoriaMapper {

    @Autowired
    TransacaoMapper transacaoMapper = new TransacaoMapperImpl();

    default Categoria toEntity(CategoriaPost dto) {
        return Categoria.builder()
                .nome(dto.name())
                .descricao(dto.description())
                .icone(dto.icon())
                .auditoria(new Auditoria())
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

    default CategoriaResponse toResponse(Categoria categoria) {
        if (categoria == null) {
            return null;
        }

        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.isAtiva(),
                categoria.getIcone(),
                categoria.getCategoriaPai() != null ? toResponse(categoria.getCategoriaPai()) : null,
                categoria.getTransacoes() != null ?
                        categoria.getTransacoes().stream()
                                .map(transacaoMapper::toResponse)
                                .collect(Collectors.toList()) :
                        null
        );
    }
}