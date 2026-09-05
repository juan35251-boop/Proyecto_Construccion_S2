package application.services;

import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.UserStatus;
import application.ports.output.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del servicio encargado
 * de registrar compradores.
 */
class RegisterBuyerServiceTest {

    /**
     * Verifica que un comprador pueda registrarse correctamente
     * y comience con sus estados activos.
     */
    @Test
    void shouldRegisterBuyer() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        RegisterBuyerService service =
                new RegisterBuyerService(repository);

        Buyer buyer = service.register(
                "1001",
                "Test Buyer",
                "buyer@email.com",
                "Main Street 10"
        );

        assertEquals("1001", buyer.getIdentification());
        assertEquals("Test Buyer", buyer.getFullName());
        assertEquals("buyer@email.com", buyer.getEmail());
        assertEquals(
                "Main Street 10",
                buyer.getPrimaryAddress()
        );
        assertEquals(UserStatus.ACTIVE, buyer.getStatus());
        assertEquals(
                BuyerStatus.ACTIVE,
                buyer.getCommercialStatus()
        );
        assertTrue(repository.contains(buyer));
    }

    /**
     * Verifica que el repositorio sea obligatorio.
     */
    @Test
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegisterBuyerService(null)
        );
    }

    /**
     * Verifica que no pueda repetirse una identificación.
     */
    @Test
    void shouldRejectDuplicatedIdentification() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        RegisterBuyerService service =
                new RegisterBuyerService(repository);

        service.register(
                "1001",
                "First Buyer",
                "first@email.com",
                "First Street"
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.register(
                        "1001",
                        "Second Buyer",
                        "second@email.com",
                        "Second Street"
                )
        );

        assertEquals(1, repository.getUsers().size());
    }

    /**
     * Verifica que no pueda repetirse un correo electrónico.
     */
    @Test
    void shouldRejectDuplicatedEmail() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        RegisterBuyerService service =
                new RegisterBuyerService(repository);

        service.register(
                "1001",
                "First Buyer",
                "buyer@email.com",
                "First Street"
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.register(
                        "1002",
                        "Second Buyer",
                        "buyer@email.com",
                        "Second Street"
                )
        );

        assertEquals(1, repository.getUsers().size());
    }

    /**
     * Verifica que la identificación sea obligatoria.
     */
    @Test
    void shouldRejectBlankIdentification() {
        RegisterBuyerService service = createService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(
                        " ",
                        "Test Buyer",
                        "buyer@email.com",
                        "Main Street 10"
                )
        );
    }

    /**
     * Verifica que el nombre sea obligatorio.
     */
    @Test
    void shouldRejectBlankFullName() {
        RegisterBuyerService service = createService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(
                        "1001",
                        " ",
                        "buyer@email.com",
                        "Main Street 10"
                )
        );
    }

    /**
     * Verifica que el correo sea obligatorio.
     */
    @Test
    void shouldRejectBlankEmail() {
        RegisterBuyerService service = createService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(
                        "1001",
                        "Test Buyer",
                        " ",
                        "Main Street 10"
                )
        );
    }

    /**
     * Verifica que la dirección principal sea obligatoria.
     */
    @Test
    void shouldRejectBlankPrimaryAddress() {
        RegisterBuyerService service = createService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(
                        "1001",
                        "Test Buyer",
                        "buyer@email.com",
                        " "
                )
        );
    }

    /**
     * Crea el servicio con un repositorio en memoria.
     */
    private RegisterBuyerService createService() {
        return new RegisterBuyerService(
                new InMemoryUserRepository()
        );
    }

    /**
     * Repositorio de usuarios utilizado durante las pruebas.
     */
    private static class InMemoryUserRepository
            implements UserRepository {

        private final List<User> users =
                new ArrayList<>();

        @Override
        public boolean existsByIdentification(
                String identification
        ) {
            for (User user : users) {
                if (user.getIdentification().equals(
                        identification
                )) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean existsByEmail(String email) {
            for (User user : users) {
                if (user.getEmail().equals(email)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public Optional<User> findByIdentification(
                String identification
        ) {
            for (User user : users) {
                if (user.getIdentification().equals(
                        identification
                )) {
                    return Optional.of(user);
                }
            }

            return Optional.empty();
        }

        @Override
        public List<User> findAll() {
            return List.copyOf(users);
        }

        @Override
        public void save(User user) {
            users.add(user);
        }

        boolean contains(User user) {
            return users.contains(user);
        }

        List<User> getUsers() {
            return List.copyOf(users);
        }
    }
}