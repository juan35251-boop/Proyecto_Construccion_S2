package application.services;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.LogisticsOperator;
import application.domain.models.Seller;
import application.domain.models.Supervisor;
import application.domain.models.Warehouse;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.services.support.InMemoryWarehouseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del servicio de consulta de bodegas.
 */
class WarehouseQueryServiceTest {

    @Test
    @DisplayName("El administrador puede consultar todas las bodegas")
    void administratorShouldFindAllWarehouses() {
        InMemoryWarehouseRepository repository =
                createRepositoryWithWarehouses();

        WarehouseQueryService service =
                new WarehouseQueryService(repository);

        List<Warehouse> result =
                service.findAccessibleWarehouses(
                        createAdministrator()
                );

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("El supervisor puede consultar todas las bodegas")
    void supervisorShouldFindAllWarehouses() {
        WarehouseQueryService service =
                new WarehouseQueryService(
                        createRepositoryWithWarehouses()
                );

        List<Warehouse> result =
                service.findAccessibleWarehouses(
                        createSupervisor()
                );

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("El vendedor consulta únicamente sus bodegas")
    void sellerShouldFindOwnWarehouses() {
        Seller seller = createSeller();

        Warehouse secondWarehouse =
                new Warehouse(WarehouseOwnerType.SELLER);

        seller.associateWarehouse(secondWarehouse);

        WarehouseQueryService service =
                new WarehouseQueryService(
                        createRepositoryWithWarehouses()
                );

        List<Warehouse> result =
                service.findAccessibleWarehouses(seller);

        assertEquals(2, result.size());
        assertTrue(
                result.contains(secondWarehouse)
        );
    }

    @Test
    @DisplayName("El operador consulta bodegas del Marketplace")
    void logisticsOperatorShouldFindMarketplaceWarehouses() {
        WarehouseQueryService service =
                new WarehouseQueryService(
                        createRepositoryWithWarehouses()
                );

        List<Warehouse> result =
                service.findAccessibleWarehouses(
                        createLogisticsOperator(
                                UserStatus.ACTIVE
                        )
                );

        assertEquals(1, result.size());
        assertTrue(
                result.get(0)
                        .isMarketplaceWarehouse()
        );
    }

    @Test
    @DisplayName("El comprador no puede consultar bodegas")
    void buyerShouldNotQueryWarehouses() {
        WarehouseQueryService service =
                new WarehouseQueryService(
                        createRepositoryWithWarehouses()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.findAccessibleWarehouses(
                        createBuyer()
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario inactivo")
    void shouldRejectInactiveUser() {
        WarehouseQueryService service =
                new WarehouseQueryService(
                        createRepositoryWithWarehouses()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.findAccessibleWarehouses(
                        createLogisticsOperator(
                                UserStatus.INACTIVE
                        )
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario nulo")
    void shouldRejectNullUser() {
        WarehouseQueryService service =
                new WarehouseQueryService(
                        createRepositoryWithWarehouses()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.findAccessibleWarehouses(null)
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio nulo")
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new WarehouseQueryService(null)
        );
    }

    private InMemoryWarehouseRepository
            createRepositoryWithWarehouses() {
        InMemoryWarehouseRepository repository =
                new InMemoryWarehouseRepository();

        repository.save(
                new Warehouse(
                        WarehouseOwnerType.MARKETPLACE
                )
        );

        repository.save(
                new Warehouse(
                        WarehouseOwnerType.SELLER
                )
        );

        return repository;
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

    private Seller createSeller() {
        return new Seller(
                "2001",
                "Seller",
                "seller@email.com",
                UserStatus.ACTIVE,
                new Warehouse(
                        WarehouseOwnerType.SELLER
                )
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