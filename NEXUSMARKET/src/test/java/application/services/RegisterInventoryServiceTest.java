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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del servicio encargado
 * de registrar inventarios.
 */
class RegisterInventoryServiceTest {

    /**
     * Verifica que un vendedor pueda registrar inventario
     * para uno de sus productos y bodegas.
     */
    @Test
    void shouldRegisterSellerInventory() {
        TestContext context = createContext();
        SellerData sellerData = createSellerData(
                UserStatus.ACTIVE
        );

        Inventory inventory = context.service.register(
                sellerData.seller,
                sellerData.product,
                sellerData.warehouse,
                10,
                InventoryCondition.AVAILABLE
        );

        assertEquals(10, inventory.getAvailableQuantity());
        assertEquals(sellerData.product, inventory.getProduct());
        assertEquals(sellerData.warehouse, inventory.getWarehouse());
        assertTrue(
                context.inventoryRepository.contains(inventory)
        );

        InventoryMovement movement =
                context.movementRepository
                        .getMovements()
                        .get(0);

        assertEquals(
                InventoryMovementType.ENTRY,
                movement.getMovementType()
        );
        assertEquals(10, movement.getQuantity());
    }

    /**
     * Verifica que un operador logístico pueda registrar
     * inventario en una bodega del Marketplace.
     */
    @Test
    void shouldRegisterMarketplaceInventory() {
        TestContext context = createContext();

        Product product = createProduct(
                ProductType.PHYSICAL
        );

        Warehouse warehouse = new Warehouse(
                WarehouseOwnerType.MARKETPLACE
        );

        LogisticsOperator operator = createOperator(
                UserStatus.ACTIVE
        );

        Inventory inventory = context.service.register(
                operator,
                product,
                warehouse,
                5,
                InventoryCondition.AVAILABLE
        );

        assertTrue(
                context.inventoryRepository.contains(inventory)
        );
        assertEquals(
                1,
                context.movementRepository
                        .getMovements()
                        .size()
        );
    }

    /**
     * Verifica que un inventario iniciado en cero
     * no genere un movimiento de ingreso.
     */
    @Test
    void shouldRegisterZeroQuantityWithoutMovement() {
        TestContext context = createContext();

        Product product = createProduct(
                ProductType.PHYSICAL
        );

        Warehouse warehouse = new Warehouse(
                WarehouseOwnerType.MARKETPLACE
        );

        Inventory inventory = context.service.register(
                createOperator(UserStatus.ACTIVE),
                product,
                warehouse,
                0,
                InventoryCondition.AVAILABLE
        );

        assertEquals(0, inventory.getAvailableQuantity());
        assertTrue(
                context.inventoryRepository.contains(inventory)
        );
        assertTrue(
                context.movementRepository
                        .getMovements()
                        .isEmpty()
        );
    }

