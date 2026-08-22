package application.domain.models;

import application.domain.valueobjects.InventoryCondition;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryMovementTest {

    @Test
    void shouldCreateMovementPerformedBySeller() {
        Warehouse warehouse = createSellerWarehouse();
        Inventory inventory = createInventory(warehouse);
        Seller seller = createSeller(warehouse);

        InventoryMovement movement = new InventoryMovement(
                inventory,
                InventoryMovementType.ENTRY,
                5,
                seller
        );

        assertEquals(inventory, movement.getInventory());
        assertEquals(
                InventoryMovementType.ENTRY,
                movement.getMovementType()
        );
        assertEquals(5, movement.getQuantity());
        assertEquals(seller, movement.getPerformedBy());
        assertTrue(movement.belongsTo(inventory));
    }

    @Test
    void shouldCreateMovementPerformedByLogisticsOperator() {
        Inventory inventory = createInventory(
                new Warehouse(WarehouseOwnerType.MARKETPLACE)
        );

        LogisticsOperator operator = new LogisticsOperator(
                "3001",
                "Logistics Operator",
                "operator@email.com",
                UserStatus.ACTIVE
        );

        InventoryMovement movement = new InventoryMovement(
                inventory,
                InventoryMovementType.RESERVATION,
                2,
                operator
        );

        assertEquals(operator, movement.getPerformedBy());
    }

    @Test
    void shouldRejectNullInventory() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new InventoryMovement(
                        null,
                        InventoryMovementType.ENTRY,
                        5,
                        createLogisticsOperator()
                )
        );
    }

    @Test
    void shouldRejectNullMovementType() {
        Inventory inventory = createInventory(
                new Warehouse(WarehouseOwnerType.MARKETPLACE)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new InventoryMovement(
                        inventory,
                        null,
                        5,
                        createLogisticsOperator()
                )
        );
    }

    @Test
    void shouldRejectNonPositiveQuantity() {
        Inventory inventory = createInventory(
                new Warehouse(WarehouseOwnerType.MARKETPLACE)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new InventoryMovement(
                        inventory,
                        InventoryMovementType.ADJUSTMENT,
                        0,
                        createLogisticsOperator()
                )
        );
    }

    @Test
    void shouldRejectNullUser() {
        Inventory inventory = createInventory(
                new Warehouse(WarehouseOwnerType.MARKETPLACE)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new InventoryMovement(
                        inventory,
                        InventoryMovementType.ENTRY,
                        5,
                        null
                )
        );
    }

    @Test
    void shouldRejectInactiveAuthorizedUser() {
        Inventory inventory = createInventory(
                new Warehouse(WarehouseOwnerType.MARKETPLACE)
        );

        LogisticsOperator operator = new LogisticsOperator(
                "3001",
                "Logistics Operator",
                "operator@email.com",
                UserStatus.INACTIVE
        );

        assertThrows(
                IllegalStateException.class,
                () -> new InventoryMovement(
                        inventory,
                        InventoryMovementType.ENTRY,
                        5,
                        operator
                )
        );
    }

    @Test
    void shouldRejectUnauthorizedUserRole() {
        Inventory inventory = createInventory(
                new Warehouse(WarehouseOwnerType.MARKETPLACE)
        );

        Administrator administrator = new Administrator(
                "4001",
                "Administrator",
                "admin@email.com",
                UserStatus.ACTIVE
        );

        assertThrows(
                IllegalStateException.class,
                () -> new InventoryMovement(
                        inventory,
                        InventoryMovementType.ENTRY,
                        5,
                        administrator
                )
        );
    }

    private Inventory createInventory(Warehouse warehouse) {
        Product product = new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );

        return new Inventory(
                product,
                warehouse,
                10,
                InventoryCondition.AVAILABLE
        );
    }

    private Warehouse createSellerWarehouse() {
        return new Warehouse(WarehouseOwnerType.SELLER);
    }

    private Seller createSeller(Warehouse warehouse) {
        return new Seller(
                "2001",
                "Seller",
                "seller@email.com",
                UserStatus.ACTIVE,
                warehouse
        );
    }

    private LogisticsOperator createLogisticsOperator() {
        return new LogisticsOperator(
                "3001",
                "Logistics Operator",
                "operator@email.com",
                UserStatus.ACTIVE
        );
    }
}
