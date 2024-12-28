package com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoMovimentacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public record CategoriaPut(
        @NotNull(message = "ID é obrigatório para atualização")
        Long id,

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 50, message = "Nome deve ter entre 3 e 50 caracteres")
        String nome,

        @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
        String descricao,

        @NotNull(message = "Tipo de movimentação é obrigatório")
        TipoMovimentacao tipo,

        String icone,

        Long categoriaPaiId,

        boolean ativa
) {}