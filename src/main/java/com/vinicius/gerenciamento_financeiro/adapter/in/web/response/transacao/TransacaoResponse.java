package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Builder
public record TransacaoResponse(
        Long id,
        String descricao,
        BigDecimal valor,
        String tipo,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime data,
        Boolean pago,
        ConfiguracaoTransacaoResponse configuracao,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime dataCriacao
) {
    @Builder
    public record ConfiguracaoTransacaoResponse(
            Boolean recorrente,
            Integer periodicidade,
            Boolean parcelado,
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate dataVencimento,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime dataPagamento
    ) {}

    @Builder
    public static TransacaoResponse of(
            Long id,
            String descricao,
            BigDecimal valor,
            String tipo,
            LocalDateTime data,
            Boolean pago,
            ConfiguracaoTransacaoResponse configuracao,
            LocalDateTime dataCriacao
    ) {
        return new TransacaoResponse(
                id, descricao, valor, tipo, data,
                pago, configuracao, dataCriacao
        );
    }
}