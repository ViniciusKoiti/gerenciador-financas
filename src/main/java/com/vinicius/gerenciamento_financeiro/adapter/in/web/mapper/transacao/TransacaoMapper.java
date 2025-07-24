package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.ConfiguracaoTransacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import org.springframework.stereotype.Component;

@Component
public class TransacaoMapper {
    private final ConfiguracaoTransacaoMapper configuracaoMapper;

    public TransacaoMapper(ConfiguracaoTransacaoMapper configuracaoMapper) {
        this.configuracaoMapper = configuracaoMapper;
    }

    public Transacao toEntity(TransacaoPost transacaoPost, CategoriaId categoriaId, UsuarioId usuarioId) {
        if (transacaoPost == null) {
            throw new IllegalArgumentException("TransacaoPost não pode ser nulo");
        }

        ConfiguracaoTransacao configuracao = configuracaoMapper.toDomain(transacaoPost.configuracao());

        return Transacao.criarNova(
                transacaoPost.descricao(),
                transacaoPost.valor(),
                transacaoPost.tipoMovimentacao(),
                transacaoPost.data(),
                categoriaId,
                usuarioId,
                configuracao,
                transacaoPost.observacoes()
        );
    }



    public TransacaoResponse toResponse(Transacao transacao) {
        return TransacaoResponse.fromEntity(transacao);
    }
}