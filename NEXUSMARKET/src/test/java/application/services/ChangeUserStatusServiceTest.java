package application.services;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.UserStatus;
import application.services.support.InMemoryUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del servicio que permite al administrador
 * modificar el estado operativo de los usuarios.
 */
class ChangeUserStatusServiceTest {

    @Test
    @DisplayName("Debe bloquear un usuario")
    void shouldBlockUser() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        Administrator administrator =
                createAdministrator(UserStatus.ACTIVE);

        Buyer buyer = createBuyer(
                "1001",
                UserStatus.ACTIVE
        );

        ChangeUserStatusService service =
                new ChangeUserStatusService(repository);

        UserStatus result = service.changeStatus(
                administrator,
                buyer,
                UserStatus.BLOCKED
        ).getStatus();

        assertEquals(UserStatus.BLOCKED, result);
        assertTrue(buyer.isBlocked());
        assertTrue(repository.contains(buyer));
        assertEquals(1, repository.getSaveCount());
    }

    @Test
    @DisplayName("Debe desactivar un usuario")
    void shouldDeactivateUser() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        ChangeUserStatusService service =
                new ChangeUserStatusService(repository);

        Buyer buyer = createBuyer(
                "1001",
                UserStatus.ACTIVE
        );

        service.changeStatus(
                createAdministrator(UserStatus.ACTIVE),
                buyer,
                UserStatus.INACTIVE
        );

        assertEquals(
                UserStatus.INACTIVE,
                buyer.getStatus()
        );
    }

    @Test
    @DisplayName("Debe rechazar un administrador nulo")
    void shouldRejectNullAdministrator() {
        ChangeUserStatusService service =
                new ChangeUserStatusService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.changeStatus(
                        null,
                        createBuyer(
                                "1001",
                                UserStatus.ACTIVE
                        ),
                        UserStatus.BLOCKED
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un administrador inactivo")
    void shouldRejectInactiveAdministrator() {
        ChangeUserStatusService service =
                new ChangeUserStatusService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.changeStatus(
                        createAdministrator(
                                UserStatus.INACTIVE
                        ),
                        createBuyer(
                                "1001",
                                UserStatus.ACTIVE
                        ),
                        UserStatus.BLOCKED
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario objetivo nulo")
    void shouldRejectNullTargetUser() {
        ChangeUserStatusService service =
                new ChangeUserStatusService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.changeStatus(
                        createAdministrator(
                                UserStatus.ACTIVE
                        ),
                        null,
                        UserStatus.BLOCKED
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un estado nulo")
    void shouldRejectNullStatus() {
        ChangeUserStatusService service =
                new ChangeUserStatusService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.changeStatus(
                        createAdministrator(
                                UserStatus.ACTIVE
                        ),
                        createBuyer(
                                "1001",
                                UserStatus.ACTIVE
                        ),
                        null
                )
        );
    }

    @Test
    @DisplayName("Debe impedir que el administrador cambie su propio estado")
    void shouldRejectAdministratorChangingOwnStatus() {
        Administrator administrator =
                createAdministrator(UserStatus.ACTIVE);

        ChangeUserStatusService service =
                new ChangeUserStatusService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.changeStatus(
                        administrator,
                        administrator,
                        UserStatus.BLOCKED
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio nulo")
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ChangeUserStatusService(null)
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
}