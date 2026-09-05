package application.services;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.LogisticsOperator;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.Supervisor;
import application.domain.models.Warehouse;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.InventoryCondition;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.services.support.InMemoryInventoryMovementQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del servicio encargado de consultar
 * movimientos de inventario.
 */
class InventoryMovementQueryServiceTest {

    @Test
    @DisplayName("El administrador consulta todos los movimientos")
    void administratorShouldFindAllMovements() {
        InventoryMovementQueryService service =
                new InventoryMovementQueryService(
                        createRepository()
                );

        List<InventoryMovement> result =
                service.findAccessibleMovements(
                        createAdministrator()
                );

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("El supervisor consulta todos los movimientos")
    void supervisorShouldFindAllMovements() {
        InventoryMovementQueryService service =
                new InventoryMovementQueryService(
                        createRepository()
                );

        List<InventoryMovement> result =
                service.findAccessibleMovements(
                        createSupervisor()
                );

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("El vendedor consulta movimientos de sus inventarios")
    void sellerShouldFindOwnMovements() {
        InMemoryInventoryMovementQueryRepository repository =
                new InMemoryInventoryMovementQueryRepository();

        Warehouse sellerWarehouse =
                new Warehouse(WarehouseOwnerType.SELLER);

        Warehouse otherWarehouse =
                new Warehouse(WarehouseOwnerType.SELLER);

        Product sellerProduct = createProduct();
        Product otherProduct = createProduct();

        Seller seller = createSeller(
                UserStatus.ACTIVE,
                sellerWarehouse
        );

        seller.registerProduct(sellerProduct);

        Inventory ownInventory = createInventory(
                sellerProduct,
                sellerWarehouse
        );

        Inventory otherInventory = createInventory(
                otherProduct,
                otherWarehouse
        );

        InventoryMovement ownMovement =
                createMovement(ownInventory);

        InventoryMovement otherMovement =
                createMovement(otherInventory);

        repository.add(ownMovement);
        repository.add(otherMovement);

        InventoryMovementQueryService service =
                new InventoryMovementQueryService(repository);

        List<InventoryMovement> result =
                service.findAccessibleMovements(seller);

        assertEquals(1, result.size());
        assertTrue(result.contains(ownMovement));
    }

    @Test
    @DisplayName("El operador consulta movimientos del Marketplace")
    void logisticsOperatorShouldFindMarketplaceMovements() {
        InMemoryInventoryMovementQueryRepository repository =
                new InMemoryInventoryMovementQueryRepository();

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

        InventoryMovement marketplaceMovement =
                createMovement(marketplaceInventory);

        repository.add(marketplaceMovement);
        repository.add(
                createMovement(sellerInventory)
        );

        InventoryMovementQueryService service =
                new InventoryMovementQueryService(repository);

        List<InventoryMovement> result =
                service.findAccessibleMovements(
                        createLogisticsOperator(
                                UserStatus.ACTIVE
                        )
                );

        assertEquals(1, result.size());
        assertTrue(
                result.contains(marketplaceMovement)
        );
    }

    @Test
    @DisplayName("El comprador no consulta movimientos")
    void buyerShouldNotQueryMovements() {
        InventoryMovementQueryService service =
                new InventoryMovementQueryService(
                        createRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.findAccessibleMovements(
                        createBuyer()
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario inactivo")
    void shouldRejectInactiveUser() {
        InventoryMovementQueryService service =
                new InventoryMovementQueryService(
                        createRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.findAccessibleMovements(
                        createLogisticsOperator(
                                UserStatus.INACTIVE
                        )
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario nulo")
    void shouldRejectNullUser() {
        InventoryMovementQueryService service =
                new InventoryMovementQueryService(
                        createRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.findAccessibleMovements(null)
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio nulo")
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new InventoryMovementQueryService(null)
        );
    }

    private InMemoryInventoryMovementQueryRepository
            createRepository() {
        InMemoryInventoryMovementQueryRepository repository =
                new InMemoryInventoryMovementQueryRepository();

        repository.add(
                createMovement(
                        createInventory(
                                createProduct(),
                                new Warehouse(
                                        WarehouseOwnerType.MARKETPLACE
                                )
                        )
                )
        );

        repository.add(
                createMovement(
                        createInventory(
                                createProduct(),
                                new Warehouse(
                                        WarehouseOwnerType.SELLER
                                )
                        )
                )
        );

        return repository;
    }

    /**
     * Crea un movimiento válido realizado por un
     * operador logístico activo.
     */
    private InventoryMovement createMovement(
            Inventory inventory
    ) {
        return new InventoryMovement(
                inventory,
                InventoryMovementType.ENTRY,
                5,
                createLogisticsOperator(UserStatus.ACTIVE)
        );
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