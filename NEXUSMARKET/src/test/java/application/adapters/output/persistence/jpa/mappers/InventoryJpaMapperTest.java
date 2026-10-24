package application.adapters.output.persistence.jpa.mappers;

import application.adapters.output.persistence.jpa.entities.InventoryJpaEntity;
import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.Warehouse;
import application.domain.valueobjects.InventoryCondition;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.WarehouseOwnerType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas unitarias del mapeador JPA de inventarios.
 *
 * Estas pruebas no requieren MySQL porque solamente
 * verifican la conversión entre objetos.
 */
class InventoryJpaMapperTest {

    /**
     * Comprueba la conversión de un inventario
     * del dominio hacia una entidad JPA.
     */
    @Test
    void shouldMapInventoryToJpaEntity() {
        Product product =
                new Product(
                        ProductType.PHYSICAL,
                        ProductStatus.PUBLISHED
                );

        Warehouse warehouse =
                new Warehouse(
                        "warehouse-001",
                        WarehouseOwnerType.MARKETPLACE
                );

        Inventory inventory =
                new Inventory(
                        "inventory-001",
                        product,
                        warehouse,
                        25,
                        InventoryCondition.AVAILABLE
                );

        InventoryJpaEntity entity =
                InventoryJpaMapper.toEntity(
                        inventory
                );

        assertEquals(
                "inventory-001",
                entity.getInventoryId()
        );

        assertEquals(
                product.getProductId(),
                entity.getProductId()
        );

        assertEquals(
                "warehouse-001",
                entity.getWarehouseId()
        );

        assertEquals(
                25,
                entity.getAvailableQuantity()
        );

        assertEquals(
                InventoryCondition.AVAILABLE,
                entity.getCondition()
        );
    }

    /**
     * Comprueba que una entidad JPA pueda reconstruir
     * correctamente un inventario del dominio.
     */
    @Test
    void shouldMapJpaEntityToInventory() {
        Product product =
                new Product(
                        ProductType.PHYSICAL,
                        ProductStatus.PUBLISHED
                );

        Warehouse warehouse =
                new Warehouse(
                        "warehouse-002",
                        WarehouseOwnerType.SELLER
                );

        InventoryJpaEntity entity =
                new InventoryJpaEntity(
                        10L,
                        "inventory-002",
                        product.getProductId(),
                        warehouse.getWarehouseId(),
                        40,
                        InventoryCondition.AVAILABLE
                );

        Inventory inventory =
                InventoryJpaMapper.toDomain(
                        entity,
                        product,
                        warehouse
                );

        assertEquals(
                "inventory-002",
                inventory.getInventoryId()
        );

        assertSame(
                product,
                inventory.getProduct()
        );

        assertSame(
                warehouse,
                inventory.getWarehouse()
        );

        assertEquals(
                40,
                inventory.getAvailableQuantity()
        );

        assertEquals(
                InventoryCondition.AVAILABLE,
                inventory.getCondition()
        );
    }

    /**
     * Comprueba que el mapeador rechace
     * un inventario nulo.
     */
    @Test
    void shouldRejectNullInventory() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                InventoryJpaMapper
                                        .toEntity(null)
                );

        assertEquals(
                "Inventory must not be null.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba que el mapeador rechace
     * una entidad JPA nula.
     */
    @Test
    void shouldRejectNullInventoryEntity() {
        Product product =
                new Product(
                        ProductType.PHYSICAL,
                        ProductStatus.PUBLISHED
                );

        Warehouse warehouse =
                new Warehouse(
                        WarehouseOwnerType.MARKETPLACE
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                InventoryJpaMapper.toDomain(
                                        null,
                                        product,
                                        warehouse
                                )
                );

        assertEquals(
                "Inventory entity must not be null.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba que la reconstrucción requiera
     * un producto.
     */
    @Test
    void shouldRejectNullProduct() {
        Warehouse warehouse =
                new Warehouse(
                        WarehouseOwnerType.MARKETPLACE
                );

        InventoryJpaEntity entity =
                new InventoryJpaEntity(
                        "inventory-003",
                        "product-003",
                        warehouse.getWarehouseId(),
                        5,
                        InventoryCondition.AVAILABLE
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                InventoryJpaMapper.toDomain(
                                        entity,
                                        null,
                                        warehouse
                                )
                );

        assertEquals(
                "Inventory product must not be null.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba que la reconstrucción requiera
     * una bodega.
     */
    @Test
    void shouldRejectNullWarehouse() {
        Product product =
                new Product(
                        ProductType.PHYSICAL,
                        ProductStatus.PUBLISHED
                );

        InventoryJpaEntity entity =
                new InventoryJpaEntity(
                        "inventory-004",
                        product.getProductId(),
                        "warehouse-004",
                        5,
                        InventoryCondition.AVAILABLE
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                InventoryJpaMapper.toDomain(
                                        entity,
                                        product,
                                        null
                                )
                );

        assertEquals(
                "Inventory warehouse must not be null.",
                exception.getMessage()
        );
    }
}