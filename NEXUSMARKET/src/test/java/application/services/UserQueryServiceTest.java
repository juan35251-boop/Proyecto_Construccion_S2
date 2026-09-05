package application.services;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.Supervisor;
import application.domain.models.User;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.UserStatus;
import application.services.support.InMemoryUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas del servicio de consulta general de usuarios.
 */
class UserQueryServiceTest {

    @Test
    @DisplayName("El administrador puede consultar otro usuario")
    void administratorShouldFindAnotherUser() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        Buyer buyer = createBuyer(
                "1001",
                UserStatus.ACTIVE
        );

        repository.save(buyer);

        UserQueryService service =
                new UserQueryService(repository);

        User result = service.findByIdentification(
                createAdministrator(UserStatus.ACTIVE),
                "1001"
        );

        assertEquals(buyer, result);
    }

    @Test
    @DisplayName("El supervisor puede consultar todos los usuarios")
    void supervisorShouldFindAllUsers() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        repository.save(
                createBuyer("1001", UserStatus.ACTIVE)
        );

        repository.save(
                createAdministrator(UserStatus.ACTIVE)
        );

        UserQueryService service =
                new UserQueryService(repository);

        List<User> users = service.findAll(
                createSupervisor(UserStatus.ACTIVE)
        );

        assertEquals(2, users.size());
    }

    @Test
    @DisplayName("Un usuario puede consultar su propia información")
    void userShouldFindOwnInformation() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        Buyer buyer = createBuyer(
                "1001",
                UserStatus.ACTIVE
        );

        repository.save(buyer);

        UserQueryService service =
                new UserQueryService(repository);

        User result = service.findByIdentification(
                buyer,
                "1001"
        );

        assertEquals(buyer, result);
    }

    @Test
    @DisplayName("Un comprador no puede consultar otro usuario")
    void buyerShouldNotFindAnotherUser() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        Buyer requestingBuyer = createBuyer(
                "1001",
                UserStatus.ACTIVE
        );

        Buyer otherBuyer = createBuyer(
                "1002",
                UserStatus.ACTIVE
        );

        repository.save(otherBuyer);

        UserQueryService service =
                new UserQueryService(repository);

        assertThrows(
                IllegalStateException.class,
                () -> service.findByIdentification(
                        requestingBuyer,
                        "1002"
                )
        );
    }

    @Test
    @DisplayName("Un comprador no puede consultar todos los usuarios")
    void buyerShouldNotFindAllUsers() {
        UserQueryService service =
                new UserQueryService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.findAll(
                        createBuyer(
                                "1001",
                                UserStatus.ACTIVE
                        )
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un solicitante inactivo")
    void shouldRejectInactiveRequester() {
        UserQueryService service =
                new UserQueryService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.findAll(
                        createSupervisor(
                                UserStatus.INACTIVE
                        )
                )
        );
    }

    @Test
    @DisplayName("Debe informar cuando el usuario no existe")
    void shouldRejectUnknownUser() {
        UserQueryService service =
                new UserQueryService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.findByIdentification(
                        createAdministrator(
                                UserStatus.ACTIVE
                        ),
                        "9999"
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar una identificación vacía")
    void shouldRejectBlankIdentification() {
        UserQueryService service =
                new UserQueryService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.findByIdentification(
                        createAdministrator(
                                UserStatus.ACTIVE
                        ),
                        " "
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio nulo")
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new UserQueryService(null)
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

    private Administrator createAdministrator(
            UserStatus status
    ) {
        return new Administrator(
                "9001",
                "Administrator",
                "admin@email.com",
                status
        );
    }

    private Supervisor createSupervisor(
            UserStatus status
    ) {
        return new Supervisor(
                "8001",
                "Supervisor",
                "supervisor@email.com",
                status
        );
    }
}