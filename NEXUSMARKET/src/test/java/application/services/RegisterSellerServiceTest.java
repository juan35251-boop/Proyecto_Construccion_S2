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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del servicio encargado de registrar vendedores.
 *
 * Se utiliza un repositorio en memoria para probar el servicio sin
 * conectarlo todavía con una base de datos real.
 */
class RegisterSellerServiceTest {

    /**
     * Comprueba que un administrador activo pueda registrar un vendedor.
     */
    @Test
    void shouldRegisterSellerWhenAdministratorIsActive() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        RegisterSellerService service =
                new RegisterSellerService(repository);

        Administrator administrator =
                createAdministrator(UserStatus.ACTIVE);

        Warehouse warehouse =
                new Warehouse(WarehouseOwnerType.SELLER);

        Seller seller = service.register(
                administrator,
                "2001",
                "Vendedor Nexus",
                "seller@nexusmarket.com",
                UserStatus.ACTIVE,
                warehouse
        );

        assertEquals("2001", seller.getIdentification());
        assertEquals("Vendedor Nexus", seller.getFullName());
        assertEquals(
                "seller@nexusmarket.com",
                seller.getEmail()
        );
        assertEquals(UserStatus.ACTIVE, seller.getStatus());
        assertEquals(SystemRole.SELLER, seller.getRole());
        assertTrue(seller.managesWarehouse(warehouse));
        assertSame(seller, repository.getLastSavedUser());
    }

    /**
     * Comprueba que el servicio no pueda crearse sin repositorio.
     */
    @Test
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegisterSellerService(null)
        );
    }

    /**
     * Comprueba que el registro requiera un administrador.
     */
    @Test
    void shouldRejectNullAdministrator() {
        RegisterSellerService service =
                new RegisterSellerService(
                        new InMemoryUserRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(
                        null,
                        "2001",
                        "Vendedor Nexus",
                        "seller@nexusmarket.com",
                        UserStatus.ACTIVE,
                        new Warehouse(
                                WarehouseOwnerType.SELLER
                        )
                )
        );
    }

    /**
     * Comprueba que un administrador inactivo no pueda
     * registrar vendedores.
     */
    @Test
    void shouldRejectInactiveAdministrator() {
        RegisterSellerService service =
                new RegisterSellerService(
                        new InMemoryUserRepository()
                );

        Administrator administrator =
                createAdministrator(UserStatus.INACTIVE);

        assertThrows(
                IllegalStateException.class,
                () -> service.register(
                        administrator,
                        "2001",
                        "Vendedor Nexus",
                        "seller@nexusmarket.com",
                        UserStatus.ACTIVE,
                        new Warehouse(
                                WarehouseOwnerType.SELLER
                        )
                )
        );
    }

    /**
     * Comprueba que un administrador bloqueado no pueda
     * registrar vendedores.
     */
    @Test
    void shouldRejectBlockedAdministrator() {
        RegisterSellerService service =
                new RegisterSellerService(
                        new InMemoryUserRepository()
                );

        Administrator administrator =
                createAdministrator(UserStatus.BLOCKED);

        assertThrows(
                IllegalStateException.class,
                () -> service.register(
                        administrator,
                        "2001",
                        "Vendedor Nexus",
                        "seller@nexusmarket.com",
                        UserStatus.ACTIVE,
                        new Warehouse(
                                WarehouseOwnerType.SELLER
                        )
                )
        );
    }

    /**
     * Comprueba que no puedan existir dos usuarios con
     * la misma identificación.
     */
    @Test
    void shouldRejectDuplicatedIdentification() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        repository.save(
                new Administrator(
                        "2001",
                        "Administrador existente",
                        "existing@nexusmarket.com",
                        UserStatus.ACTIVE
                )
        );

        RegisterSellerService service =
                new RegisterSellerService(repository);

        assertThrows(
                IllegalStateException.class,
                () -> service.register(
                        createAdministrator(UserStatus.ACTIVE),
                        "2001",
                        "Vendedor Nexus",
                        "seller@nexusmarket.com",
                        UserStatus.ACTIVE,
                        new Warehouse(
                                WarehouseOwnerType.SELLER
                        )
                )
        );

        assertEquals(1, repository.size());
    }

    /**
     * Comprueba que no puedan existir dos usuarios con
     * el mismo correo electrónico.
     */
    @Test
    void shouldRejectDuplicatedEmail() {
        InMemoryUserRepository repository =
                new InMemoryUserRepository();

        repository.save(
                new Administrator(
                        "1002",
                        "Administrador existente",
                        "seller@nexusmarket.com",
                        UserStatus.ACTIVE
                )
        );

        RegisterSellerService service =
                new RegisterSellerService(repository);

        assertThrows(
                IllegalStateException.class,
                () -> service.register(
                        createAdministrator(UserStatus.ACTIVE),
                        "2001",
                        "Vendedor Nexus",
                        "seller@nexusmarket.com",
                        UserStatus.ACTIVE,
                        new Warehouse(
                                WarehouseOwnerType.SELLER
                        )
                )
        );

        assertEquals(1, repository.size());
    }

    /**
     * Comprueba que el vendedor no pueda registrarse con una
     * bodega perteneciente al marketplace.
     */
    @Test
    void shouldRejectMarketplaceWarehouse() {
        RegisterSellerService service =
                new RegisterSellerService(
                        new InMemoryUserRepository()
                );

        Warehouse marketplaceWarehouse =
                new Warehouse(WarehouseOwnerType.MARKETPLACE);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(
                        createAdministrator(UserStatus.ACTIVE),
                        "2001",
                        "Vendedor Nexus",
                        "seller@nexusmarket.com",
                        UserStatus.ACTIVE,
                        marketplaceWarehouse
                )
        );
    }

    /**
     * Crea un administrador para reutilizarlo en las pruebas.
     */
    private Administrator createAdministrator(
            UserStatus status
    ) {
        return new Administrator(
                "1001",
                "Administrador Nexus",
                "admin@nexusmarket.com",
                status
        );
    }

    /**
     * Implementación sencilla de UserRepository que almacena
     * usuarios en una lista durante las pruebas.
     */
    private static class InMemoryUserRepository
            implements UserRepository {

        private final List<User> users = new ArrayList<>();

        @Override
        public boolean existsByIdentification(
                String identification
        ) {
            return users.stream().anyMatch(
                    user -> user.getIdentification()
                            .equals(identification)
            );
        }

        @Override
        public boolean existsByEmail(String email) {
            return users.stream().anyMatch(
                    user -> user.getEmail().equals(email)
            );
        }

        @Override
        public void save(User user) {
            users.add(user);
        }

        /**
         * Obtiene el último usuario guardado para comprobar
         * el resultado de la prueba.
         */
        public User getLastSavedUser() {
            return users.get(users.size() - 1);
        }

        /**
         * Obtiene la cantidad de usuarios almacenados.
         */
        public int size() {
            return users.size();
        }
    }
}
