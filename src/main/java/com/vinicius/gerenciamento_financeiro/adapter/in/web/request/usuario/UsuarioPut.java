package com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
public record UsuarioPut(

    @NotNull(message = "O id é necessario para atualizar ")
    Long id,

    String nome,

    String email


){

}
