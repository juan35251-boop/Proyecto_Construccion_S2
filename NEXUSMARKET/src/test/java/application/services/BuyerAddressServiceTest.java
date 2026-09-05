package application.services;

import application.domain.models.Buyer;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.UserStatus;
import application.services.support.InMemoryUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la administración de direcciones del comprador.
 */
class BuyerAddressServiceTest {

    @Test
    @DisplayName("Debe cambiar la dirección principal")
    void shouldChangePrimaryAddress() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        Buyer buyer = createBuyer(UserStatus.ACTIVE);

        BuyerAddressService service =
                new BuyerAddressService(repository);

        service.changePrimaryAddress(
                buyer,
                "Second Street 20"
        );

        assertEquals(
                "Second Street 20",
                buyer.getPrimaryAddress()
        );

        assertTrue(repository.contains(buyer));
        assertEquals(1, repository.getSaveCount());
    }

    @Test
    @DisplayName("Debe agregar una dirección adicional")
    void shouldAddAdditionalAddress() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        Buyer buyer = createBuyer(UserStatus.ACTIVE);

        BuyerAddressService service =
                new BuyerAddressService(repository);

        service.addAdditionalAddress(
                buyer,
                "Third Street 30"
        );

        assertTrue(
                buyer.getAdditionalAddresses()
                        .contains("Third Street 30")
        );

        assertEquals(1, repository.getSaveCount());
    }

    @Test
    @DisplayName("Debe eliminar una dirección existente")
    void shouldRemoveAdditionalAddress() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        Buyer buyer = createBuyer(UserStatus.ACTIVE);
        buyer.addAdditionalAddress("Third Street 30");

        BuyerAddressService service =
                new BuyerAddressService(repository);

        boolean removed = service.removeAdditionalAddress(
                buyer,
                "Third Street 30"
        );

        assertTrue(removed);
        assertFalse(
                buyer.getAdditionalAddresses()
                        .contains("Third Street 30")
        );
        assertEquals(1, repository.getSaveCount());
    }

    @Test
    @DisplayName("No debe guardar si la dirección no existe")
    void shouldNotSaveWhenAddressDoesNotExist() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        BuyerAddressService service =
                new BuyerAddressService(repository);

        boolean removed = service.removeAdditionalAddress(
                createBuyer(UserStatus.ACTIVE),
                "Unknown Street"
        );

        assertFalse(removed);
        assertEquals(0, repository.getSaveCount());
    }

    @Test
    @DisplayName("Debe rechazar un comprador nulo")
    void shouldRejectNullBuyer() {
        BuyerAddressService service =
                new BuyerAddressService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.changePrimaryAddress(
                        null,
                        "Second Street 20"
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un comprador inactivo")
    void shouldRejectInactiveBuyer() {
        BuyerAddressService service =
                new BuyerAddressService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.addAdditionalAddress(
                        createBuyer(UserStatus.INACTIVE),
                        "Second Street 20"
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar una dirección vacía")
    void shouldRejectBlankAddress() {
        BuyerAddressService service =
                new BuyerAddressService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.changePrimaryAddress(
                        createBuyer(UserStatus.ACTIVE),
                        " "
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio nulo")
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BuyerAddressService(null)
        );
    }

    private Buyer createBuyer(UserStatus status) {
        return new Buyer(
                "1001",
                "Buyer",
                "buyer@email.com",
                status,
                "Main Street 10",
                BuyerStatus.ACTIVE
        );
    }
}