package com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriaPost {
    // TODO: Adicione os atributos necessários para criação

    @NotNull(message = "Id não deve ser enviado na criação ")
    private Long id;


}
