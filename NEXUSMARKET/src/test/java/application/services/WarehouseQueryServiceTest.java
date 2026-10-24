package application.services;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.LogisticsOperator;
import application.domain.models.Seller;
import application.domain.models.Supervisor;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.services.support.InMemoryUserRepository;
import application.services.support.InMemoryWarehouseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del servicio encargado
 * de consultar bodegas.
 */
class WarehouseQueryServiceTest {

    @Test
    @DisplayName("El administrador puede consultar todas las bodegas")
    void administratorShouldFindAllWarehouses() {
        Administrator administrator =
                createAdministrator();

        WarehouseQueryService service =
                createService(administrator);

        List<Warehouse> result =
                service.findAccessibleWarehouses(
                        administrator.getIdentification()
                );

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("El supervisor puede consultar todas las bodegas")
    void supervisorShouldFindAllWarehouses() {
        Supervisor supervisor = createSupervisor();

        WarehouseQueryService service =
                createService(supervisor);

        List<Warehouse> result =
                service.findAccessibleWarehouses(
                        supervisor.getIdentification()
                );

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("El vendedor consulta únicamente sus bodegas")
    void sellerShouldFindOwnWarehouses() {
        Seller seller = createSeller();

        Warehouse secondWarehouse =
                new Warehouse(
                        WarehouseOwnerType.SELLER
                );

        seller.associateWarehouse(secondWarehouse);

        WarehouseQueryService service =
                createService(seller);

        List<Warehouse> result =
                service.findAccessibleWarehouses(
                        seller.getIdentification()
                );

        assertEquals(2, result.size());
        assertTrue(result.contains(secondWarehouse));
    }

    @Test
    @DisplayName("El operador consulta bodegas del Marketplace")
    void logisticsOperatorShouldFindMarketplaceWarehouses() {
        LogisticsOperator operator =
                createLogisticsOperator(
                        UserStatus.ACTIVE
                );

        WarehouseQueryService service =
                createService(operator);

        List<Warehouse> result =
                service.findAccessibleWarehouses(
                        operator.getIdentification()
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
        Buyer buyer = createBuyer();

        WarehouseQueryService service =
                createService(buyer);

        assertThrows(
                IllegalStateException.class,
                () -> service.findAccessibleWarehouses(
                        buyer.getIdentification()
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario inactivo")
    void shouldRejectInactiveUser() {
        LogisticsOperator operator =
                createLogisticsOperator(
                        UserStatus.INACTIVE
                );

        WarehouseQueryService service =
                createService(operator);

        assertThrows(
                IllegalStateException.class,
                () -> service.findAccessibleWarehouses(
                        operator.getIdentification()
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar una identificación nula")
    void shouldRejectNullIdentification() {
        WarehouseQueryService service =
                createService(createAdministrator());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.findAccessibleWarehouses(null)
        );
    }

    @Test
    @DisplayName("Debe rechazar una identificación vacía")
    void shouldRejectBlankIdentification() {
        WarehouseQueryService service =
                createService(createAdministrator());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.findAccessibleWarehouses(" ")
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario inexistente")
    void shouldRejectMissingUser() {
        WarehouseQueryService service =
                new WarehouseQueryService(
                        new InMemoryUserRepository(),
                        createRepositoryWithWarehouses()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.findAccessibleWarehouses(
                        "9999"
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio de usuarios nulo")
    void shouldRejectNullUserRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new WarehouseQueryService(
                        null,
                        createRepositoryWithWarehouses()
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio de bodegas nulo")
    void shouldRejectNullWarehouseRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new WarehouseQueryService(
                        new InMemoryUserRepository(),
                        null
                )
        );
    }

    /**
     * Crea el servicio y registra al usuario solicitante.
     */
    private WarehouseQueryService createService(
            User requester
    ) {
        InMemoryUserRepository userRepository =
                new InMemoryUserRepository();

        userRepository.save(requester);

        return new WarehouseQueryService(
                userRepository,
                createRepositoryWithWarehouses()
        );
    }

    /**
     * Crea un repositorio con una bodega del Marketplace
     * y una bodega de vendedor.
     */
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