package application.domain.models;

import application.domain.valueobjects.InventoryCondition;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.WarehouseOwnerType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryTest {

    @Test
    void shouldCreateAvailableInventory() {
        Inventory inventory = createInventory(10);

        assertEquals(10, inventory.getAvailableQuantity());
        assertEquals(
                InventoryCondition.AVAILABLE,
                inventory.getCondition()
        );
    }

    @Test
    void shouldRejectNullProduct() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Inventory(
                        null,
                        createWarehouse(),
                        10,
                        InventoryCondition.AVAILABLE
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
                        10,
                        InventoryCondition.AVAILABLE
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
                        -1,
                        InventoryCondition.AVAILABLE
                )
        );
    }

    @Test
    void shouldRejectNullCondition() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Inventory(
                        createProduct(),
                        createWarehouse(),
                        10,
                        null
                )
        );
    }

    @Test
    void shouldAddQuantity() {
        Inventory inventory = createInventory(10);

        inventory.addQuantity(5);

        assertEquals(15, inventory.getAvailableQuantity());
    }

    @Test
    void shouldRejectNonPositiveEntryQuantity() {
        Inventory inventory = createInventory(10);

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
        Inventory inventory = createInventory(10);

        inventory.reserveQuantity(4);

        assertEquals(6, inventory.getAvailableQuantity());
    }

    @Test
    void shouldRejectReservationGreaterThanAvailableQuantity() {
        Inventory inventory = createInventory(10);

        assertThrows(
                IllegalStateException.class,
                () -> inventory.reserveQuantity(11)
        );

        assertEquals(10, inventory.getAvailableQuantity());
    }

    @Test
    void shouldRejectNonPositiveReservationQuantity() {
        Inventory inventory = createInventory(10);

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
    void shouldRejectDamagedInventoryReservation() {
        Inventory inventory = createInventory(10);

        inventory.changeCondition(InventoryCondition.DAMAGED);

        assertThrows(
                IllegalStateException.class,
                () -> inventory.reserveQuantity(1)
        );

        assertEquals(10, inventory.getAvailableQuantity());
    }

    @Test
    void shouldChangeInventoryCondition() {
        Inventory inventory = createInventory(10);

        inventory.changeCondition(InventoryCondition.DAMAGED);

        assertEquals(
                InventoryCondition.DAMAGED,
                inventory.getCondition()
        );
    }

    @Test
    void shouldAdjustQuantity() {
        Inventory inventory = createInventory(10);

        inventory.adjustQuantity(3);

        assertEquals(3, inventory.getAvailableQuantity());
    }

    @Test
    void shouldRejectNegativeAdjustment() {
        Inventory inventory = createInventory(10);

        assertThrows(
                IllegalArgumentException.class,
                () -> inventory.adjustQuantity(-1)
        );
    }

    private Inventory createInventory(int quantity) {
        return new Inventory(
                createProduct(),
                createWarehouse(),
                quantity,
                InventoryCondition.AVAILABLE
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
