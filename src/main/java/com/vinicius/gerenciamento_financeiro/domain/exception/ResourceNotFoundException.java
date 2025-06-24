package com.vinicius.gerenciamento_financeiro.domain.exception;

import java.util.Map;

public class ResourceNotFoundException extends DomainException {
    public ResourceNotFoundException(String resourceType, Object id) {
        super("RESOURCE_NOT_FOUND",
                String.format("%s com ID %s não encontrado", resourceType, id),
                Map.of("resourceType", resourceType, "id", id));
    }
}