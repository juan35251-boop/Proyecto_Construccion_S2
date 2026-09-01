package application.domain.models;

import application.domain.valueobjects.WarehouseOwnerType;

/**
 * Representa una bodega utilizada para almacenar productos en NexusMarket.
 *
 * Cada bodega se clasifica según su tipo de propietario: puede pertenecer
 * directamente al marketplace o puede ser administrada por un vendedor.
 *
 * El tipo de propietario no puede cambiar después de crear la bodega.
 */
public class Warehouse {

    /**
     * Tipo de propietario de la bodega.
     *
     * Indica si la bodega pertenece al marketplace o a un vendedor.
     */
    private final WarehouseOwnerType ownerType;

    /**
     * Crea una bodega con un tipo de propietario.
     *
     * @param ownerType tipo de propietario de la bodega
     *
     * @throws IllegalArgumentException si el tipo de propietario es nulo
     */
    public Warehouse(WarehouseOwnerType ownerType) {
        validateOwnerType(ownerType);
        this.ownerType = ownerType;
    }

    /**
     * Obtiene el tipo de propietario de la bodega.
     *
     * @return el tipo de propietario
     */
    public WarehouseOwnerType getOwnerType() {
        return ownerType;
    }

    /**
     * Indica si la bodega pertenece al marketplace.
     *
     * @return {@code true} si el tipo de propietario es
     *         {@link WarehouseOwnerType#MARKETPLACE}; de lo contrario,
     *         {@code false}
     */
    public boolean isMarketplaceWarehouse() {
        return ownerType == WarehouseOwnerType.MARKETPLACE;
    }

    /**
     * Indica si la bodega pertenece a un vendedor.
     *
     * @return {@code true} si el tipo de propietario es
     *         {@link WarehouseOwnerType#SELLER}; de lo contrario,
     *         {@code false}
     */
    public boolean isSellerWarehouse() {
        return ownerType == WarehouseOwnerType.SELLER;
    }

    /**
     * Valida que la bodega tenga definido un tipo de propietario.
     *
     * @param ownerType tipo de propietario que se desea validar
     *
     * @throws IllegalArgumentException si el tipo es nulo
     */
    private void validateOwnerType(WarehouseOwnerType ownerType) {
        if (ownerType == null) {
            throw new IllegalArgumentException(
                    "Warehouse owner type must not be null."
            );
        }
    }
}