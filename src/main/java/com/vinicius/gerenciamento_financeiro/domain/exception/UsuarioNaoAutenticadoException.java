package com.vinicius.gerenciamento_financeiro.domain.exception;

import java.util.Map;

public class UsuarioNaoAutenticadoException extends DomainException {

    public UsuarioNaoAutenticadoException() {
        super("USUARIO_NAO_AUTENTICADO",
                "Nenhum usuário está autenticado no contexto atual",
                Map.of("timestamp", java.time.LocalDateTime.now()));
    }

    public UsuarioNaoAutenticadoException(String detalhe) {
        super("USUARIO_NAO_AUTENTICADO",
                "Falha na autenticação: " + detalhe,
                Map.of("detalhe", detalhe, "timestamp", java.time.LocalDateTime.now()));
    }
}