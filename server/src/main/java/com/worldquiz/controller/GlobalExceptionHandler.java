/* (C)2026 */
package com.worldquiz.controller;

import com.worldquiz.exceptions.*;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({UserNotFoundException.class, ValidationException.class})
    public ResponseEntity<?> handleAuthFailures(RuntimeException ex) {
        // Do not reveal whether the user exists to prevent user enumeration attacks
        return build(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidToken(InvalidTokenException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Invalid token");
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<?> handleExpired(TokenExpiredException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Token expired");
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserExists(UserAlreadyExistsException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(TooManyMailsSent.class)
    public ResponseEntity<?> handleTooManyMailsSent(TooManyMailsSent ex) {
        return build(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
    }

    private ResponseEntity<?> build(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(
                        Map.of(
                                "timestamp", Instant.now().toString(),
                                "status", status.value(),
                                "error", status.getReasonPhrase(),
                                "message", message));
    }
}
