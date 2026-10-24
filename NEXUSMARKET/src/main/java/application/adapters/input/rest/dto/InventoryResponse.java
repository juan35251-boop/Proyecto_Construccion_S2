package application.adapters.input.rest.dto;

import application.domain.models.Inventory;
import application.domain.valueobjects.InventoryCondition;

/**
 * Respuesta utilizada por la API para representar
 * el estado actual de un inventario.
 *
 * @param inventoryId identificador del inventario
 * @param productId identificador del producto
 * @param warehouseId identificador de la bodega
 * @param availableQuantity cantidad disponible
 * @param condition condición operativa
 */
public record InventoryResponse(
        String inventoryId,
        String productId,
        String warehouseId,
        int availableQuantity,
        InventoryCondition condition
) {

    /**
     * Convierte un inventario del dominio
     * en una respuesta para la API.
     *
     * @param inventory inventario convertido
     * @return respuesta construida
     */
    public static InventoryResponse from(
            Inventory inventory
    ) {
        if (inventory == null) {
            throw new IllegalArgumentException(
                    "Inventory must not be null."
            );
        }

        if (inventory.getProduct() == null) {
            throw new IllegalStateException(
                    "Inventory product must not be null."
            );
        }

        if (inventory.getWarehouse() == null) {
            throw new IllegalStateException(
                    "Inventory warehouse must not be null."
            );
        }

        return new InventoryResponse(
                inventory.getInventoryId(),
                inventory.getProduct().getProductId(),
                inventory.getWarehouse().getWarehouseId(),
                inventory.getAvailableQuantity(),
                inventory.getCondition()
        );
    }
}