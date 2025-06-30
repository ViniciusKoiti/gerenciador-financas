package com.vinicius.gerenciamento_financeiro.domain.model.usuario;

import java.time.LocalDateTime;

public record ContextoUsuario(
        UsuarioId usuarioId,
        String email,
        LocalDateTime ultimoAcesso,
        String ipOrigem
) {

    public static ContextoUsuario criar(UsuarioId usuarioId, String email, String ipOrigem) {
        return new ContextoUsuario(usuarioId, email, LocalDateTime.now(), ipOrigem);
    }

    public boolean isUsuarioValido() {
        return usuarioId != null && email != null && !email.isBlank();
    }
}
