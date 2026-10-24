package application.ports.output;

import application.domain.models.Inventory;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida utilizado para consultar inventarios.
 *
 * Las implementaciones pueden recuperar la información
 * desde MySQL, memoria u otra tecnología sin afectar
 * los servicios de aplicación.
 */
public interface InventoryQueryRepository {

    /**
     * Obtiene todos los inventarios registrados.
     *
     * @return lista de inventarios
     */
    List<Inventory> findAll();

    /**
     * Busca un inventario mediante su identificador.
     *
     * La implementación predeterminada mantiene compatibles
     * los repositorios en memoria existentes.
     *
     * @param inventoryId identificador consultado
     * @return inventario encontrado, si existe
     */
    default Optional<Inventory> findById(
            String inventoryId
    ) {
        if (inventoryId == null
                || inventoryId.isBlank()) {
            return Optional.empty();
        }

        return findAll()
                .stream()
                .filter(inventory ->
                        inventoryId.equals(
                                inventory.getInventoryId()
                        )
                )
                .findFirst();
    }

    /**
     * Obtiene los inventarios relacionados con un producto.
     *
     * @param productId identificador del producto
     * @return inventarios del producto
     */
    default List<Inventory> findByProductId(
            String productId
    ) {
        if (productId == null
                || productId.isBlank()) {
            return List.of();
        }

        return findAll()
                .stream()
                .filter(inventory ->
                        productId.equals(
                                inventory
                                        .getProduct()
                                        .getProductId()
                        )
                )
                .toList();
    }

    /**
     * Obtiene los inventarios almacenados
     * en una bodega.
     *
     * @param warehouseId identificador de la bodega
     * @return inventarios de la bodega
     */
    default List<Inventory> findByWarehouseId(
            String warehouseId
    ) {
        if (warehouseId == null
                || warehouseId.isBlank()) {
            return List.of();
        }

        return findAll()
                .stream()
                .filter(inventory ->
                        warehouseId.equals(
                                inventory
                                        .getWarehouse()
                                        .getWarehouseId()
                        )
                )
                .toList();
    }
}