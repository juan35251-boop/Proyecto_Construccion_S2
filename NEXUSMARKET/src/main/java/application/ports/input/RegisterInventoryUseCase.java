package application.ports.input;

import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.InventoryCondition;

/**
 * Puerto de entrada que define el caso de uso
 * para registrar inventario en NexusMarket.
 *
 * El inventario solamente aplica para productos físicos
 * y siempre debe estar relacionado con una bodega.
 */
public interface RegisterInventoryUseCase {

    /**
     * Registra el inventario inicial de un producto
     * dentro de una bodega.
     *
     * Si la cantidad inicial es superior a cero, también
     * se genera un movimiento de inventario de tipo ENTRY.
     *
     * @param performedBy usuario que realiza la operación
     * @param product producto físico almacenado
     * @param warehouse bodega donde se almacena el producto
     * @param initialQuantity cantidad inicial disponible
     * @param condition condición inicial del inventario
     * @return inventario registrado
     *
     * @throws IllegalArgumentException si algún dato
     *         obligatorio es inválido
     * @throws IllegalStateException si el usuario no tiene
     *         permiso o el producto es digital
     */
    Inventory register(
            User performedBy,
            Product product,
            Warehouse warehouse,
            int initialQuantity,
            InventoryCondition condition
    );
}