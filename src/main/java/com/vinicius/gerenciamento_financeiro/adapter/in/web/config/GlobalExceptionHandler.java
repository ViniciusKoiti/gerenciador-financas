package com.vinicius.gerenciamento_financeiro.adapter.in.web.config;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.ApiResponseSistema;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseSistema<Void>> handleAllExceptions(Exception ex) {
        ApiResponseSistema<Void> response = new ApiResponseSistema<>(null, "Ocorreu um erro no servidor: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseSistema<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponseSistema<Void> response = new ApiResponseSistema<>(null, "Erro de validação: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.badRequest().body(response);
    }

}
