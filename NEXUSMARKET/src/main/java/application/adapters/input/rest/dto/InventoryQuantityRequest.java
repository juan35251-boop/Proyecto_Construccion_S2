package application.adapters.input.rest.dto;

import jakarta.validation.constraints.Positive;

/**
 * Datos recibidos para realizar una operación
 * que requiere una cantidad positiva.
 *
 * Se utiliza para:
 *
 * - Ingresar unidades.
 * - Reservar unidades.
 * - Registrar devoluciones.
 *
 * @param quantity cantidad utilizada en la operación
 */
public record InventoryQuantityRequest(

        @Positive(
                message = "Quantity must be greater than zero."
        )
        int quantity

) {
}