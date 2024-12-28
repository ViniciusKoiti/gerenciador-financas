package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String senha
) {}
