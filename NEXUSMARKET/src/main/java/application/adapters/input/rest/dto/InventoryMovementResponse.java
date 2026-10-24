package application.adapters.input.rest.dto;

import application.domain.models.InventoryMovement;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.SystemRole;

import java.time.Instant;

/**
 * Respuesta utilizada por la API para representar
 * un movimiento histórico de inventario.
 *
 * @param movementId identificador del movimiento
 * @param inventoryId identificador del inventario
 * @param productId identificador del producto
 * @param warehouseId identificador de la bodega
 * @param movementType tipo de movimiento
 * @param quantity cantidad relacionada con el movimiento
 * @param performedByIdentification identificación del responsable
 * @param performedByRole rol del responsable
 * @param occurredAt fecha y hora del movimiento
 */
public record InventoryMovementResponse(
        String movementId,
        String inventoryId,
        String productId,
        String warehouseId,
        InventoryMovementType movementType,
        int quantity,
        String performedByIdentification,
        SystemRole performedByRole,
        Instant occurredAt
) {

    /**
     * Convierte un movimiento del dominio
     * en una respuesta para la API.
     *
     * @param movement movimiento convertido
     * @return respuesta construida
     */
    public static InventoryMovementResponse from(
            InventoryMovement movement
    ) {
        if (movement == null) {
            throw new IllegalArgumentException(
                    "Inventory movement must not be null."
            );
        }

        if (movement.getInventory() == null) {
            throw new IllegalStateException(
                    "Movement inventory must not be null."
            );
        }

        if (movement.getInventory().getProduct() == null) {
            throw new IllegalStateException(
                    "Movement product must not be null."
            );
        }

        if (movement.getInventory().getWarehouse() == null) {
            throw new IllegalStateException(
                    "Movement warehouse must not be null."
            );
        }

        if (movement.getPerformedBy() == null) {
            throw new IllegalStateException(
                    "Movement responsible user must not be null."
            );
        }

        return new InventoryMovementResponse(
                movement.getMovementId(),
                movement.getInventory().getInventoryId(),
                movement.getInventory()
                        .getProduct()
                        .getProductId(),
                movement.getInventory()
                        .getWarehouse()
                        .getWarehouseId(),
                movement.getMovementType(),
                movement.getQuantity(),
                movement.getPerformedBy()
                        .getIdentification(),
                movement.getPerformedBy()
                        .getRole(),
                movement.getOccurredAt()
        );
    }
}