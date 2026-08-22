package application.domain.models;

import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.SystemRole;

public class InventoryMovement {

    private final Inventory inventory;
    private final InventoryMovementType movementType;
    private final int quantity;
    private final User performedBy;

    public InventoryMovement(
            Inventory inventory,
            InventoryMovementType movementType,
            int quantity,
            User performedBy
    ) {
        validateInventory(inventory);
        validateMovementType(movementType);
        validateQuantity(quantity);
        validateAuthorizedUser(performedBy);

        this.inventory = inventory;
        this.movementType = movementType;
        this.quantity = quantity;
        this.performedBy = performedBy;
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

    public boolean belongsTo(Inventory inventory) {
        return this.inventory == inventory;
    }

    private void validateInventory(Inventory inventory) {
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

    private void validateAuthorizedUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "Inventory movement must be performed by a user."
            );
        }

        if (!user.isActive()) {
            throw new IllegalStateException(
                    "Only active users can manage inventory."
            );
        }

        boolean isSeller =
                user.getRole() == SystemRole.SELLER;

        boolean isLogisticsOperator =
                user.getRole() == SystemRole.LOGISTICS_OPERATOR;

        if (!isSeller && !isLogisticsOperator) {
            throw new IllegalStateException(
                    "User is not authorized to manage inventory."
            );
        }
    }
}