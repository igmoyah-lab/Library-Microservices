package com.library.auth.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.library.auth.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(ResponseStatusException ex) {

        ApiResponse<Void> response = new ApiResponse<>(
                false,
                ex.getReason(),
                null
        );

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(response);
    }


}
