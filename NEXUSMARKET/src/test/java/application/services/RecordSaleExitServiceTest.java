package application.services;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.LogisticsOperator;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.InventoryCondition;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.services.support.InMemoryInventoryMovementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas del servicio que registra la salida
 * física de productos vendidos.
 */
class RecordSaleExitServiceTest {

    @Test
    @DisplayName("El vendedor registra la salida de su producto")
    void sellerShouldRecordSaleExit() {
        InMemoryInventoryMovementRepository repository =
                new InMemoryInventoryMovementRepository();

        Warehouse warehouse =
                new Warehouse(WarehouseOwnerType.SELLER);

        Product product = createProduct();

        Seller seller = createSeller(
                UserStatus.ACTIVE,
                warehouse
        );

        seller.registerProduct(product);

        Inventory inventory = createInventory(
                product,
                warehouse
        );

        Order order = createDispatchedOrder(
                product,
                2
        );

        RecordSaleExitService service =
                new RecordSaleExitService(repository);

        InventoryMovement movement = service.record(
                seller,
                order,
                inventory,
                2
        );

        assertEquals(
                InventoryMovementType.SALE_EXIT,
                movement.getMovementType()
        );

        assertEquals(2, movement.getQuantity());
        assertEquals(seller, movement.getPerformedBy());
        assertEquals(inventory, movement.getInventory());
        assertEquals(1, repository.getSaveCount());

        /*
         * La cantidad disponible no vuelve a disminuir porque
         * ya debió descontarse durante la reserva.
         */
        assertEquals(
                10,
                inventory.getAvailableQuantity()
        );
    }

    @Test
    @DisplayName("El operador registra una salida del Marketplace")
    void logisticsOperatorShouldRecordMarketplaceExit() {
        InMemoryInventoryMovementRepository repository =
                new InMemoryInventoryMovementRepository();

        Product product = createProduct();

        Inventory inventory = createInventory(
                product,
                new Warehouse(
                        WarehouseOwnerType.MARKETPLACE
                )
        );

        RecordSaleExitService service =
                new RecordSaleExitService(repository);

        InventoryMovement movement = service.record(
                createLogisticsOperator(
                        UserStatus.ACTIVE
                ),
                createDispatchedOrder(product, 1),
                inventory,
                1
        );

        assertEquals(
                InventoryMovementType.SALE_EXIT,
                movement.getMovementType()
        );

        assertEquals(1, repository.getSaveCount());
    }

