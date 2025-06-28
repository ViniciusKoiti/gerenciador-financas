package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransacaoMapper {

    default Transacao toEntity(TransacaoPost transacaoPost, CategoriaId categoriaId, UsuarioId usuarioId) {
        if (transacaoPost == null) {
            throw new IllegalArgumentException("TransacaoPost não pode ser nulo");
        }

        return Transacao.criarNova(
                transacaoPost.descricao(),
                transacaoPost.valor(),
                transacaoPost.tipoMovimentacao(),
                transacaoPost.data(),
                categoriaId,
                usuarioId
        );
    }

    default TransacaoResponse toResponse(Transacao transacao) {
        return TransacaoResponse.fromEntity(transacao);
    }
}