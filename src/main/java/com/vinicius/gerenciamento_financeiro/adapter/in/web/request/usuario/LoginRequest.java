package com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder
public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String senha
) {}