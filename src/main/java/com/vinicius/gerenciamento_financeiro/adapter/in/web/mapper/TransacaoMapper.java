package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPut;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransacaoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipo", expression = "java(Transacao.Tipo)")
    Transacao toEntity(TransacaoPost dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipo", expression = "java(Transacao.Tipo)")
    Transacao toEntity(TransacaoPut dto);

    TransacaoResponse toResponse(Transacao transacao);
}
