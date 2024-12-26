package com.vinicius.gerenciamento_financeiro.adapter.in.web;

public class ApiResponseSistema<T> {
    private T data;
    private String message;
    private String timestamp;
    private int statusCode;
    public ApiResponseSistema(T data, String message, int statusCode) {
        this.data = data;
        this.message = message;
        this.timestamp = String.valueOf(System.currentTimeMillis());
        this.statusCode = statusCode;
    }
}
