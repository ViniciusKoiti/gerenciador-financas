package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.response;

public record AuthenticationResponse(
        String token,
        UsuarioResponse usuario
) {}