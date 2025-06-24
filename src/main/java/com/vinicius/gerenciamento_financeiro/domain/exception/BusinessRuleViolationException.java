package com.vinicius.gerenciamento_financeiro.domain.exception;

import java.util.Map;

public class BusinessRuleViolationException extends DomainException {
    public BusinessRuleViolationException(String rule, String message) {
        super("BUSINESS_RULE_VIOLATION", message, Map.of("rule", rule));
    }
}