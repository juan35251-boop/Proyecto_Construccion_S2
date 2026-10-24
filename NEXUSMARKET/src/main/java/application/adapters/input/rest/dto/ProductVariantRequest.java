package application.adapters.input.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Datos recibidos para agregar o eliminar
 * una variante de producto.
 *
 * @param description descripción de la variante
 */
public record ProductVariantRequest(

        @NotBlank(
                message =
                        "Product variant description must not be empty."
        )
        String description
) {
}