package application.adapters.input.rest.dto;

import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import jakarta.validation.constraints.NotNull;

/**
 * Datos recibidos para registrar un producto.
 *
 * @param productType tipo físico o digital
 * @param status estado inicial
 */
public record RegisterProductRequest(

        @NotNull(
                message =
                        "Product type must not be null."
        )
        ProductType productType,

        @NotNull(
                message =
                        "Product status must not be null."
        )
        ProductStatus status
) {
}