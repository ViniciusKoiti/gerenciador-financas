package com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

public record UsuarioPost(
        @NotBlank @NotEmpty String nome,
        @Email @NotBlank String email,
        @NotBlank String senha
) {}