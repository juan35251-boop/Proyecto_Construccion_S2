package application.services;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.Inventory;
import application.domain.models.LogisticsOperator;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.Supervisor;
import application.domain.models.Warehouse;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.InventoryCondition;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.services.support.InMemoryInventoryQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del servicio encargado de consultar inventarios.
 */
class InventoryQueryServiceTest {

    @Test
    @DisplayName("El administrador puede consultar todo el inventario")
    void administratorShouldFindAllInventory() {
        InventoryQueryService service =
                new InventoryQueryService(
                        createRepository()
                );

        List<Inventory> inventories =
                service.findAccessibleInventory(
                        createAdministrator()
                );

        assertEquals(3, inventories.size());
    }

    @Test
    @DisplayName("El supervisor puede consultar todo el inventario")
    void supervisorShouldFindAllInventory() {
        InventoryQueryService service =
                new InventoryQueryService(
                        createRepository()
                );

        List<Inventory> inventories =
                service.findAccessibleInventory(
                        createSupervisor()
                );

        assertEquals(3, inventories.size());
    }

    @Test
    @DisplayName("El vendedor consulta inventario de sus productos y bodegas")
    void sellerShouldFindOwnInventory() {
        InMemoryInventoryQueryRepository repository =
                new InMemoryInventoryQueryRepository();

        Warehouse sellerWarehouse =
                new Warehouse(WarehouseOwnerType.SELLER);

        Warehouse otherWarehouse =
                new Warehouse(WarehouseOwnerType.SELLER);

        Product ownProduct = createProduct();
        Product otherProduct = createProduct();

        Seller seller = createSeller(
                UserStatus.ACTIVE,
                sellerWarehouse
        );

        seller.registerProduct(ownProduct);

        Inventory validInventory = createInventory(
                ownProduct,
                sellerWarehouse
        );

        /*
         * Tiene el producto correcto, pero está en una
         * bodega que el vendedor no administra.
         */
        Inventory inventoryInOtherWarehouse =
                createInventory(
                        ownProduct,
                        otherWarehouse
                );

        /*
         * Está en su bodega, pero pertenece a un producto
         * que el vendedor no administra.
         */
        Inventory inventoryOfOtherProduct =
                createInventory(
                        otherProduct,
                        sellerWarehouse
                );

        repository.add(validInventory);
        repository.add(inventoryInOtherWarehouse);
        repository.add(inventoryOfOtherProduct);

        InventoryQueryService service =
                new InventoryQueryService(repository);

        List<Inventory> result =
                service.findAccessibleInventory(seller);

        assertEquals(1, result.size());
        assertTrue(result.contains(validInventory));
    }

    @Test
    @DisplayName("El operador consulta inventario del Marketplace")
    void logisticsOperatorShouldFindMarketplaceInventory() {
        InMemoryInventoryQueryRepository repository =
                new InMemoryInventoryQueryRepository();

        Inventory marketplaceInventory =
                createInventory(
                        createProduct(),
                        new Warehouse(
                                WarehouseOwnerType.MARKETPLACE
                        )
                );

        Inventory sellerInventory =
                createInventory(
                        createProduct(),
                        new Warehouse(
                                WarehouseOwnerType.SELLER
                        )
                );

        repository.add(marketplaceInventory);
        repository.add(sellerInventory);

        InventoryQueryService service =
                new InventoryQueryService(repository);

        List<Inventory> result =
                service.findAccessibleInventory(
                        createLogisticsOperator(
                                UserStatus.ACTIVE
                        )
                );

        assertEquals(1, result.size());
        assertTrue(
                result.contains(marketplaceInventory)
        );
    }

    @Test
    @DisplayName("El comprador no puede consultar inventario")
    void buyerShouldNotQueryInventory() {
        InventoryQueryService service =
                new InventoryQueryService(
                        createRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.findAccessibleInventory(
                        createBuyer()
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario inactivo")
    void shouldRejectInactiveUser() {
        InventoryQueryService service =
                new InventoryQueryService(
                        createRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.findAccessibleInventory(
                        createLogisticsOperator(
                                UserStatus.INACTIVE
                        )
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario nulo")
    void shouldRejectNullUser() {
        InventoryQueryService service =
                new InventoryQueryService(
                        createRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.findAccessibleInventory(null)
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio nulo")
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new InventoryQueryService(null)
        );
    }

    /**
     * Crea inventarios pertenecientes a distintos tipos de bodega.
     */
    private InMemoryInventoryQueryRepository
            createRepository() {
        InMemoryInventoryQueryRepository repository =
                new InMemoryInventoryQueryRepository();

        repository.add(
                createInventory(
                        createProduct(),
                        new Warehouse(
                                WarehouseOwnerType.MARKETPLACE
                        )
                )
        );

        repository.add(
                createInventory(
                        createProduct(),
                        new Warehouse(
                                WarehouseOwnerType.SELLER
                        )
                )
        );

        repository.add(
                createInventory(
                        createProduct(),
                        new Warehouse(
                                WarehouseOwnerType.SELLER
                        )
                )
        );

        return repository;
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

    private Administrator createAdministrator() {
        return new Administrator(
                "9001",
                "Administrator",
                "admin@email.com",
                UserStatus.ACTIVE
        );
    }

    private Supervisor createSupervisor() {
        return new Supervisor(
                "8001",
                "Supervisor",
                "supervisor@email.com",
                UserStatus.ACTIVE
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
}