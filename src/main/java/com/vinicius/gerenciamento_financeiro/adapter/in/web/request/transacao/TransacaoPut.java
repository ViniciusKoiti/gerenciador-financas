package com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoMovimentacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoPut(
        @NotNull(message = "Para atualizar precisamos do ID")
        Long id,

        @NotBlank(message = "A descrição não pode ser vazia.")
        String descricao,

        @NotNull(message = "O valor é obrigatório.")
        @DecimalMin(value = "0.01", message = "O valor deve ser positivo.")
        BigDecimal valor,

        @NotBlank(message = "O tipo é obrigatório.")
        TipoMovimentacao tipo,

        @NotNull(message = "A data é obrigatória.")
        @PastOrPresent(message = "A data não pode ser no futuro.")
        LocalDateTime data
) {}