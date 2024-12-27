package com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransacaoPost(@NotBlank(message = "A descrição não pode ser vazia.") String descricao,
                            @NotNull(message = "O valor é obrigatório.") @DecimalMin(value = "0.01", message = "O valor deve ser positivo.") BigDecimal valor,
                            @NotBlank(message = "O tipo é obrigatório.") Transacao.Tipo tipo,
                            @NotNull(message = "A data é obrigatória.") @PastOrPresent(message = "A data não pode ser no futuro.") LocalDateTime data) {


    public TransacaoPost(String descricao, BigDecimal valor, Transacao.Tipo tipo, LocalDateTime data) {
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
        this.data = data;
    }

}