    /**
     * Verifica que el repositorio de inventarios sea obligatorio.
     */
    @Test
    void shouldRejectNullInventoryRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegisterInventoryService(
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
                () -> new RegisterInventoryService(
                        new InMemoryInventoryRepository(),
                        null
                )
        );
    }

    /**
     * Verifica que el registro requiera un usuario.
     */
    @Test
    void shouldRejectNullUser() {
        TestContext context = createContext();

        assertThrows(
                IllegalArgumentException.class,
                () -> context.service.register(
                        null,
                        createProduct(ProductType.PHYSICAL),
                        new Warehouse(
                                WarehouseOwnerType.MARKETPLACE
                        ),
                        5,
                        InventoryCondition.AVAILABLE
                )
        );
    }

    /**
     * Verifica que un usuario inactivo no pueda
     * registrar inventarios.
     */
    @Test
    void shouldRejectInactiveUser() {
        TestContext context = createContext();

        assertThrows(
                IllegalStateException.class,
                () -> context.service.register(
                        createOperator(UserStatus.INACTIVE),
                        createProduct(ProductType.PHYSICAL),
                        new Warehouse(
                                WarehouseOwnerType.MARKETPLACE
                        ),
                        5,
                        InventoryCondition.AVAILABLE
                )
        );
    }

    /**
     * Verifica que un administrador no pueda
     * registrar inventarios.
     */
    @Test
    void shouldRejectUnauthorizedUser() {
        TestContext context = createContext();

        Administrator administrator =
                new Administrator(
                        "4001",
                        "Administrator",
                        "admin@email.com",
                        UserStatus.ACTIVE
                );

        assertThrows(
                IllegalStateException.class,
                () -> context.service.register(
                        administrator,
                        createProduct(ProductType.PHYSICAL),
                        new Warehouse(
                                WarehouseOwnerType.MARKETPLACE
                        ),
                        5,
                        InventoryCondition.AVAILABLE
                )
        );
    }

    /**
     * Verifica que un vendedor no registre inventario
     * para un producto ajeno.
     */
    @Test
    void shouldRejectProductNotManagedBySeller() {
        TestContext context = createContext();
        SellerData sellerData = createSellerData(
                UserStatus.ACTIVE
        );

        Product anotherProduct = createProduct(
                ProductType.PHYSICAL
        );

        assertThrows(
                IllegalStateException.class,
                () -> context.service.register(
                        sellerData.seller,
                        anotherProduct,
                        sellerData.warehouse,
                        5,
                        InventoryCondition.AVAILABLE
                )
        );
    }

    /**
     * Verifica que un vendedor no utilice
     * una bodega que no administra.
     */
    @Test
    void shouldRejectWarehouseNotManagedBySeller() {
        TestContext context = createContext();
        SellerData sellerData = createSellerData(
                UserStatus.ACTIVE
        );

        Warehouse anotherWarehouse = new Warehouse(
                WarehouseOwnerType.SELLER
        );

        assertThrows(
                IllegalStateException.class,
                () -> context.service.register(
                        sellerData.seller,
                        sellerData.product,
                        anotherWarehouse,
                        5,
                        InventoryCondition.AVAILABLE
                )
        );
    }

    /**
     * Verifica que un operador logístico no registre
     * inventario en una bodega de vendedor.
     */
    @Test
    void shouldRejectSellerWarehouseForOperator() {
        TestContext context = createContext();

        assertThrows(
                IllegalStateException.class,
                () -> context.service.register(
                        createOperator(UserStatus.ACTIVE),
                        createProduct(ProductType.PHYSICAL),
                        new Warehouse(
                                WarehouseOwnerType.SELLER
                        ),
                        5,
                        InventoryCondition.AVAILABLE
                )
        );
    }

    /**
     * Verifica que los productos digitales
     * no tengan inventario físico.
     */
    @Test
    void shouldRejectDigitalProduct() {
        TestContext context = createContext();

        assertThrows(
                IllegalStateException.class,
                () -> context.service.register(
                        createOperator(UserStatus.ACTIVE),
                        createProduct(ProductType.DIGITAL),
                        new Warehouse(
                                WarehouseOwnerType.MARKETPLACE
                        ),
                        5,
                        InventoryCondition.AVAILABLE
                )
        );
    }

    /**
     * Verifica que la cantidad inicial no sea negativa.
     */
    @Test
    void shouldRejectNegativeInitialQuantity() {
        TestContext context = createContext();

        assertThrows(
                IllegalArgumentException.class,
                () -> context.service.register(
                        createOperator(UserStatus.ACTIVE),
                        createProduct(ProductType.PHYSICAL),
                        new Warehouse(
                                WarehouseOwnerType.MARKETPLACE
                        ),
                        -1,
                        InventoryCondition.AVAILABLE
                )
        );
    }

    /**
     * Verifica que la condición inicial sea obligatoria.
     */
    @Test
    void shouldRejectNullCondition() {
        TestContext context = createContext();

        assertThrows(
                IllegalArgumentException.class,
                () -> context.service.register(
                        createOperator(UserStatus.ACTIVE),
                        createProduct(ProductType.PHYSICAL),
                        new Warehouse(
                                WarehouseOwnerType.MARKETPLACE
                        ),
                        5,
                        null
                )
        );
    }

    /**
     * Crea el servicio y sus repositorios en memoria.
     */
    private TestContext createContext() {
        InMemoryInventoryRepository inventoryRepository =
                new InMemoryInventoryRepository();

        InMemoryInventoryMovementRepository movementRepository =
                new InMemoryInventoryMovementRepository();

        RegisterInventoryService service =
                new RegisterInventoryService(
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
     * Crea un vendedor junto con uno de sus productos
     * y una de sus bodegas.
     */
    private SellerData createSellerData(
            UserStatus status
    ) {
        Warehouse warehouse = new Warehouse(
                WarehouseOwnerType.SELLER
        );

        Seller seller = new Seller(
                "2001",
                "Test Seller",
                "seller@email.com",
                status,
                warehouse
        );

        Product product = createProduct(
                ProductType.PHYSICAL
        );

        seller.registerProduct(product);

        return new SellerData(
                seller,
                product,
                warehouse
        );
    }

    /**
     * Crea un producto con el tipo indicado.
     */
    private Product createProduct(ProductType type) {
        return new Product(
                type,
                ProductStatus.PUBLISHED
        );
    }

    /**
     * Crea un operador logístico.
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
     * Agrupa los objetos generales de una prueba.
     */
    private record TestContext(
            RegisterInventoryService service,
            InMemoryInventoryRepository inventoryRepository,
            InMemoryInventoryMovementRepository movementRepository
    ) {
    }

    /**
     * Agrupa un vendedor, su producto y su bodega.
     */
    private record SellerData(
            Seller seller,
            Product product,
            Warehouse warehouse
    ) {
    }

    /**
     * Repositorio de inventarios utilizado en las pruebas.
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
     * Repositorio de movimientos utilizado en las pruebas.
     */
    private static class InMemoryInventoryMovementRepository
            implements InventoryMovementRepository {

        private final List<InventoryMovement> movements =
                new ArrayList<>();

        @Override
        public void save(InventoryMovement movement) {
            movements.add(movement);
        }

        List<InventoryMovement> getMovements() {
            return List.copyOf(movements);
        }
    }
}