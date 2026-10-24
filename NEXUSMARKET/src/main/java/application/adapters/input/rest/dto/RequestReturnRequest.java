package application.adapters.input.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Datos recibidos por la API para solicitar
 * la devolución de una orden.
 *
 * @param orderId identificador de la orden
 */
public record RequestReturnRequest(

        @NotBlank(
                message = "Order identifier must not be empty."
        )
        String orderId
) {
}