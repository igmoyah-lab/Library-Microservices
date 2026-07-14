package com.library.auth.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.library.auth.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores producidos por credenciales incorrectas.
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>>
            handleInvalidCredentials(
                    InvalidCredentialsException exception
            ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ApiResponse<>(
                                false,
                                exception.getMessage(),
                                null
                        )
                );
    }

    /**
     * Maneja intentos de registrar un correo duplicado.
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Void>>
            handleDuplicateEmail(
                    DuplicateEmailException exception
            ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        new ApiResponse<>(
                                false,
                                exception.getMessage(),
                                null
                        )
                );
    }

    /**
     * Maneja errores de validación en los DTO recibidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<
            ApiResponse<Map<String, String>>
    > handleValidation(
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

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ApiResponse<>(
                                false,
                                "Existen datos inválidos en la solicitud",
                                errors
                        )
                );
    }

    /**
     * Maneja cualquier error inesperado del servicio.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>>
            handleUnexpectedError(
                    Exception exception
            ) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ApiResponse<>(
                                false,
                                "Ocurrió un error interno inesperado",
                                null
                        )
                );
    }
}
