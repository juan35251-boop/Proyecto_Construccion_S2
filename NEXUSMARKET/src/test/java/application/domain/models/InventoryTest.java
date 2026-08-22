package application.domain.models;

import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.WarehouseOwnerType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryTest {

    @Test
    void shouldCreateInventory() {
        Inventory inventory = new Inventory(
                createProduct(),
                createWarehouse(),
                10
        );

        assertEquals(10, inventory.getAvailableQuantity());
    }

    @Test
    void shouldRejectNullProduct() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Inventory(
                        null,
                        createWarehouse(),
                        10
                )
        );
    }

    @Test
    void shouldRejectNullWarehouse() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Inventory(
                        createProduct(),
                        null,
                        10
                )
        );
    }

    @Test
    void shouldRejectNegativeInitialQuantity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Inventory(
                        createProduct(),
                        createWarehouse(),
                        -1
                )
        );
    }

    @Test
    void shouldAddQuantity() {
        Inventory inventory = new Inventory(
                createProduct(),
                createWarehouse(),
                10
        );

        inventory.addQuantity(5);

        assertEquals(15, inventory.getAvailableQuantity());
    }

    @Test
    void shouldRejectNonPositiveEntryQuantity() {
        Inventory inventory = new Inventory(
                createProduct(),
                createWarehouse(),
                10
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> inventory.addQuantity(0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> inventory.addQuantity(-1)
        );
    }

    @Test
    void shouldReserveAvailableQuantity() {
        Inventory inventory = new Inventory(
                createProduct(),
                createWarehouse(),
                10
        );

        inventory.reserveQuantity(4);

        assertEquals(6, inventory.getAvailableQuantity());
    }

    @Test
    void shouldRejectReservationGreaterThanAvailableQuantity() {
        Inventory inventory = new Inventory(
                createProduct(),
                createWarehouse(),
                10
        );

        assertThrows(
                IllegalStateException.class,
                () -> inventory.reserveQuantity(11)
        );

        assertEquals(10, inventory.getAvailableQuantity());
    }

    @Test
    void shouldRejectNonPositiveReservationQuantity() {
        Inventory inventory = new Inventory(
                createProduct(),
                createWarehouse(),
                10
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> inventory.reserveQuantity(0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> inventory.reserveQuantity(-1)
        );
    }

    @Test
    void shouldAdjustQuantity() {
        Inventory inventory = new Inventory(
                createProduct(),
                createWarehouse(),
                10
        );

        inventory.adjustQuantity(3);

        assertEquals(3, inventory.getAvailableQuantity());
    }

    @Test
    void shouldRejectNegativeAdjustment() {
        Inventory inventory = new Inventory(
                createProduct(),
                createWarehouse(),
                10
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> inventory.adjustQuantity(-1)
        );
    }

    private Product createProduct() {
        return new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );
    }

    private Warehouse createWarehouse() {
        return new Warehouse(
                WarehouseOwnerType.MARKETPLACE
        );
    }
}
