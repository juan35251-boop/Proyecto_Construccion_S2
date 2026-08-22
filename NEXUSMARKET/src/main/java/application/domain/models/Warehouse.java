package application.domain.models;

import application.domain.valueobjects.WarehouseOwnerType;

public class Warehouse {

    private final WarehouseOwnerType ownerType;

    public Warehouse(WarehouseOwnerType ownerType) {
        validateOwnerType(ownerType);
        this.ownerType = ownerType;
    }

    public WarehouseOwnerType getOwnerType() {
        return ownerType;
    }

    public boolean isMarketplaceWarehouse() {
        return ownerType == WarehouseOwnerType.MARKETPLACE;
    }

    public boolean isSellerWarehouse() {
        return ownerType == WarehouseOwnerType.SELLER;
    }

    private void validateOwnerType(WarehouseOwnerType ownerType) {
        if (ownerType == null) {
            throw new IllegalArgumentException(
                    "Warehouse owner type must not be null."
            );
        }
    }
}
