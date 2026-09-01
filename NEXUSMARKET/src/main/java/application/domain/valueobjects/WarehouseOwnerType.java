package application.domain.valueobjects;

/**
 * Representa los tipos de propietario que puede tener una bodega
 * dentro de NexusMarket.
 *
 * Permite clasificar una bodega como propiedad del marketplace
 * o como una bodega administrada por un vendedor.
 */
public enum WarehouseOwnerType {

    /**
     * Indica que la bodega pertenece directamente a NexusMarket
     * y funciona como una bodega central del marketplace.
     */
    MARKETPLACE,

    /**
     * Indica que la bodega pertenece o está asociada a un vendedor.
     */
    SELLER
}