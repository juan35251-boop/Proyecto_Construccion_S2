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
 * Pruebas de las consultas específicas de vendedores.
 */
class SellerQueryServiceTest {

    @Test
    @DisplayName("El vendedor puede consultar su propia información")
    void sellerShouldFindOwnInformation() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        Seller seller = createSeller(
                "2001",
                UserStatus.ACTIVE
        );

        repository.save(seller);

        SellerQueryService service =
                new SellerQueryService(repository);

        Seller result = service.findByIdentification(
                seller,
                "2001"
        );

        assertEquals(seller, result);
    }

    @Test
    @DisplayName("El administrador puede consultar cualquier vendedor")
    void administratorShouldFindSeller() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        Seller seller = createSeller(
                "2001",
                UserStatus.ACTIVE
        );

        repository.save(seller);

        SellerQueryService service =
                new SellerQueryService(repository);

        Seller result = service.findByIdentification(
                createAdministrator(),
                "2001"
        );

        assertEquals(seller, result);
    }

    @Test
    @DisplayName("El supervisor puede consultar todos los vendedores")
    void supervisorShouldFindAllSellers() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        repository.save(
                createSeller("2001", UserStatus.ACTIVE)
        );

        repository.save(
                createSeller("2002", UserStatus.ACTIVE)
        );

        repository.save(createBuyer());

        SellerQueryService service =
                new SellerQueryService(repository);

        List<Seller> sellers = service.findAll(
                createSupervisor()
        );

        assertEquals(2, sellers.size());
    }

    @Test
    @DisplayName("La consulta general debe excluir otros roles")
    void findAllShouldOnlyReturnSellers() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        repository.save(
                createSeller("2001", UserStatus.ACTIVE)
        );

        repository.save(createBuyer());
        repository.save(createAdministrator());

        SellerQueryService service =
                new SellerQueryService(repository);

        List<Seller> sellers = service.findAll(
                createAdministrator()
        );

        assertEquals(1, sellers.size());
    }

    @Test
    @DisplayName("Un vendedor no puede consultar otro vendedor")
    void sellerShouldNotFindAnotherSeller() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        repository.save(
                createSeller("2002", UserStatus.ACTIVE)
        );

        SellerQueryService service =
                new SellerQueryService(repository);

        assertThrows(
                IllegalStateException.class,
                () -> service.findByIdentification(
                        createSeller(
                                "2001",
                                UserStatus.ACTIVE
                        ),
                        "2002"
                )
        );
    }

    @Test
    @DisplayName("Un comprador no puede consultar vendedores")
    void buyerShouldNotFindAllSellers() {
        SellerQueryService service =
                new SellerQueryService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.findAll(createBuyer())
        );
    }

    @Test
    @DisplayName("Debe rechazar si el usuario no es vendedor")
    void shouldRejectNonSellerUser() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        repository.save(createBuyer());

        SellerQueryService service =
                new SellerQueryService(repository);

        assertThrows(
                IllegalStateException.class,
                () -> service.findByIdentification(
                        createAdministrator(),
                        "1001"
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un vendedor solicitante inactivo")
    void shouldRejectInactiveRequester() {
        SellerQueryService service =
                new SellerQueryService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.findAll(
                        createSeller(
                                "2001",
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
                () -> new SellerQueryService(null)
        );
    }

    private Seller createSeller(
            String identification,
            UserStatus status
    ) {
        return new Seller(
                identification,
                "Seller",
                identification + "@email.com",
                status,
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
}