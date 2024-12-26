package com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransacaoPut {

    @NotNull(message = "Para atualizar precisamos do ID")
    private final Long id;


    @NotBlank(message = "A descrição não pode ser vazia.")
    private final String descricao;

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser positivo.")
    private final BigDecimal valor;

    @NotBlank(message = "O tipo é obrigatório.")
    private final Transacao.Tipo tipo;

    @NotNull(message = "A data é obrigatória.")
    @PastOrPresent(message = "A data não pode ser no futuro.")
    private final LocalDateTime data;

    public TransacaoPut(Long id, String descricao, BigDecimal valor, Transacao.Tipo tipo, LocalDateTime data) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
        this.data = data;
    }
}
