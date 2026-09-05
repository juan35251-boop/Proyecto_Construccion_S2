package application.services;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.Seller;
import application.domain.models.Supervisor;
import application.domain.models.Warehouse;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.services.support.InMemoryUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas de las consultas específicas de compradores.
 */
class BuyerQueryServiceTest {

    @Test
    @DisplayName("El comprador puede consultar su propia información")
    void buyerShouldFindOwnInformation() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        Buyer buyer = createBuyer(
                "1001",
                UserStatus.ACTIVE
        );

        repository.save(buyer);

        BuyerQueryService service =
                new BuyerQueryService(repository);

        Buyer result = service.findByIdentification(
                buyer,
                "1001"
        );

        assertEquals(buyer, result);
    }

    @Test
    @DisplayName("El administrador puede consultar cualquier comprador")
    void administratorShouldFindBuyer() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        Buyer buyer = createBuyer(
                "1001",
                UserStatus.ACTIVE
        );

        repository.save(buyer);

        BuyerQueryService service =
                new BuyerQueryService(repository);

        Buyer result = service.findByIdentification(
                createAdministrator(),
                "1001"
        );

        assertEquals(buyer, result);
    }

    @Test
    @DisplayName("El supervisor puede consultar todos los compradores")
    void supervisorShouldFindAllBuyers() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        repository.save(
                createBuyer("1001", UserStatus.ACTIVE)
        );

        repository.save(
                createBuyer("1002", UserStatus.ACTIVE)
        );

        repository.save(createSeller());

        BuyerQueryService service =
                new BuyerQueryService(repository);

        List<Buyer> buyers = service.findAll(
                createSupervisor()
        );

        assertEquals(2, buyers.size());
    }

    @Test
    @DisplayName("La consulta general debe excluir otros roles")
    void findAllShouldOnlyReturnBuyers() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        repository.save(
                createBuyer("1001", UserStatus.ACTIVE)
        );

        repository.save(createAdministrator());
        repository.save(createSeller());

        BuyerQueryService service =
                new BuyerQueryService(repository);

        List<Buyer> buyers = service.findAll(
                createAdministrator()
        );

        assertEquals(1, buyers.size());
    }

    @Test
    @DisplayName("Un comprador no puede consultar otro comprador")
    void buyerShouldNotFindAnotherBuyer() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        repository.save(
                createBuyer("1002", UserStatus.ACTIVE)
        );

        BuyerQueryService service =
                new BuyerQueryService(repository);

        assertThrows(
                IllegalStateException.class,
                () -> service.findByIdentification(
                        createBuyer(
                                "1001",
                                UserStatus.ACTIVE
                        ),
                        "1002"
                )
        );
    }

    @Test
    @DisplayName("Un vendedor no puede consultar compradores")
    void sellerShouldNotFindAllBuyers() {
        BuyerQueryService service =
                new BuyerQueryService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.findAll(createSeller())
        );
    }

    @Test
    @DisplayName("Debe rechazar si el usuario encontrado no es comprador")
    void shouldRejectNonBuyerUser() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        repository.save(createSeller());

        BuyerQueryService service =
                new BuyerQueryService(repository);

        assertThrows(
                IllegalStateException.class,
                () -> service.findByIdentification(
                        createAdministrator(),
                        "2001"
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un solicitante inactivo")
    void shouldRejectInactiveRequester() {
        BuyerQueryService service =
                new BuyerQueryService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.findAll(
                        createBuyer(
                                "1001",
                                UserStatus.INACTIVE
                        )
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio nulo")
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BuyerQueryService(null)
        );
    }

    private Buyer createBuyer(
            String identification,
            UserStatus status
    ) {
        return new Buyer(
                identification,
                "Buyer",
                identification + "@email.com",
                status,
                "Main Street 10",
                BuyerStatus.ACTIVE
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
}