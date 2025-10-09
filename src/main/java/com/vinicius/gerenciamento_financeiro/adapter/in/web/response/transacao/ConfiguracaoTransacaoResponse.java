package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ConfiguracaoTransacaoResponse(
        Boolean recorrente,
        Integer periodicidade,
        Boolean parcelado,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dataVencimento,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime dataPagamento
) {
}