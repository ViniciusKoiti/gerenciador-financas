package com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoRecorrencia;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

public record ConfiguracaoTransacaoPost(
        @JsonProperty("paid")
        Boolean pago,

        @JsonProperty("recurring")
        Boolean recorrente,

        @JsonProperty("recurrenceType")
        TipoRecorrencia tipoRecorrencia,

        @JsonProperty("periodicity")
        @Min(value = 1, message = "Periodicidade deve ser maior que zero")
        Integer periodicidade,

        @JsonProperty("dueDate")
        LocalDate dataVencimento,

        @JsonProperty("installment")
        Boolean parcelado
) {

    public static ConfiguracaoTransacaoPost padrao() {
        return new ConfiguracaoTransacaoPost(false, false, null, null, null, false);
    }
}