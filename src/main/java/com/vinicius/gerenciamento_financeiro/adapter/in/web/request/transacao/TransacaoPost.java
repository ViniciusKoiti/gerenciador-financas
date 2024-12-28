package com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoMovimentacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransacaoPost(@NotBlank(message = "A descrição não pode ser vazia.") String descricao,
                            @NotNull(message = "O valor é obrigatório.") @DecimalMin(value = "0.01", message = "O valor deve ser positivo.") BigDecimal valor,
                            @NotNull(message = "O tipo é obrigatório.") TipoMovimentacao tipoMovimentacao,
                            @NotNull(message = "A data é obrigatória.") LocalDateTime data) {


    public TransacaoPost(String descricao, BigDecimal valor, TipoMovimentacao tipoMovimentacao, LocalDateTime data) {
        this.descricao = descricao;
        this.valor = valor;
        this.tipoMovimentacao = tipoMovimentacao;
        this.data = data;
    }

}
