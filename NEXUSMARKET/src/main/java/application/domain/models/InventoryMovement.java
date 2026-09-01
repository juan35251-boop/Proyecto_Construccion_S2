package application.domain.models;

import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.SystemRole;

/**
 * Representa el registro de un movimiento realizado sobre un inventario.
 *
 * Un movimiento indica el inventario afectado, el tipo de operación,
 * la cantidad involucrada y el usuario responsable de realizarla.
 *
 * Solamente un vendedor o un operador logístico activo puede registrar
 * movimientos de inventario.
 *
 * Esta clase conserva la información del movimiento, pero no modifica
 * directamente las existencias del objeto {@link Inventory}.
 */
public class InventoryMovement {

    /**
     * Inventario al que pertenece el movimiento.
     */
    private final Inventory inventory;

    /**
     * Tipo de movimiento realizado sobre el inventario.
     */
    private final InventoryMovementType movementType;

    /**
     * Cantidad de unidades involucradas en el movimiento.
     *
     * Siempre debe ser mayor que cero.
     */
    private final int quantity;

    /**
     * Usuario responsable de realizar el movimiento.
     */
    private final User performedBy;

    /**
     * Crea el registro de un movimiento de inventario.
     *
     * Antes de crear el movimiento se valida que el inventario y el tipo
     * de movimiento existan, que la cantidad sea válida y que el usuario
     * tenga autorización para gestionar el inventario.
     *
     * @param inventory inventario asociado al movimiento
     * @param movementType tipo de movimiento realizado
     * @param quantity cantidad de unidades involucradas
     * @param performedBy usuario que realizó el movimiento
     *
     * @throws IllegalArgumentException si el inventario es nulo
     * @throws IllegalArgumentException si el tipo de movimiento es nulo
     * @throws IllegalArgumentException si la cantidad es menor
     *                                  o igual a cero
     * @throws IllegalArgumentException si el usuario es nulo
     * @throws IllegalStateException si el usuario no está activo
     * @throws IllegalStateException si el usuario no tiene un rol
     *                               autorizado para gestionar inventario
     */
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

    /**
     * Obtiene el inventario asociado al movimiento.
     *
     * @return el inventario relacionado
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Obtiene el tipo de movimiento registrado.
     *
     * @return el tipo de movimiento de inventario
     */
    public InventoryMovementType getMovementType() {
        return movementType;
    }

    /**
     * Obtiene la cantidad de unidades involucradas.
     *
     * @return la cantidad del movimiento
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Obtiene el usuario que realizó el movimiento.
     *
     * @return el usuario responsable
     */
    public User getPerformedBy() {
        return performedBy;
    }

    /**
     * Determina si el movimiento pertenece al inventario recibido.
     *
     * La comparación se realiza por identidad, por lo que las referencias
     * deben apuntar a la misma instancia de {@link Inventory}.
     *
     * @param inventory inventario que se desea comparar
     * @return {@code true} si corresponde a la misma instancia;
     *         de lo contrario, {@code false}
     */
    public boolean belongsTo(Inventory inventory) {
        return this.inventory == inventory;
    }

    /**
     * Valida que el movimiento esté relacionado con un inventario.
     *
     * @param inventory inventario que se desea validar
     *
     * @throws IllegalArgumentException si el inventario es nulo
     */
    private void validateInventory(Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException(
                    "Inventory movement must reference inventory."
            );
        }
    }

    /**
     * Valida que el movimiento tenga un tipo definido.
     *
     * @param movementType tipo de movimiento que se desea validar
     *
     * @throws IllegalArgumentException si el tipo de movimiento es nulo
     */
    private void validateMovementType(
            InventoryMovementType movementType
    ) {
        if (movementType == null) {
            throw new IllegalArgumentException(
                    "Inventory movement type must not be null."
            );
        }
    }

    /**
     * Valida que la cantidad del movimiento sea mayor que cero.
     *
     * @param quantity cantidad que se desea validar
     *
     * @throws IllegalArgumentException si la cantidad es menor
     *                                  o igual a cero
     */
    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Movement quantity must be greater than zero."
            );
        }
    }

    /**
     * Valida que el usuario pueda gestionar movimientos de inventario.
     *
     * El usuario debe existir, estar activo y tener el rol de vendedor
     * o de operador logístico.
     *
     * @param user usuario que se desea validar
     *
     * @throws IllegalArgumentException si el usuario es nulo
     * @throws IllegalStateException si el usuario no está activo
     * @throws IllegalStateException si su rol no es vendedor ni
     *                               operador logístico
     */
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