package application.adapters.input.rest.dto;

import application.domain.valueobjects.InventoryCondition;
import jakarta.validation.constraints.NotNull;

/**
 * Datos recibidos para modificar la condición
 * operativa de un inventario.
 *
 * @param condition nueva condición del inventario
 */
public record InventoryConditionRequest(

        @NotNull(
                message = "Inventory condition must not be null."
        )
        InventoryCondition condition

) {
}