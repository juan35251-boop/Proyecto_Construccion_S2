package application.ports.input;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Order;
import application.domain.models.User;

/**
 * Puerto de entrada que define el caso de uso
 * para registrar salidas de inventario por venta.
 *
 * La salida se registra cuando el pedido ya fue
 * despachado físicamente.
 */
public interface RecordSaleExitUseCase {

    /**
     * Registra la salida de inventario correspondiente
     * a un producto vendido.
     *
     * La operación puede ser realizada por un vendedor
     * autorizado o por un operador logístico autorizado.
     *
     * @param performedBy usuario que registra la salida
     * @param order pedido relacionado con la venta
     * @param inventory inventario del producto vendido
     * @param quantity cantidad que salió de la bodega
     * @return movimiento de inventario registrado
     *
     * @throws IllegalArgumentException si algún dato
     *         obligatorio es inválido
     * @throws IllegalStateException si el usuario no está
     *         autorizado, la orden no fue despachada,
     *         el producto no pertenece a la orden o la
     *         cantidad supera las unidades compradas
     */
    InventoryMovement record(
            User performedBy,
            Order order,
            Inventory inventory,
            int quantity
    );
}