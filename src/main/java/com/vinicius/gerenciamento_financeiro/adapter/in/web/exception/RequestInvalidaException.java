package com.vinicius.gerenciamento_financeiro.adapter.in.web.exception;

public class RequestInvalidaException extends RuntimeException {
    public RequestInvalidaException(String mensagem) {
        super(mensagem);
    }
}