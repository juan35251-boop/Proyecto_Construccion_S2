package application.adapters.input.rest.dto;

import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseOwnerType;

/**
 * Respuesta utilizada por la API para representar
 * una bodega.
 *
 * @param warehouseId identificador permanente
 * @param ownerType tipo de propietario
 */
public record WarehouseResponse(
        String warehouseId,
        WarehouseOwnerType ownerType
) {

    /**
     * Convierte una bodega del dominio
     * en una respuesta REST.
     *
     * @param warehouse bodega del dominio
     * @return respuesta de la API
     */
    public static WarehouseResponse from(
            Warehouse warehouse
    ) {
        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "Warehouse must not be null."
            );
        }

        return new WarehouseResponse(
                warehouse.getWarehouseId(),
                warehouse.getOwnerType()
        );
    }
}