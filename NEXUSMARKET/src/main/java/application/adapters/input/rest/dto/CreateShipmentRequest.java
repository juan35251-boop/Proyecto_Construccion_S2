package application.adapters.input.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Datos necesarios para crear un envío físico.
 *
 * @param orderId identificador del pedido pagado
 */
public record CreateShipmentRequest(

        @NotBlank(
                message = "Order identifier must not be empty."
        )
        String orderId

) {
}