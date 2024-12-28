package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao;

public record AuthenticationResponse(
        String token,
        UsuarioResponse usuario
) {}