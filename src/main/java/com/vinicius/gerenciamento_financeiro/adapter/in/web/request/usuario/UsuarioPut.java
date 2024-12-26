package com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioPut {
    // TODO: Adicione os atributos necessários para criação

    @NotNull(message = "O id é necessario para atualizar ")
    private Long id;


}
