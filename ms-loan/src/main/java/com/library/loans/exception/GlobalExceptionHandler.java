package com.library.loans.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.library.loans.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
        ResourceNotFoundException exception
) {
        ApiResponse<Void> response = new ApiResponse<>(
                false,
                null,
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
}

@ExceptionHandler(BusinessRuleException.class)
public ResponseEntity<ApiResponse<Void>> handleBusinessRule(
        BusinessRuleException exception
) {
        ApiResponse<Void> response = new ApiResponse<>(
                false,
                null,
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
}

@ExceptionHandler(ExternalServiceException.class)
public ResponseEntity<ApiResponse<Void>> handleExternalService(
        ExternalServiceException exception
) {
        ApiResponse<Void> response = new ApiResponse<>(
                false,
                null,
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
}

@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
        MethodArgumentNotValidException exception
) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                false,
                errors,
                "Existen datos inválidos en la solicitud"
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
}

@ExceptionHandler(Exception.class)
public ResponseEntity<ApiResponse<Void>> handleUnexpectedError(
        Exception exception
) {
        ApiResponse<Void> response = new ApiResponse<>(
                false,
                null,
                "Ocurrió un error interno inesperado"
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
}
}