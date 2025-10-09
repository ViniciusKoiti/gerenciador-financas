package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransacaoResponse(
        Long id,
        String description,
        BigDecimal amount,
        String type,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime date,
        Boolean paid,
        ConfiguracaoTransacaoResponse config,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdDate
) {
}