package application.adapters.input.rest.dto;

import application.domain.valueobjects.ProductStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Datos recibidos para cambiar el estado
 * de un producto.
 *
 * @param status nuevo estado
 */
public record ChangeProductStatusRequest(

        @NotNull(
                message =
                        "Product status must not be null."
        )
        ProductStatus status
) {
}