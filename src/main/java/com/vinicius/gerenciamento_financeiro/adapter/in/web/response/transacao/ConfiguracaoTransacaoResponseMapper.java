package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.ConfiguracaoTransacao;
import org.springframework.stereotype.Component;

@Component
public class ConfiguracaoTransacaoResponseMapper {

    public ConfiguracaoTransacaoResponse toResponse(ConfiguracaoTransacao configuracao) {
        if (configuracao == null) {
            return null;
        }

        return ConfiguracaoTransacaoResponse.builder()
                .recorrente(configuracao.getRecorrente())
                .periodicidade(configuracao.getPeriodicidade())
                .parcelado(configuracao.getParcelado())
                .dataVencimento(configuracao.getDataVencimento())
                .dataPagamento(configuracao.getDataPagamento())
                .build();
    }
}