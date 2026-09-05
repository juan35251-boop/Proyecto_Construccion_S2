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
 * Pruebas del cambio de estado comercial de compradores.
 */
class ChangeBuyerCommercialStatusServiceTest {

    @Test
    @DisplayName("Debe suspender comercialmente al comprador")
    void shouldSuspendBuyer() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        Buyer buyer = createBuyer();

        ChangeBuyerCommercialStatusService service =
                new ChangeBuyerCommercialStatusService(
                        repository
                );

        service.changeStatus(
                createAdministrator(UserStatus.ACTIVE),
                buyer,
                BuyerStatus.SUSPENDED
        );

        assertEquals(
                BuyerStatus.SUSPENDED,
                buyer.getCommercialStatus()
        );

        assertTrue(repository.contains(buyer));
        assertEquals(1, repository.getSaveCount());
    }

    @Test
    @DisplayName("Debe reactivar comercialmente al comprador")
    void shouldActivateBuyer() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        Buyer buyer = createBuyer();
        buyer.changeCommercialStatus(
                BuyerStatus.SUSPENDED
        );

        ChangeBuyerCommercialStatusService service =
                new ChangeBuyerCommercialStatusService(
                        repository
                );

        service.changeStatus(
                createAdministrator(UserStatus.ACTIVE),
                buyer,
                BuyerStatus.ACTIVE
        );

        assertEquals(
                BuyerStatus.ACTIVE,
                buyer.getCommercialStatus()
        );
    }

    @Test
    @DisplayName("Debe rechazar un administrador nulo")
    void shouldRejectNullAdministrator() {
        ChangeBuyerCommercialStatusService service =
                new ChangeBuyerCommercialStatusService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.changeStatus(
                        null,
                        createBuyer(),
                        BuyerStatus.SUSPENDED
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un administrador inactivo")
    void shouldRejectInactiveAdministrator() {
        ChangeBuyerCommercialStatusService service =
                new ChangeBuyerCommercialStatusService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.changeStatus(
                        createAdministrator(
                                UserStatus.INACTIVE
                        ),
                        createBuyer(),
                        BuyerStatus.SUSPENDED
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un comprador nulo")
    void shouldRejectNullBuyer() {
        ChangeBuyerCommercialStatusService service =
                new ChangeBuyerCommercialStatusService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.changeStatus(
                        createAdministrator(
                                UserStatus.ACTIVE
                        ),
                        null,
                        BuyerStatus.SUSPENDED
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un estado comercial nulo")
    void shouldRejectNullCommercialStatus() {
        ChangeBuyerCommercialStatusService service =
                new ChangeBuyerCommercialStatusService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.changeStatus(
                        createAdministrator(
                                UserStatus.ACTIVE
                        ),
                        createBuyer(),
                        null
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio nulo")
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ChangeBuyerCommercialStatusService(
                        null
                )
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