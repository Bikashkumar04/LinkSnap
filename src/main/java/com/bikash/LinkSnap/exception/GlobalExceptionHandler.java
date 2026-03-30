package com.bikash.LinkSnap.exception;

import com.bikash.LinkSnap.dto.ApiErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> details = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI(), details);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), Collections.emptyList());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(), Collections.emptyList());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleResponseStatus(
            ResponseStatusException ex,
            HttpServletRequest request
    ) {
        String message = ex.getReason() == null ? "Request failed" : ex.getReason();
        return build(HttpStatus.valueOf(ex.getStatusCode().value()), message, request.getRequestURI(), Collections.emptyList());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDTO> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error",
                request.getRequestURI(),
                Collections.emptyList()
        );
    }

    private ResponseEntity<ApiErrorResponseDTO> build(
            HttpStatus status,
            String message,
            String path,
            List<String> details
    ) {
        ApiErrorResponseDTO response = new ApiErrorResponseDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                details
        );
        return ResponseEntity.status(status).body(response);
    }
}
