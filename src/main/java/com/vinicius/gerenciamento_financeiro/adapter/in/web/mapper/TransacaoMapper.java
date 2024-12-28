package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPut;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransacaoMapper {



    default Transacao toEntity(TransacaoPost dto) {
        return Transacao.builder()
                .descricao(dto.descricao())
                .valor(dto.valor())
                .tipo(dto.tipoMovimentacao())
                .data(dto.data())
                .build();
    }

    default Transacao toEntity(TransacaoPut dto) {
        return Transacao.builder()
                .id(dto.id())
                .descricao(dto.descricao())
                .valor(dto.valor())
                .tipo(dto.tipo())
                .data(dto.data())
                .build();
    }
    TransacaoResponse toResponse(Transacao transacao);
}
