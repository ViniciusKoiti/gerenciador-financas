package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.ConfiguracaoTransacaoPost;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.ConfiguracaoTransacao;
import org.springframework.stereotype.Component;

@Component
public class ConfiguracaoTransacaoMapper {

    public ConfiguracaoTransacao toDomain(ConfiguracaoTransacaoPost configuracaoPost) {
        if (configuracaoPost == null) {
            return ConfiguracaoTransacao.padrao();
        }

        ConfiguracaoTransacao.Builder builder = new ConfiguracaoTransacao.Builder()
                .pago(configuracaoPost.pago() != null ? configuracaoPost.pago() : false)
                .recorrente(configuracaoPost.recorrente() != null ? configuracaoPost.recorrente() : false)
                .parcelado(configuracaoPost.parcelado() != null ? configuracaoPost.parcelado() : false);

        if (Boolean.TRUE.equals(configuracaoPost.recorrente())) {
            builder.tipoRecorrencia(configuracaoPost.tipoRecorrencia())
                    .periodicidade(configuracaoPost.periodicidade());
        }

        if (configuracaoPost.dataVencimento() != null) {
            builder.dataVencimento(configuracaoPost.dataVencimento());
        }

        return builder.build();
    }
}