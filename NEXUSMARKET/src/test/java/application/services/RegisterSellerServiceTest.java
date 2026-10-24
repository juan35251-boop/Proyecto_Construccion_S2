package application.services;

import application.domain.models.Administrator;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.ports.output.UserRepository;
import application.ports.output.WarehouseRepository;
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
     * registrar un vendedor y su primera bodega.
     */
    @Test
    void shouldRegisterSellerByActiveAdministrator() {
        InMemoryUserRepository userRepository =
                new InMemoryUserRepository();

        InMemoryWarehouseRepository warehouseRepository =
                new InMemoryWarehouseRepository();

        RegisterSellerService service =
                new RegisterSellerService(
                        userRepository,
                        warehouseRepository
                );

        Administrator administrator =
                createAdministrator(UserStatus.ACTIVE);

        Warehouse warehouse =
                createSellerWarehouse();

        Seller seller = service.register(
                administrator,
                "2001",
                "Test Seller",
                "seller@email.com",
                UserStatus.ACTIVE,
                warehouse
        );

        assertEquals(
                "2001",
                seller.getIdentification()
        );

        assertEquals(
                "Test Seller",
                seller.getFullName()
        );

        assertEquals(
                "seller@email.com",
                seller.getEmail()
        );

        assertEquals(
                UserStatus.ACTIVE,
                seller.getStatus()
        );

        assertEquals(
                SystemRole.SELLER,
                seller.getRole()
        );

        assertTrue(
                seller.managesWarehouse(warehouse)
        );

        assertTrue(
                userRepository.contains(seller)
        );

        assertTrue(
                warehouseRepository.exists(warehouse)
        );
    }

    /**
     * Verifica que el repositorio de usuarios
     * sea obligatorio.
     */
    @Test
    void shouldRejectNullUserRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegisterSellerService(
                        null,
                        new InMemoryWarehouseRepository()
                )
        );
    }

    /**
     * Verifica que el repositorio de bodegas
     * sea obligatorio.
     */
    @Test
    void shouldRejectNullWarehouseRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegisterSellerService(
                        new InMemoryUserRepository(),
                        null
                )
        );
    }

    /**
     * Verifica que el registro requiera
     * un administrador.
     */
    @Test
    void shouldRejectNullAdministrator() {
        RegisterSellerService service =
                createService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(
                        (Administrator) null,
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
        RegisterSellerService service =
                createService();

        Administrator administrator =
                createAdministrator(
                        UserStatus.INACTIVE
                );

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
        RegisterSellerService service =
                createService();

        Administrator administrator =
                createAdministrator(
                        UserStatus.BLOCKED
                );

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
     * sea única.
     */
    @Test
    void shouldRejectDuplicatedIdentification() {
        InMemoryUserRepository userRepository =
                new InMemoryUserRepository();

        InMemoryWarehouseRepository warehouseRepository =
                new InMemoryWarehouseRepository();

        RegisterSellerService service =
                new RegisterSellerService(
                        userRepository,
                        warehouseRepository
                );

        userRepository.save(
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
                        createAdministrator(
                                UserStatus.ACTIVE
                        ),
                        "2001",
                        "Test Seller",
                        "seller@email.com",
                        UserStatus.ACTIVE,
                        createSellerWarehouse()
                )
        );

        assertEquals(
                1,
                userRepository.findAll().size()
        );

        assertTrue(
                warehouseRepository
                        .findAll()
                        .isEmpty()
        );
    }

    /**
     * Verifica que el correo electrónico
     * sea único.
     */
    @Test
    void shouldRejectDuplicatedEmail() {
        InMemoryUserRepository userRepository =
                new InMemoryUserRepository();

        InMemoryWarehouseRepository warehouseRepository =
                new InMemoryWarehouseRepository();

        RegisterSellerService service =
                new RegisterSellerService(
                        userRepository,
                        warehouseRepository
                );

        userRepository.save(
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
                        createAdministrator(
                                UserStatus.ACTIVE
                        ),
                        "2001",
                        "Test Seller",
                        "seller@email.com",
                        UserStatus.ACTIVE,
                        createSellerWarehouse()
                )
        );

        assertEquals(
                1,
                userRepository.findAll().size()
        );

        assertTrue(
                warehouseRepository
                        .findAll()
                        .isEmpty()
        );
    }

    /**
     * Verifica que un vendedor solamente pueda
     * registrarse con una bodega de vendedor.
     */
    @Test
    void shouldRejectMarketplaceWarehouse() {
        RegisterSellerService service =
                createService();

        Warehouse marketplaceWarehouse =
                new Warehouse(
                        WarehouseOwnerType.MARKETPLACE
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(
                        createAdministrator(
                                UserStatus.ACTIVE
                        ),
                        "2001",
                        "Test Seller",
                        "seller@email.com",
                        UserStatus.ACTIVE,
                        marketplaceWarehouse
                )
        );
    }

    /**
     * Crea el servicio con sus repositorios
     * en memoria.
     */
    private RegisterSellerService createService() {
        return new RegisterSellerService(
                new InMemoryUserRepository(),
                new InMemoryWarehouseRepository()
        );
    }

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

    private Warehouse createSellerWarehouse() {
        return new Warehouse(
                WarehouseOwnerType.SELLER
        );
    }

    /**
     * Repositorio de usuarios para las pruebas.
     */
    private static class InMemoryUserRepository
            implements UserRepository {

        private final List<User> users =
                new ArrayList<>();

        @Override
        public boolean existsByIdentification(
                String identification
        ) {
            return users.stream()
                    .anyMatch(user ->
                            user.getIdentification()
                                    .equals(identification)
                    );
        }

        @Override
        public boolean existsByEmail(
                String email
        ) {
            return users.stream()
                    .anyMatch(user ->
                            user.getEmail()
                                    .equals(email)
                    );
        }

        @Override
        public Optional<User> findByIdentification(
                String identification
        ) {
            return users.stream()
                    .filter(user ->
                            user.getIdentification()
                                    .equals(identification)
                    )
                    .findFirst();
        }

        @Override
        public List<User> findAll() {
            return List.copyOf(users);
        }

        @Override
        public void save(
                User user
        ) {
            users.add(user);
        }

        boolean contains(
                User user
        ) {
            return users.contains(user);
        }
    }

    /**
     * Repositorio de bodegas para las pruebas.
     */
    private static class InMemoryWarehouseRepository
            implements WarehouseRepository {

        private final List<Warehouse> warehouses =
                new ArrayList<>();

        @Override
        public void save(
                Warehouse warehouse
        ) {
            warehouses.add(warehouse);
        }

        @Override
        public boolean exists(
                Warehouse warehouse
        ) {
            return warehouses.contains(warehouse);
        }

        @Override
        public Optional<Warehouse> findById(
                String warehouseId
        ) {
            return warehouses.stream()
                    .filter(warehouse ->
                            warehouse.getWarehouseId()
                                    .equals(warehouseId)
                    )
                    .findFirst();
        }

        @Override
        public List<Warehouse> findAll() {
            return List.copyOf(warehouses);
        }
    }
}