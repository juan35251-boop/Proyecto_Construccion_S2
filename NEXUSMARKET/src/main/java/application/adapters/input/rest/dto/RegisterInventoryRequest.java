package application.adapters.input.rest.dto;

import application.domain.valueobjects.InventoryCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Datos recibidos por la API para registrar
 * el inventario inicial de un producto físico.
 *
 * @param productId identificador del producto
 * @param warehouseId identificador de la bodega
 * @param initialQuantity cantidad inicial disponible
 * @param condition condición inicial del inventario
 */
public record RegisterInventoryRequest(

        @NotBlank(
                message = "Product identifier must not be empty."
        )
        String productId,

        @NotBlank(
                message = "Warehouse identifier must not be empty."
        )
        String warehouseId,

        @PositiveOrZero(
                message = "Initial quantity must not be negative."
        )
        int initialQuantity,

        @NotNull(
                message = "Inventory condition must not be null."
        )
        InventoryCondition condition

) {
}