package com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoMovimentacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record CategoriaPost(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 50, message = "Nome deve ter entre 3 e 50 caracteres")
        String name,

        @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
        String description,

        String icon,

        Long categoriaPaiId
) {}