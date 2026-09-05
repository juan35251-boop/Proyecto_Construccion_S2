package application.services;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.UserStatus;
import application.services.support.InMemoryUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del servicio que permite a un usuario
 * actualizar su propio nombre y correo electrónico.
 */
class UpdateOwnProfileServiceTest {

    @Test
    @DisplayName("Debe actualizar el perfil de un comprador activo")
    void shouldUpdateActiveBuyerProfile() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        Buyer buyer = createBuyer(UserStatus.ACTIVE);

        UpdateOwnProfileService service =
                new UpdateOwnProfileService(repository);

        User result = service.update(
                buyer,
                "Updated Buyer",
                "updated@email.com"
        );

        assertEquals("Updated Buyer", result.getFullName());
        assertEquals("updated@email.com", result.getEmail());
        assertEquals(buyer, result);
        assertTrue(repository.contains(buyer));
        assertEquals(1, repository.getSaveCount());
    }

    @Test
    @DisplayName("Un administrador activo puede actualizar su perfil")
    void administratorShouldUpdateOwnProfile() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        Administrator administrator =
                createAdministrator(UserStatus.ACTIVE);

        UpdateOwnProfileService service =
                new UpdateOwnProfileService(repository);

        service.update(
                administrator,
                "Updated Administrator",
                "updated-admin@email.com"
        );

        assertEquals(
                "Updated Administrator",
                administrator.getFullName()
        );

        assertEquals(
                "updated-admin@email.com",
                administrator.getEmail()
        );
    }

    @Test
    @DisplayName("Debe permitir conservar el mismo correo")
    void shouldAllowSameEmail() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        Buyer buyer = createBuyer(UserStatus.ACTIVE);
        repository.save(buyer);

        UpdateOwnProfileService service =
                new UpdateOwnProfileService(repository);

        service.update(
                buyer,
                "New Buyer Name",
                "buyer@email.com"
        );

        assertEquals(
                "New Buyer Name",
                buyer.getFullName()
        );

        assertEquals(
                "buyer@email.com",
                buyer.getEmail()
        );
    }

    @Test
    @DisplayName("Debe rechazar un correo utilizado por otro usuario")
    void shouldRejectDuplicatedEmail() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        Buyer buyer = createBuyer(UserStatus.ACTIVE);

        Administrator administrator =
                createAdministrator(UserStatus.ACTIVE);

        repository.save(administrator);

        UpdateOwnProfileService service =
                new UpdateOwnProfileService(repository);

        assertThrows(
                IllegalStateException.class,
                () -> service.update(
                        buyer,
                        "Changed Name",
                        "admin@email.com"
                )
        );

        /*
         * Como el correo era duplicado, ningún dato del
         * usuario debe haberse modificado.
         */
        assertEquals("Buyer", buyer.getFullName());
        assertEquals("buyer@email.com", buyer.getEmail());
    }

    @Test
    @DisplayName("Debe rechazar un usuario inactivo")
    void shouldRejectInactiveUser() {
        UpdateOwnProfileService service =
                new UpdateOwnProfileService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.update(
                        createBuyer(UserStatus.INACTIVE),
                        "Updated Buyer",
                        "updated@email.com"
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario bloqueado")
    void shouldRejectBlockedUser() {
        UpdateOwnProfileService service =
                new UpdateOwnProfileService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.update(
                        createBuyer(UserStatus.BLOCKED),
                        "Updated Buyer",
                        "updated@email.com"
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario nulo")
    void shouldRejectNullUser() {
        UpdateOwnProfileService service =
                new UpdateOwnProfileService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.update(
                        null,
                        "Updated User",
                        "updated@email.com"
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un nombre vacío")
    void shouldRejectBlankFullName() {
        UpdateOwnProfileService service =
                new UpdateOwnProfileService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.update(
                        createBuyer(UserStatus.ACTIVE),
                        " ",
                        "updated@email.com"
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un correo vacío")
    void shouldRejectBlankEmail() {
        UpdateOwnProfileService service =
                new UpdateOwnProfileService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.update(
                        createBuyer(UserStatus.ACTIVE),
                        "Updated Buyer",
                        " "
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio nulo")
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new UpdateOwnProfileService(null)
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
}