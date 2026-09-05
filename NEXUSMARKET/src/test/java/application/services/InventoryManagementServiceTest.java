package application.services;

import application.domain.models.Administrator;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.LogisticsOperator;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.valueobjects.InventoryCondition;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.ports.output.InventoryMovementRepository;
import application.ports.output.InventoryRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del servicio de gestión de inventario.
 *
 * Los repositorios en memoria permiten comprobar las operaciones
 * sin utilizar todavía una base de datos.
 */
class InventoryManagementServiceTest {

    /**
     * Verifica el ingreso de unidades realizado por un vendedor
     * sobre uno de sus productos y bodegas.
     */
    @Test
    void shouldRegisterInventoryEntry() {
        TestContext context = createContext();

        Warehouse warehouse = createSellerWarehouse();

        Seller seller = createSeller(
                warehouse,
                UserStatus.ACTIVE
        );

        Product product = new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );

        /*
         * El producto debe pertenecer al catálogo del vendedor
         * antes de administrar su inventario.
         */
        seller.registerProduct(product);

        Inventory inventory = new Inventory(
                product,
                warehouse,
                10,
                InventoryCondition.AVAILABLE
        );

        InventoryMovement movement =
                context.service.registerEntry(
                        seller,
                        inventory,
                        5
                );

        assertEquals(15, inventory.getAvailableQuantity());
        assertEquals(
                InventoryMovementType.ENTRY,
                movement.getMovementType()
        );
        assertEquals(5, movement.getQuantity());
        assertTrue(
                context.inventoryRepository.contains(inventory)
        );
        assertTrue(
                context.movementRepository.contains(movement)
        );
    }

    /**
     * Verifica que un operador logístico pueda reservar inventario.
     */
    @Test
    void shouldReserveInventoryQuantity() {
        TestContext context = createContext();

        Inventory inventory = createMarketplaceInventory(
                10,
                InventoryCondition.AVAILABLE
        );

        LogisticsOperator operator = createOperator(
                UserStatus.ACTIVE
        );

        InventoryMovement movement =
                context.service.reserveQuantity(
                        operator,
                        inventory,
                        4
                );

        assertEquals(6, inventory.getAvailableQuantity());
        assertEquals(
                InventoryMovementType.RESERVATION,
                movement.getMovementType()
        );
        assertEquals(4, movement.getQuantity());
        assertTrue(
                context.movementRepository.contains(movement)
        );
    }

    /**
     * Verifica que la cantidad del inventario pueda ajustarse a cero.
     */
    @Test
    void shouldAdjustInventoryQuantityToZero() {
        TestContext context = createContext();

        Inventory inventory = createMarketplaceInventory(
                10,
                InventoryCondition.AVAILABLE
        );

        InventoryMovement movement =
                context.service.adjustQuantity(
                        createOperator(UserStatus.ACTIVE),
                        inventory,
                        0
                );

        assertEquals(0, inventory.getAvailableQuantity());
        assertEquals(
                InventoryMovementType.ADJUSTMENT,
                movement.getMovementType()
        );

        /*
         * El movimiento registra la diferencia entre
         * la cantidad anterior y la nueva.
         */
        assertEquals(10, movement.getQuantity());
    }

    /**
     * Verifica que una devolución aumente las existencias.
     */
    @Test
    void shouldRegisterInventoryReturn() {
        TestContext context = createContext();

        Inventory inventory = createMarketplaceInventory(
                5,
                InventoryCondition.AVAILABLE
        );

        InventoryMovement movement =
                context.service.registerReturn(
                        createOperator(UserStatus.ACTIVE),
                        inventory,
                        3
                );

        assertEquals(8, inventory.getAvailableQuantity());
        assertEquals(
                InventoryMovementType.RETURN,
                movement.getMovementType()
        );
        assertEquals(3, movement.getQuantity());
    }

    /**
     * Verifica que un usuario autorizado pueda cambiar
     * la condición del inventario.
     */
    @Test
    void shouldChangeInventoryCondition() {
        TestContext context = createContext();

        Inventory inventory = createMarketplaceInventory(
                5,
                InventoryCondition.AVAILABLE
        );

        context.service.changeCondition(
                createOperator(UserStatus.ACTIVE),
                inventory,
                InventoryCondition.DAMAGED
        );

        assertEquals(
                InventoryCondition.DAMAGED,
                inventory.getCondition()
        );
        assertTrue(
                context.inventoryRepository.contains(inventory)
        );

        /*
         * Cambiar la condición no genera
         * un movimiento de cantidad.
         */
        assertTrue(
                context.movementRepository
                        .getMovements()
                        .isEmpty()
        );
    }

    /**
     * Verifica que el repositorio de inventario sea obligatorio.
     */
    @Test
    void shouldRejectNullInventoryRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new InventoryManagementService(
                        null,
                        new InMemoryInventoryMovementRepository()
                )
        );
    }

    /**
     * Verifica que el repositorio de movimientos sea obligatorio.
     */
    @Test
    void shouldRejectNullMovementRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new InventoryManagementService(
                        new InMemoryInventoryRepository(),
                        null
                )
        );
    }

    /**
     * Verifica que un usuario inactivo no pueda
     * administrar inventario.
     */
    @Test
    void shouldRejectInactiveUser() {
        TestContext context = createContext();

        Inventory inventory = createMarketplaceInventory(
                10,
                InventoryCondition.AVAILABLE
        );

        assertThrows(
                IllegalStateException.class,
                () -> context.service.registerEntry(
                        createOperator(UserStatus.INACTIVE),
                        inventory,
                        5
                )
        );

        assertEquals(10, inventory.getAvailableQuantity());
        assertFalse(
                context.inventoryRepository.contains(inventory)
        );
    }

    /**
     * Verifica que un administrador no pueda
     * administrar inventario.
     */
    @Test
    void shouldRejectUnauthorizedUser() {
        TestContext context = createContext();

        Inventory inventory = createMarketplaceInventory(
                10,
                InventoryCondition.AVAILABLE
        );

        Administrator administrator =
                new Administrator(
                        "4001",
                        "Administrator",
                        "admin@email.com",
                        UserStatus.ACTIVE
                );

        assertThrows(
                IllegalStateException.class,
                () -> context.service.registerEntry(
                        administrator,
                        inventory,
                        5
                )
        );
    }

    /**
     * Verifica que la operación requiera un usuario.
     */
    @Test
    void shouldRejectNullUser() {
        TestContext context = createContext();

        Inventory inventory = createMarketplaceInventory(
                10,
                InventoryCondition.AVAILABLE
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> context.service.registerEntry(
                        null,
                        inventory,
                        5
                )
        );
    }

    /**
     * Verifica que no puedan reservarse más unidades
     * que las disponibles.
     */
    @Test
    void shouldRejectInsufficientInventory() {
        TestContext context = createContext();

        Inventory inventory = createMarketplaceInventory(
                3,
                InventoryCondition.AVAILABLE
        );

        assertThrows(
                IllegalStateException.class,
                () -> context.service.reserveQuantity(
                        createOperator(UserStatus.ACTIVE),
                        inventory,
                        4
                )
        );

        assertEquals(3, inventory.getAvailableQuantity());
        assertTrue(
                context.movementRepository
                        .getMovements()
                        .isEmpty()
        );
    }

    /**
     * Verifica que el inventario dañado no pueda reservarse.
     */
    @Test
    void shouldRejectDamagedInventoryReservation() {
        TestContext context = createContext();

        Inventory inventory = createMarketplaceInventory(
                10,
                InventoryCondition.DAMAGED
        );

        assertThrows(
                IllegalStateException.class,
                () -> context.service.reserveQuantity(
                        createOperator(UserStatus.ACTIVE),
                        inventory,
                        2
                )
        );

        assertEquals(10, inventory.getAvailableQuantity());
    }

    /**
     * Verifica que un ajuste realmente cambie la cantidad.
     */
    @Test
    void shouldRejectUnchangedAdjustment() {
        TestContext context = createContext();

        Inventory inventory = createMarketplaceInventory(
                10,
                InventoryCondition.AVAILABLE
        );

        assertThrows(
                IllegalStateException.class,
                () -> context.service.adjustQuantity(
                        createOperator(UserStatus.ACTIVE),
                        inventory,
                        10
                )
        );
    }

    /**
     * Verifica que un ajuste no permita cantidades negativas.
     */
    @Test
    void shouldRejectNegativeAdjustment() {
        TestContext context = createContext();

        Inventory inventory = createMarketplaceInventory(
                10,
                InventoryCondition.AVAILABLE
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> context.service.adjustQuantity(
                        createOperator(UserStatus.ACTIVE),
                        inventory,
                        -1
                )
        );
    }

    /**
     * Verifica que un vendedor no pueda modificar
     * el inventario de un producto ajeno.
     */
    @Test
    void shouldRejectInventoryFromAnotherSeller() {
        TestContext context = createContext();

        Warehouse sellerWarehouse =
                createSellerWarehouse();

        Seller seller = createSeller(
                sellerWarehouse,
                UserStatus.ACTIVE
        );

        Product anotherProduct = new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );

        /*
         * El producto no se registra en el catálogo del vendedor,
         * por lo tanto se considera un producto ajeno.
         */
        Inventory inventory = new Inventory(
                anotherProduct,
                sellerWarehouse,
                10,
                InventoryCondition.AVAILABLE
        );

        assertThrows(
                IllegalStateException.class,
                () -> context.service.registerEntry(
                        seller,
                        inventory,
                        5
                )
        );

        assertEquals(10, inventory.getAvailableQuantity());
        assertFalse(
                context.inventoryRepository.contains(inventory)
        );
    }

    /**
     * Verifica que un operador logístico no pueda modificar
     * inventario ubicado en una bodega de vendedor.
     */
    @Test
    void shouldRejectSellerInventoryForLogisticsOperator() {
        TestContext context = createContext();

        Warehouse sellerWarehouse =
                createSellerWarehouse();

        Inventory inventory = createInventory(
                sellerWarehouse,
                10,
                InventoryCondition.AVAILABLE
        );

        LogisticsOperator operator =
                createOperator(UserStatus.ACTIVE);

        assertThrows(
                IllegalStateException.class,
                () -> context.service.reserveQuantity(
                        operator,
                        inventory,
                        2
                )
        );

        assertEquals(10, inventory.getAvailableQuantity());
        assertFalse(
                context.inventoryRepository.contains(inventory)
        );
    }

    /**
     * Crea los repositorios en memoria y el servicio bajo prueba.
     */
    private TestContext createContext() {
        InMemoryInventoryRepository inventoryRepository =
                new InMemoryInventoryRepository();

        InMemoryInventoryMovementRepository movementRepository =
                new InMemoryInventoryMovementRepository();

        InventoryManagementService service =
                new InventoryManagementService(
                        inventoryRepository,
                        movementRepository
                );

        return new TestContext(
                service,
                inventoryRepository,
                movementRepository
        );
    }

    /**
     * Crea un inventario ubicado en una bodega del Marketplace.
     */
    private Inventory createMarketplaceInventory(
            int quantity,
            InventoryCondition condition
    ) {
        return createInventory(
                new Warehouse(
                        WarehouseOwnerType.MARKETPLACE
                ),
                quantity,
                condition
        );
    }

    /**
     * Crea un inventario para las pruebas.
     */
    private Inventory createInventory(
            Warehouse warehouse,
            int quantity,
            InventoryCondition condition
    ) {
        Product product = new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );

        return new Inventory(
                product,
                warehouse,
                quantity,
                condition
        );
    }

    /**
     * Crea una bodega perteneciente a un vendedor.
     */
    private Warehouse createSellerWarehouse() {
        return new Warehouse(
                WarehouseOwnerType.SELLER
        );
    }

    /**
     * Crea un vendedor con la bodega y el estado indicados.
     */
    private Seller createSeller(
            Warehouse warehouse,
            UserStatus status
    ) {
        return new Seller(
                "2001",
                "Test Seller",
                "seller@email.com",
                status,
                warehouse
        );
    }

    /**
     * Crea un operador logístico con el estado indicado.
     */
    private LogisticsOperator createOperator(
            UserStatus status
    ) {
        return new LogisticsOperator(
                "3001",
                "Logistics Operator",
                "operator@email.com",
                status
        );
    }

    /**
     * Agrupa los objetos utilizados por cada prueba.
     */
    private record TestContext(
            InventoryManagementService service,
            InMemoryInventoryRepository inventoryRepository,
            InMemoryInventoryMovementRepository movementRepository
    ) {
    }

    /**
     * Repositorio de inventarios utilizado durante las pruebas.
     */
    private static class InMemoryInventoryRepository
            implements InventoryRepository {

        private final List<Inventory> inventories =
                new ArrayList<>();

        @Override
        public void save(Inventory inventory) {
            inventories.add(inventory);
        }

        boolean contains(Inventory inventory) {
            return inventories.contains(inventory);
        }
    }

    /**
     * Repositorio de movimientos utilizado durante las pruebas.
     */
    private static class InMemoryInventoryMovementRepository
            implements InventoryMovementRepository {

        private final List<InventoryMovement> movements =
                new ArrayList<>();

        @Override
        public void save(InventoryMovement movement) {
            movements.add(movement);
        }

        boolean contains(InventoryMovement movement) {
            return movements.contains(movement);
        }

        List<InventoryMovement> getMovements() {
            return List.copyOf(movements);
        }
    }
}