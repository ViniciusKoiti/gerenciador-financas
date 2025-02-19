package com.vinicius.gerenciamento_financeiro.adapter.in.web.config;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.ApiResponseSistema;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponseSistema<Void>> handleEntityNotFound(EntityNotFoundException ex) {
        ApiResponseSistema<Void> response = new ApiResponseSistema<>(null, "Registro não encontrado: " + ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseSistema<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponseSistema<Void> response = new ApiResponseSistema<>(null, "Erro de validação: " + ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseSistema<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ApiResponseSistema<Void> response = new ApiResponseSistema<>(null, "Erro de integridade: " + ex.getMessage(), HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponseSistema<Void>> handleBadRequest(BadRequestException ex) {
        ApiResponseSistema<Void> response = new ApiResponseSistema<>(null, "Requisição inválida: " + ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseSistema<Void>> handleBadCredentials(BadCredentialsException ex) {
        ApiResponseSistema<Void> response = new ApiResponseSistema<>(null, "Usuário ou senha inválidos", HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseSistema<Void>> handleAccessDenied(AccessDeniedException ex) {
        ApiResponseSistema<Void> response = new ApiResponseSistema<>(null, "Acesso negado", HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseSistema<Void>> handleAllExceptions(Exception ex) {
        ApiResponseSistema<Void> response = new ApiResponseSistema<>(null, "Erro interno no servidor: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
