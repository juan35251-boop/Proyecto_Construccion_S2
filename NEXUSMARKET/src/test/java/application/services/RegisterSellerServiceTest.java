package application.services;

import application.domain.models.Administrator;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
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
 * de registrar vendedores.
 */
class RegisterSellerServiceTest {

    /**
     * Verifica que un administrador activo pueda
     * registrar un vendedor.
     */
    @Test
    void shouldRegisterSellerByActiveAdministrator() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        RegisterSellerService service =
                new RegisterSellerService(repository);

        Administrator administrator =
                createAdministrator(UserStatus.ACTIVE);

        Warehouse warehouse = createSellerWarehouse();

        Seller seller = service.register(
                administrator,
                "2001",
                "Test Seller",
                "seller@email.com",
                UserStatus.ACTIVE,
                warehouse
        );

        assertEquals("2001", seller.getIdentification());
        assertEquals("Test Seller", seller.getFullName());
        assertEquals("seller@email.com", seller.getEmail());
        assertEquals(UserStatus.ACTIVE, seller.getStatus());
        assertEquals(SystemRole.SELLER, seller.getRole());
        assertTrue(seller.managesWarehouse(warehouse));
        assertTrue(repository.contains(seller));
    }

    /**
     * Verifica que el repositorio sea obligatorio.
     */
    @Test
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegisterSellerService(null)
        );
    }

    /**
     * Verifica que el registro requiera
     * un administrador.
     */
    @Test
    void shouldRejectNullAdministrator() {
        RegisterSellerService service = createService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(
                        null,
                        "2001",
                        "Test Seller",
                        "seller@email.com",
                        UserStatus.ACTIVE,
                        createSellerWarehouse()
                )
        );
    }

    /**
     * Verifica que un administrador inactivo
     * no pueda registrar vendedores.
     */
    @Test
    void shouldRejectInactiveAdministrator() {
        RegisterSellerService service = createService();

        Administrator administrator =
                createAdministrator(UserStatus.INACTIVE);

        assertThrows(
                IllegalStateException.class,
                () -> service.register(
                        administrator,
                        "2001",
                        "Test Seller",
                        "seller@email.com",
                        UserStatus.ACTIVE,
                        createSellerWarehouse()
                )
        );
    }

    /**
     * Verifica que un administrador bloqueado
     * no pueda registrar vendedores.
     */
    @Test
    void shouldRejectBlockedAdministrator() {
        RegisterSellerService service = createService();

        Administrator administrator =
                createAdministrator(UserStatus.BLOCKED);

        assertThrows(
                IllegalStateException.class,
                () -> service.register(
                        administrator,
                        "2001",
                        "Test Seller",
                        "seller@email.com",
                        UserStatus.ACTIVE,
                        createSellerWarehouse()
                )
        );
    }

    /**
     * Verifica que la identificación del vendedor
     * no pueda estar registrada previamente.
     */
    @Test
    void shouldRejectDuplicatedIdentification() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        RegisterSellerService service =
                new RegisterSellerService(repository);

        repository.save(
                new Administrator(
                        "2001",
                        "Existing User",
                        "existing@email.com",
                        UserStatus.ACTIVE
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.register(
                        createAdministrator(UserStatus.ACTIVE),
                        "2001",
                        "Test Seller",
                        "seller@email.com",
                        UserStatus.ACTIVE,
                        createSellerWarehouse()
                )
        );

        assertEquals(1, repository.findAll().size());
    }

    /**
     * Verifica que el correo electrónico del vendedor
     * no pueda estar registrado previamente.
     */
    @Test
    void shouldRejectDuplicatedEmail() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        RegisterSellerService service =
                new RegisterSellerService(repository);

        repository.save(
                new Administrator(
                        "5001",
                        "Existing User",
                        "seller@email.com",
                        UserStatus.ACTIVE
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.register(
                        createAdministrator(UserStatus.ACTIVE),
                        "2001",
                        "Test Seller",
                        "seller@email.com",
                        UserStatus.ACTIVE,
                        createSellerWarehouse()
                )
        );

        assertEquals(1, repository.findAll().size());
    }

    /**
     * Verifica que un vendedor solamente pueda registrarse
     * con una bodega perteneciente a vendedores.
     */
    @Test
    void shouldRejectMarketplaceWarehouse() {
        RegisterSellerService service = createService();

        Warehouse marketplaceWarehouse =
                new Warehouse(
                        WarehouseOwnerType.MARKETPLACE
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(
                        createAdministrator(UserStatus.ACTIVE),
                        "2001",
                        "Test Seller",
                        "seller@email.com",
                        UserStatus.ACTIVE,
                        marketplaceWarehouse
                )
        );
    }

    /**
     * Crea el servicio con un repositorio en memoria.
     */
    private RegisterSellerService createService() {
        return new RegisterSellerService(
                new InMemoryUserRepository()
        );
    }

    /**
     * Crea un administrador con el estado indicado.
     */
    private Administrator createAdministrator(
            UserStatus status
    ) {
        return new Administrator(
                "4001",
                "Administrator",
                "admin@email.com",
                status
        );
    }

    /**
     * Crea una bodega perteneciente a un vendedor.
     */
    private Warehouse createSellerWarehouse() {
        return new Warehouse(
                WarehouseOwnerType.SELLER
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
    }
}