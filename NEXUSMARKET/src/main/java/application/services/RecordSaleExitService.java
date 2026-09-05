package application.services;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.LogisticsOperator;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.SystemRole;
import application.ports.output.InventoryMovementRepository;

/**
 * Servicio encargado de registrar una salida de inventario por venta.
 *
 * La salida se registra cuando el pedido ya fue despachado.
 * No se vuelve a restar la cantidad disponible porque las unidades
 * fueron descontadas previamente durante la reserva.
 */
public class RecordSaleExitService {

    private final InventoryMovementRepository movementRepository;

    /**
     * Construye el servicio con el repositorio de movimientos.
     *
     * @param movementRepository repositorio de movimientos
     */
    public RecordSaleExitService(
            InventoryMovementRepository movementRepository
    ) {
        if (movementRepository == null) {
            throw new IllegalArgumentException(
                    "Inventory movement repository must not be null."
            );
        }

        this.movementRepository = movementRepository;
    }

    /**
     * Registra la salida por venta de un producto.
     *
     * @param performedBy usuario que registra la salida
     * @param order pedido relacionado con la venta
     * @param inventory inventario del producto vendido
     * @param quantity cantidad que salió de la bodega
     * @return movimiento registrado
     */
    public InventoryMovement record(
            User performedBy,
            Order order,
            Inventory inventory,
            int quantity
    ) {
        validateAuthorizedUser(performedBy);
        validateOrder(order);
        validateInventory(inventory);
        validateQuantity(quantity);
        validateDispatchedOrder(order);
        validateInventoryAccess(performedBy, inventory);
        validateProductAndQuantity(order, inventory, quantity);

        InventoryMovement movement =
                new InventoryMovement(
                        inventory,
                        InventoryMovementType.SALE_EXIT,
                        quantity,
                        performedBy
                );

        movementRepository.save(movement);

        return movement;
    }

    /**
     * Valida que el usuario pueda registrar movimientos.
     */
    private void validateAuthorizedUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "Performing user must not be null."
            );
        }

        if (!user.isActive()) {
            throw new IllegalStateException(
                    "Only active users can record sale exits."
            );
        }

        boolean isSeller =
                user.getRole() == SystemRole.SELLER;

        boolean isLogisticsOperator =
                user.getRole()
                        == SystemRole.LOGISTICS_OPERATOR;

        if (!isSeller && !isLogisticsOperator) {
            throw new IllegalStateException(
                    "User is not authorized to record sale exits."
            );
        }
    }

    /**
     * Valida que el pedido exista.
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Order must not be null."
            );
        }
    }

    /**
     * Valida que el inventario exista.
     */
    private void validateInventory(Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException(
                    "Inventory must not be null."
            );
        }
    }

    /**
     * Valida que la cantidad sea positiva.
     */
    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Sale exit quantity must be greater than zero."
            );
        }
    }

    /**
     * La salida física se registra después del despacho.
     */
    private void validateDispatchedOrder(Order order) {
        if (order.getStatus() != OrderStatus.DISPATCHED) {
            throw new IllegalStateException(
                    "Sale exit can only be recorded for a dispatched order."
            );
        }
    }

    /**
     * Comprueba que el usuario tenga acceso al inventario específico.
     */
    private void validateInventoryAccess(
            User user,
            Inventory inventory
    ) {
        if (user instanceof Seller seller) {
            if (!seller.managesProduct(
                    inventory.getProduct()
            )) {
                throw new IllegalStateException(
                        "Seller can only record exits for their own products."
                );
            }

            if (!seller.managesWarehouse(
                    inventory.getWarehouse()
            )) {
                throw new IllegalStateException(
                        "Seller can only record exits from their own warehouses."
                );
            }

            return;
        }

        if (user instanceof LogisticsOperator
                && !inventory.getWarehouse()
                        .isMarketplaceWarehouse()) {
            throw new IllegalStateException(
                    "Logistics operator can only manage marketplace inventory."
            );
        }
    }

    /**
     * Comprueba que el pedido contenga el producto y que
     * la cantidad registrada no supere la cantidad comprada.
     */
    private void validateProductAndQuantity(
            Order order,
            Inventory inventory,
            int quantity
    ) {
        int orderedQuantity = 0;

        for (OrderItem item : order.getItems()) {
            if (item.belongsTo(inventory.getProduct())) {
                orderedQuantity += item.getQuantity();
            }
        }

        if (orderedQuantity == 0) {
            throw new IllegalStateException(
                    "Inventory product does not belong to the order."
            );
        }

        if (quantity > orderedQuantity) {
            throw new IllegalStateException(
                    "Sale exit quantity exceeds the ordered quantity."
            );
        }
    }
}