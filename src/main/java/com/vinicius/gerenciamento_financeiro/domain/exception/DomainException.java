package com.vinicius.gerenciamento_financeiro.domain.exception;

import java.util.Map;

public abstract class DomainException extends RuntimeException {
    private final String errorCode;
    private final Map<String, Object> details;

    protected DomainException(String errorCode, String message, Map<String, Object> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details != null ? details : Map.of();
    }

    public String getErrorCode() { return errorCode; }
    public Map<String, Object> getDetails() { return details; }
}