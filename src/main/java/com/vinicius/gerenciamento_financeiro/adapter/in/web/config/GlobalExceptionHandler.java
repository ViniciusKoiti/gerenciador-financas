package com.vinicius.gerenciamento_financeiro.adapter.in.web.config;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.ApiResponseSistema;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("RESOURCE_NOT_FOUND")
                .message("Recurso solicitado não foi encontrado")
                .details(Map.of("originalMessage", ex.getMessage()))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Argumento inválido: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("INVALID_ARGUMENT")
                .message("Dados fornecidos são inválidos")
                .details(Map.of(
                        "reason", ex.getMessage(),
                        "suggestion", "Verifique os dados e tente novamente"
                ))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(error);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.error("Erro de integridade de dados: {}", ex.getMessage());

        String userFriendlyMessage = determineIntegrityErrorMessage(ex);

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("DATA_INTEGRITY_VIOLATION")
                .message(userFriendlyMessage)
                .details(Map.of(
                        "type", "database_constraint",
                        "suggestion", "Verifique se os dados não conflitam com registros existentes"
                ))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponseSistema<Void>> handleBadRequest(BadRequestException ex) {
        ApiResponseSistema<Void> response = new ApiResponseSistema<>(null, "Requisição inválida: " + ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Tentativa de login com credenciais inválidas");

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("AUTHENTICATION_FAILED")
                .message("Email ou senha incorretos")
                .details(Map.of(
                        "suggestion", "Verifique suas credenciais e tente novamente",
                        "helpLink", "/api/auth/reset-password"
                ))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Acesso negado para recurso: {}", request.getRequestURI());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("ACCESS_DENIED")
                .message("Você não tem permissão para acessar este recurso")
                .details(Map.of(
                        "requiredPermission", "Permissão de proprietário do recurso",
                        "suggestion", "Entre em contato com o administrador se necessário"
                ))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.error("Erro interno do servidor [{}]: {}", errorId, ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .message("Ocorreu um erro interno. Nossa equipe foi notificada.")
                .details(Map.of(
                        "errorId", errorId,
                        "suggestion", "Tente novamente em alguns instantes. Se o problema persistir, entre em contato conosco."
                ))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String determineIntegrityErrorMessage(DataIntegrityViolationException ex) {
        String message = ex.getMessage().toLowerCase();

        if (message.contains("unique") || message.contains("duplicate")) {
            if (message.contains("email")) {
                return "Este email já está em uso por outro usuário";
            }
            return "Este dado já existe no sistema";
        }

        if (message.contains("foreign key") || message.contains("constraint")) {
            return "Não é possível realizar esta operação devido a dependências existentes";
        }

        return "Erro de integridade dos dados";
    }
}
