package application.domain.models;

import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.SystemRole;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa el registro histórico de una operación
 * realizada sobre un inventario.
 *
 * Un movimiento registra el inventario afectado, el tipo,
 * la cantidad, el usuario responsable y el momento exacto
 * en el que ocurrió.
 *
 * Los movimientos serán almacenados en MongoDB como
 * información de auditoría y trazabilidad.
 */
public class InventoryMovement {

    private final String movementId;
    private final Inventory inventory;
    private final InventoryMovementType movementType;
    private final int quantity;
    private final User performedBy;
    private final Instant occurredAt;

    /**
     * Crea un movimiento nuevo.
     *
     * En una operación nueva, el usuario debe encontrarse
     * activo y tener autorización para administrar inventario.
     *
     * @param inventory inventario afectado
     * @param movementType tipo de movimiento
     * @param quantity cantidad involucrada
     * @param performedBy usuario responsable
     */
    public InventoryMovement(
            Inventory inventory,
            InventoryMovementType movementType,
            int quantity,
            User performedBy
    ) {
        this(
                UUID.randomUUID().toString(),
                inventory,
                movementType,
                quantity,
                performedBy,
                Instant.now(),
                true
        );
    }

    /**
     * Reconstruye un movimiento histórico almacenado.
     *
     * No vuelve a exigir que el usuario continúe activo,
     * porque bloquearlo posteriormente no debe impedir
     * consultar las operaciones que realizó anteriormente.
     *
     * @param movementId identificador del movimiento
     * @param inventory inventario relacionado
     * @param movementType tipo de movimiento
     * @param quantity cantidad involucrada
     * @param performedBy usuario que realizó la operación
     * @param occurredAt fecha y hora de la operación
     */
    public InventoryMovement(
            String movementId,
            Inventory inventory,
            InventoryMovementType movementType,
            int quantity,
            User performedBy,
            Instant occurredAt
    ) {
        this(
                movementId,
                inventory,
                movementType,
                quantity,
                performedBy,
                occurredAt,
                false
        );
    }

    /**
     * Constructor central para movimientos
     * nuevos e históricos.
     *
     * @param requireCurrentAuthorization indica si debe
     *                                    verificarse la autorización actual
     */
    private InventoryMovement(
            String movementId,
            Inventory inventory,
            InventoryMovementType movementType,
            int quantity,
            User performedBy,
            Instant occurredAt,
            boolean requireCurrentAuthorization
    ) {
        validateMovementId(movementId);
        validateInventory(inventory);
        validateMovementType(movementType);
        validateQuantity(quantity);
        validateUser(performedBy);
        validateOccurredAt(occurredAt);

        if (requireCurrentAuthorization) {
            validateAuthorizedUser(performedBy);
        }

        this.movementId = movementId;
        this.inventory = inventory;
        this.movementType = movementType;
        this.quantity = quantity;
        this.performedBy = performedBy;
        this.occurredAt = occurredAt;
    }

    public String getMovementId() {
        return movementId;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public InventoryMovementType getMovementType() {
        return movementType;
    }

    public int getQuantity() {
        return quantity;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    /**
     * Comprueba si el movimiento pertenece
     * al inventario indicado.
     *
     * @param inventory inventario consultado
     * @return true si tiene el mismo inventoryId
     */
    public boolean belongsTo(
            Inventory inventory
    ) {
        return this.inventory
                .isSameInventory(inventory);
    }

    private void validateMovementId(
            String movementId
    ) {
        if (movementId == null
                || movementId.isBlank()) {
            throw new IllegalArgumentException(
                    "Inventory movement identifier must not be empty."
            );
        }
    }

    private void validateInventory(
            Inventory inventory
    ) {
        if (inventory == null) {
            throw new IllegalArgumentException(
                    "Inventory movement must reference inventory."
            );
        }
    }

    private void validateMovementType(
            InventoryMovementType movementType
    ) {
        if (movementType == null) {
            throw new IllegalArgumentException(
                    "Inventory movement type must not be null."
            );
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Movement quantity must be greater than zero."
            );
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "Inventory movement must be performed by a user."
            );
        }
    }

    /**
     * Verifica la autorización para registrar
     * un movimiento nuevo.
     *
     * @param user usuario que ejecuta la operación
     */
    private void validateAuthorizedUser(User user) {
        if (!user.isActive()) {
            throw new IllegalStateException(
                    "Only active users can manage inventory."
            );
        }

        boolean isSeller =
                user.getRole() == SystemRole.SELLER;

        boolean isLogisticsOperator =
                user.getRole()
                        == SystemRole.LOGISTICS_OPERATOR;

        if (!isSeller && !isLogisticsOperator) {
            throw new IllegalStateException(
                    "User is not authorized to manage inventory."
            );
        }
    }

    private void validateOccurredAt(
            Instant occurredAt
    ) {
        if (occurredAt == null) {
            throw new IllegalArgumentException(
                    "Inventory movement occurrence date must not be null."
            );
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object
                instanceof InventoryMovement movement)) {
            return false;
        }

        return movementId.equals(
                movement.movementId
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(movementId);
    }
}