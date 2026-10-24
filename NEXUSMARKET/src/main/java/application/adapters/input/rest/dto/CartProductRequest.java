package application.adapters.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Datos recibidos para agregar un producto
 * al carrito de compras.
 *
 * @param productId identificador del producto
 * @param quantity cantidad agregada
 */
public record CartProductRequest(

        @NotBlank(
                message = "Product identifier must not be empty."
        )
        String productId,

        @Positive(
                message = "Product quantity must be greater than zero."
        )
        int quantity

) {
}