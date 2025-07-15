package com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransacaoPost(
        @JsonProperty("description")
        @NotBlank(message = "A descrição não pode ser vazia.")
        String descricao,

        @JsonProperty("amount")
        @NotNull(message = "O valor é obrigatório.")
        @DecimalMin(value = "0.01", message = "O valor deve ser positivo.")
        BigDecimal valor,

        @JsonProperty("type")
        @NotNull(message = "O tipo é obrigatório.")
        TipoMovimentacao tipoMovimentacao,

        @JsonProperty("date")
        @NotNull(message = "A data é obrigatória.")
        LocalDateTime data,

        @JsonProperty("categoryId")
        @NotNull(message = "O ID da categoriaJpaEntity é obrigatório.")
        Long categoriaId,
        @JsonProperty("observations")
        String observacoes,

        @JsonProperty("configuration")
        @Valid
        ConfiguracaoTransacaoPost configuracao
) {


    public TransacaoPost(String descricao, BigDecimal valor, TipoMovimentacao tipoMovimentacao,
                         LocalDateTime data, Long categoriaId) {
        this(descricao, valor, tipoMovimentacao, data, categoriaId, null, ConfiguracaoTransacaoPost.padrao());
    }

    public ConfiguracaoTransacaoPost configuracao() {
        return configuracao != null ? configuracao : ConfiguracaoTransacaoPost.padrao();
    }

}
