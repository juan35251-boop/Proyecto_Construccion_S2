package application.adapters.input.rest.dto;

import application.domain.valueobjects.WarehouseOwnerType;
import jakarta.validation.constraints.NotNull;

/**
 * Datos recibidos por la API para registrar una bodega.
 *
 * @param ownerType tipo de propietario de la bodega
 */
public record RegisterWarehouseRequest(

        @NotNull(
                message =
                        "Warehouse owner type must not be null."
        )
        WarehouseOwnerType ownerType
) {
}