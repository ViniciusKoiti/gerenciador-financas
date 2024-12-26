package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPut;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import org.springframework.stereotype.Component;

@Component
public class TransacaoMapperImpl implements TransacaoMapper{


//    public TransacaoMapperImpl(JwtService jwtService) {
//        this.jwtService = jwtService;
//    }

    @Override
    public Transacao toEntity(TransacaoPost dto) {
        return
                new Transacao(
                        null,
                dto.getDescricao(),
                dto.getValor(),
                dto.getTipo(),
                dto.getData(),
                        null

        );
    }

    @Override
    public Transacao toEntity(TransacaoPut dto) {
        return null;
    }


    @Override
    public TransacaoResponse toResponse(Transacao transacao) {
        if (transacao == null) {
            return null;
        }

        return new TransacaoResponse(
                transacao.getId(),
                transacao.getDescricao(),
                transacao.getValor(),
                transacao.getTipo().name(),
                transacao.getData()
        );
    }
}
