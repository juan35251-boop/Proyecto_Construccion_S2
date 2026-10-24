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
     * Registra un inventario utilizando identificadores.
     *
     * Esta operación está diseñada para ser utilizada
     * por los controladores REST, porque la API recibe
     * identificadores y no objetos completos del dominio.
     *
     * El servicio deberá localizar al usuario, producto
     * y bodega antes de registrar el inventario.
     *
     * @param performedByIdentification identificación del usuario
     *                                  que realiza la operación
     * @param productId identificador del producto
     * @param warehouseId identificador de la bodega
     * @param initialQuantity cantidad inicial disponible
     * @param condition condición inicial del inventario
     * @return inventario registrado
     *
     * @throws IllegalArgumentException si algún identificador
     *         o dato obligatorio es inválido
     * @throws IllegalStateException si alguna entidad no existe,
     *         el usuario no está autorizado o el producto es digital
     */
    Inventory register(
            String performedByIdentification,
            String productId,
            String warehouseId,
            int initialQuantity,
            InventoryCondition condition
    );

    /**
     * Registra un inventario utilizando objetos
     * completos del dominio.
     *
     * Esta operación se conserva para mantener compatibilidad
     * con los servicios y pruebas unitarias existentes.
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