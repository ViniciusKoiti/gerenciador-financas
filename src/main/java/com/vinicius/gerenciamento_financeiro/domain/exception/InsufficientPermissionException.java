package com.vinicius.gerenciamento_financeiro.domain.exception;

import java.util.Map;

public class InsufficientPermissionException extends DomainException {
    public InsufficientPermissionException(String resource, String action) {
        super("INSUFFICIENT_PERMISSION",
                String.format("Permissão insuficiente para %s em %s", action, resource),
                Map.of("resource", resource, "action", action));
    }
}