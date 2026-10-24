package application.adapters.input.rest.dto;

import jakarta.validation.constraints.PositiveOrZero;

/**
 * Datos recibidos para ajustar la cantidad
 * disponible de un inventario.
 *
 * La nueva cantidad puede ser cero, pero nunca negativa.
 *
 * @param newQuantity nueva cantidad disponible
 */
public record InventoryAdjustmentRequest(

        @PositiveOrZero(
                message = "New inventory quantity must not be negative."
        )
        int newQuantity

) {
}