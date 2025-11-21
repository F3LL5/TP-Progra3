package com.owo.TP_prg3.Excepciones;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* Maneja TODAS las excepciones personalizadas del sistema */
    @ExceptionHandler(SistemaException.class)
    public ResponseEntity<ErrorResponse> handleSistemaException(SistemaException ex) {
        ErrorResponse error = new ErrorResponse(
            ex.getHttpStatus().value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, ex.getHttpStatus());
    }

    /* Maneja errores de validación de Bean Validation (@Valid, @NotBlank, etc.) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errores = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining("; "));
            
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Errores de validación: " + errores,
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /* Maneja cualquier otra excepción no contemplada */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Error interno del servidor: " + ex.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static class ErrorResponse {
        private int status;
        private String mensaje;
        private LocalDateTime timestamp;

        public ErrorResponse(int status, String mensaje, LocalDateTime timestamp) {
            this.status = status;
            this.mensaje = mensaje;
            this.timestamp = timestamp;
        }

        // Getters
        public int getStatus() { return status; }
        public String getMensaje() { return mensaje; }
        public LocalDateTime getTimestamp() { return timestamp; }

        // Setters
        public void setStatus(int status) { this.status = status; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}
