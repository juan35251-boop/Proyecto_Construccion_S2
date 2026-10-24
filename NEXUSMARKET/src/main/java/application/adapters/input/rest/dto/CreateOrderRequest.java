package application.adapters.input.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Datos requeridos para convertir un carrito
 * en un pedido.
 *
 * @param cartId identificador del carrito
 */
public record CreateOrderRequest(

        @NotBlank(
                message = "Cart identifier must not be empty."
        )
        String cartId

) {
}