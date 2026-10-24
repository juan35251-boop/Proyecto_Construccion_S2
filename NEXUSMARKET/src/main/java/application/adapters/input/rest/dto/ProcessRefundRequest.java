package application.adapters.input.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Datos recibidos por la API para procesar
 * el reembolso de una devolución.
 *
 * @param returnId identificador de la devolución
 */
public record ProcessRefundRequest(

        @NotBlank(
                message = "Return identifier must not be empty."
        )
        String returnId
) {
}