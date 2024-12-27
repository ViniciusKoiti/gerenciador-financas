package com.vinicius.gerenciamento_financeiro.adapter.in.web;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
@Getter
@Builder
public class ApiResponseSistema<T> {
    private T data;
    private String message;
    private LocalDateTime  timestamp;
    private int statusCode;

    public ApiResponseSistema(T data, String message, LocalDateTime timestamp, int statusCode) {
        this.data = data;
        this.message = message;
        this.timestamp = timestamp;
        this.statusCode = statusCode;
    }

    public ApiResponseSistema(T data, String message, int statusCode) {
        this.data = data;
        this.message = message;
        this.timestamp = getTimestamp();
        this.statusCode = statusCode;
    }

    public static <T> ApiResponseSistema<T> success(T data) {
        return ApiResponseSistema.<T>builder()
                .data(data)
                .message("Operação realizada com sucesso")
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    public static <T> ApiResponseSistema<T> success(T data, String message) {
        return ApiResponseSistema.<T>builder()
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    public static <T> ApiResponseSistema<T> error(String message, HttpStatus status) {
        return ApiResponseSistema.<T>builder()
                .data(null)
                .message(message)
                .timestamp(LocalDateTime.now())
                .statusCode(status.value())
                .build();
    }
}
