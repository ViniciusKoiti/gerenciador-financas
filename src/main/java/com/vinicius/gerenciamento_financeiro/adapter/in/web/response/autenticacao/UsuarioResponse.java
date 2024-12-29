package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao;

import lombok.Builder;

@Builder
public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String senha
) {}
