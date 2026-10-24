package application.adapters.input.rest.dto;

import jakarta.validation.constraints.Positive;

/**
 * Datos recibidos para cambiar la cantidad
 * de un producto existente en el carrito.
 *
 * @param newQuantity nueva cantidad solicitada
 */
public record CartQuantityRequest(

        @Positive(
                message = "New product quantity must be greater than zero."
        )
        int newQuantity

) {
}