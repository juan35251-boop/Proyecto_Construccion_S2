package application.adapters.output.persistence.jpa.mappers;

import application.adapters.output.persistence.jpa.entities.InventoryJpaEntity;
import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.Warehouse;

/**
 * Convierte inventarios entre el modelo de dominio
 * y su representación JPA.
 *
 * El mapeador no consulta bases de datos. Para reconstruir
 * el inventario recibe el producto y la bodega que fueron
 * recuperados previamente por el adaptador.
 */
public final class InventoryJpaMapper {

    /**
     * Evita la creación de instancias del mapeador.
     */
    private InventoryJpaMapper() {
    }

    /**
     * Convierte un inventario del dominio
     * en una entidad JPA.
     *
     * @param inventory inventario del dominio
     * @return entidad preparada para MySQL
     */
    public static InventoryJpaEntity toEntity(
            Inventory inventory
    ) {
        validateInventory(inventory);

        return new InventoryJpaEntity(
                inventory.getInventoryId(),
                inventory.getProduct().getProductId(),
                inventory.getWarehouse().getWarehouseId(),
                inventory.getAvailableQuantity(),
                inventory.getCondition()
        );
    }

    /**
     * Reconstruye un inventario del dominio.
     *
     * @param entity entidad recuperada desde MySQL
     * @param product producto relacionado
     * @param warehouse bodega relacionada
     * @return inventario del dominio
     */
    public static Inventory toDomain(
            InventoryJpaEntity entity,
            Product product,
            Warehouse warehouse
    ) {
        validateEntity(entity);
        validateProduct(product);
        validateWarehouse(warehouse);

        return new Inventory(
                entity.getInventoryId(),
                product,
                warehouse,
                entity.getAvailableQuantity(),
                entity.getCondition()
        );
    }

    /**
     * Valida que exista un inventario.
     *
     * @param inventory inventario que será validado
     */
    private static void validateInventory(
            Inventory inventory
    ) {
        if (inventory == null) {
            throw new IllegalArgumentException(
                    "Inventory must not be null."
            );
        }
    }

    /**
     * Valida que exista una entidad JPA.
     *
     * @param entity entidad que será validada
     */
    private static void validateEntity(
            InventoryJpaEntity entity
    ) {
        if (entity == null) {
            throw new IllegalArgumentException(
                    "Inventory entity must not be null."
            );
        }
    }

    /**
     * Valida el producto utilizado durante
     * la reconstrucción.
     *
     * @param product producto relacionado
     */
    private static void validateProduct(
            Product product
    ) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Inventory product must not be null."
            );
        }
    }

    /**
     * Valida la bodega utilizada durante
     * la reconstrucción.
     *
     * @param warehouse bodega relacionada
     */
    private static void validateWarehouse(
            Warehouse warehouse
    ) {
        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "Inventory warehouse must not be null."
            );
        }
    }
}