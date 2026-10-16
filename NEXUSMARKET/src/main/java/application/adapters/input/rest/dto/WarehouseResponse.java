package application.adapters.input.rest.dto;

import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseOwnerType;

public record WarehouseResponse(
        WarehouseOwnerType ownerType
) {

    public static WarehouseResponse from(
            Warehouse warehouse
    ) {
        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "Warehouse must not be null."
            );
        }

        return new WarehouseResponse(
                warehouse.getOwnerType()
        );
    }
}