    @Test
    @DisplayName("Debe rechazar un pedido que no esté despachado")
    void shouldRejectOrderNotDispatched() {
        Product product = createProduct();

        RecordSaleExitService service =
                new RecordSaleExitService(
                        new InMemoryInventoryMovementRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.record(
                        createLogisticsOperator(
                                UserStatus.ACTIVE
                        ),
                        createPendingOrder(product, 1),
                        createInventory(
                                product,
                                new Warehouse(
                                        WarehouseOwnerType.MARKETPLACE
                                )
                        ),
                        1
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un producto ajeno al pedido")
    void shouldRejectProductOutsideOrder() {
        Product orderedProduct = createProduct();
        Product inventoryProduct = createProduct();

        RecordSaleExitService service =
                new RecordSaleExitService(
                        new InMemoryInventoryMovementRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.record(
                        createLogisticsOperator(
                                UserStatus.ACTIVE
                        ),
                        createDispatchedOrder(
                                orderedProduct,
                                1
                        ),
                        createInventory(
                                inventoryProduct,
                                new Warehouse(
                                        WarehouseOwnerType.MARKETPLACE
                                )
                        ),
                        1
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar una cantidad superior a la comprada")
    void shouldRejectQuantityGreaterThanOrdered() {
        Product product = createProduct();

        RecordSaleExitService service =
                new RecordSaleExitService(
                        new InMemoryInventoryMovementRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.record(
                        createLogisticsOperator(
                                UserStatus.ACTIVE
                        ),
                        createDispatchedOrder(product, 2),
                        createInventory(
                                product,
                                new Warehouse(
                                        WarehouseOwnerType.MARKETPLACE
                                )
                        ),
                        3
                )
        );
    }

    @Test
    @DisplayName("El vendedor no registra salida de productos ajenos")
    void sellerShouldNotRecordExitForOtherProduct() {
        Warehouse warehouse =
                new Warehouse(WarehouseOwnerType.SELLER);

        Seller seller = createSeller(
                UserStatus.ACTIVE,
                warehouse
        );

        Product product = createProduct();

        RecordSaleExitService service =
                new RecordSaleExitService(
                        new InMemoryInventoryMovementRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.record(
                        seller,
                        createDispatchedOrder(product, 1),
                        createInventory(product, warehouse),
                        1
                )
        );
    }

    @Test
    @DisplayName("El operador no administra bodegas de vendedores")
    void logisticsOperatorShouldNotRecordSellerExit() {
        Product product = createProduct();

        RecordSaleExitService service =
                new RecordSaleExitService(
                        new InMemoryInventoryMovementRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.record(
                        createLogisticsOperator(
                                UserStatus.ACTIVE
                        ),
                        createDispatchedOrder(product, 1),
                        createInventory(
                                product,
                                new Warehouse(
                                        WarehouseOwnerType.SELLER
                                )
                        ),
                        1
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario inactivo")
    void shouldRejectInactiveUser() {
        Product product = createProduct();

        RecordSaleExitService service =
                new RecordSaleExitService(
                        new InMemoryInventoryMovementRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.record(
                        createLogisticsOperator(
                                UserStatus.INACTIVE
                        ),
                        createDispatchedOrder(product, 1),
                        createInventory(
                                product,
                                new Warehouse(
                                        WarehouseOwnerType.MARKETPLACE
                                )
                        ),
                        1
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar una cantidad igual a cero")
    void shouldRejectZeroQuantity() {
        Product product = createProduct();

        RecordSaleExitService service =
                new RecordSaleExitService(
                        new InMemoryInventoryMovementRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.record(
                        createLogisticsOperator(
                                UserStatus.ACTIVE
                        ),
                        createDispatchedOrder(product, 1),
                        createInventory(
                                product,
                                new Warehouse(
                                        WarehouseOwnerType.MARKETPLACE
                                )
                        ),
                        0
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio nulo")
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RecordSaleExitService(null)
        );
    }

    /**
     * Crea un pedido en estado despachado.
     */
    private Order createDispatchedOrder(
            Product product,
            int quantity
    ) {
        Order order = createPendingOrder(
                product,
                quantity
        );

        order.markAsPaid();
        order.dispatch();

        return order;
    }

    /**
     * Crea un pedido pendiente de pago.
     */
    private Order createPendingOrder(
            Product product,
            int quantity
    ) {
        Cart cart = new Cart(createBuyer());
        cart.addProduct(product, quantity);

        return new Order(cart);
    }

    private Inventory createInventory(
            Product product,
            Warehouse warehouse
    ) {
        return new Inventory(
                product,
                warehouse,
                10,
                InventoryCondition.AVAILABLE
        );
    }

    private Product createProduct() {
        return new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );
    }

    private Buyer createBuyer() {
        return new Buyer(
                "1001",
                "Buyer",
                "buyer@email.com",
                UserStatus.ACTIVE,
                "Main Street 10",
                BuyerStatus.ACTIVE
        );
    }

    private Seller createSeller(
            UserStatus status,
            Warehouse warehouse
    ) {
        return new Seller(
                "2001",
                "Seller",
                "seller@email.com",
                status,
                warehouse
        );
    }

    private LogisticsOperator createLogisticsOperator(
            UserStatus status
    ) {
        return new LogisticsOperator(
                "3001",
                "Logistics Operator",
                "operator@email.com",
                status
        );
    }
}