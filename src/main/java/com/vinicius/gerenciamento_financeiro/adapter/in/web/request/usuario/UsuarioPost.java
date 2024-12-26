package com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioPost {
    // TODO: Adicione os atributos necessários para criação

    @NotNull(message = "Id não deve ser enviado na criação ")
    private Long id;


}
