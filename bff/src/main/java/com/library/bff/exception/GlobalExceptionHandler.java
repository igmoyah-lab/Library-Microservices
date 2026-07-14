package com.library.bff.exception;

import com.library.bff.dto.ApiResponse;
import io.sentry.Sentry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

/**
 * Maneja de forma centralizada las excepciones producidas en el BFF.
 *
 * Los errores esperados mantienen su código HTTP correspondiente.
 * Los errores inesperados son registrados en consola y enviados
 * a GlitchTip para facilitar su seguimiento.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja errores producidos al comunicarse con otro microservicio.
     *
     * @param ex excepción producida por un servicio externo
     * @return respuesta con el código HTTP original
     */
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleExternalServiceException(
            ExternalServiceException ex) {

        log.warn(
                "Error controlado al comunicarse con un microservicio. Status: {}, mensaje: {}",
                ex.getStatusCode(),
                ex.getMessage()
        );

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(new ApiResponse<>(false, null, ex.getMessage()));
    }

    /**
     * Propaga respuestas HTTP de error entregadas por los microservicios.
     *
     * @param ex excepción generada por RestClient
     * @return cuerpo y código HTTP entregado por el microservicio
     */
    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<String> handleRestClientResponseException(
            RestClientResponseException ex) {

        if (ex.getStatusCode().is5xxServerError()) {
            log.error(
                    "Un microservicio respondió con un error interno. Status: {}",
                    ex.getStatusCode(),
                    ex
            );

            Sentry.captureException(ex);
        } else {
            log.warn(
                    "Un microservicio respondió con un error controlado. Status: {}, respuesta: {}",
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString()
            );
        }

        return ResponseEntity
                .status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.getResponseBodyAsString());
    }

    /**
     * Maneja errores inesperados no capturados por otros handlers.
     *
     * El error se muestra en consola y también se envía a GlitchTip.
     *
     * @param ex error inesperado
     * @return respuesta HTTP 500 sin exponer información interna
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(
            Exception ex) {

        log.error("Error interno no controlado en el BFF", ex);

        Sentry.captureException(ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(
                        false,
                        null,
                        "Ocurrió un error interno en el servidor"
                ));
    }
}