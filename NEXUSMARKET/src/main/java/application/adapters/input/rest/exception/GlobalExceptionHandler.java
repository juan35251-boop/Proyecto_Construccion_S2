package application.adapters.input.rest.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador global de excepciones generadas
 * durante las solicitudes REST.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    GlobalExceptionHandler.class
            );

    /**
     * Procesa errores de validación de DTO.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse>
    handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> validationErrors =
                new LinkedHashMap<>();

        for (FieldError fieldError :
                exception.getBindingResult()
                        .getFieldErrors()) {

            validationErrors.putIfAbsent(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }

        ApiErrorResponse response = createResponse(
                HttpStatus.BAD_REQUEST,
                "Request validation failed.",
                request.getRequestURI(),
                validationErrors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Procesa argumentos inválidos recibidos
     * por la aplicación.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse>
    handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = createResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI(),
                Map.of()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Procesa conflictos con las reglas de negocio.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse>
    handleIllegalStateException(
            IllegalStateException exception,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = createResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequestURI(),
                Map.of()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    /**
     * Procesa errores técnicos inesperados.
     *
     * El cliente recibe un mensaje genérico para evitar
     * exponer detalles internos. La excepción completa
     * queda registrada en la consola del servidor.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse>
    handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        LOGGER.error(
                "Unexpected error while processing {} {}",
                request.getMethod(),
                request.getRequestURI(),
                exception
        );

        ApiErrorResponse response = createResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred.",
                request.getRequestURI(),
                Map.of()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    /**
     * Construye una respuesta uniforme de error.
     */
    private ApiErrorResponse createResponse(
            HttpStatus status,
            String message,
            String path,
            Map<String, String> validationErrors
    ) {
        return new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                validationErrors
        );
    }
}