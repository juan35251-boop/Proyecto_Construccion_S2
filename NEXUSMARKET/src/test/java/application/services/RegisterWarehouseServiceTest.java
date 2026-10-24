package application.services;

import application.domain.models.Administrator;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.services.support.InMemoryUserRepository;
import application.services.support.InMemoryWarehouseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del servicio encargado
 * de registrar bodegas.
 */
class RegisterWarehouseServiceTest {

    @Test
    @DisplayName("Debe registrar una bodega del Marketplace")
    void shouldRegisterMarketplaceWarehouse() {
        InMemoryUserRepository userRepository =
                new InMemoryUserRepository();

        InMemoryWarehouseRepository warehouseRepository =
                new InMemoryWarehouseRepository();

        Administrator administrator =
                createAdministrator(UserStatus.ACTIVE);

        userRepository.save(administrator);

        RegisterWarehouseService service =
                new RegisterWarehouseService(
                        userRepository,
                        warehouseRepository
                );

        Warehouse warehouse = service.register(
                administrator.getIdentification(),
                WarehouseOwnerType.MARKETPLACE
        );

        assertTrue(warehouse.isMarketplaceWarehouse());
        assertTrue(warehouseRepository.exists(warehouse));
        assertEquals(
                1,
                warehouseRepository.getSaveCount()
        );
    }

    @Test
    @DisplayName("Debe registrar una bodega de vendedor")
    void shouldRegisterSellerWarehouse() {
        InMemoryUserRepository userRepository =
                new InMemoryUserRepository();

        InMemoryWarehouseRepository warehouseRepository =
                new InMemoryWarehouseRepository();

        Administrator administrator =
                createAdministrator(UserStatus.ACTIVE);

        userRepository.save(administrator);

        RegisterWarehouseService service =
                new RegisterWarehouseService(
                        userRepository,
                        warehouseRepository
                );

        Warehouse warehouse = service.register(
                administrator.getIdentification(),
                WarehouseOwnerType.SELLER
        );

        assertTrue(warehouse.isSellerWarehouse());
        assertTrue(warehouseRepository.exists(warehouse));
    }

    @Test
    @DisplayName("Debe rechazar una identificación nula")
    void shouldRejectNullIdentification() {
        RegisterWarehouseService service =
                createService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(
                        null,
                        WarehouseOwnerType.MARKETPLACE
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar una identificación vacía")
    void shouldRejectBlankIdentification() {
        RegisterWarehouseService service =
                createService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(
                        " ",
                        WarehouseOwnerType.MARKETPLACE
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario inexistente")
    void shouldRejectMissingUser() {
        RegisterWarehouseService service =
                createService();

        assertThrows(
                IllegalStateException.class,
                () -> service.register(
                        "9999",
                        WarehouseOwnerType.MARKETPLACE
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario que no sea administrador")
    void shouldRejectNonAdministrator() {
        InMemoryUserRepository userRepository =
                new InMemoryUserRepository();

        InMemoryWarehouseRepository warehouseRepository =
                new InMemoryWarehouseRepository();

        Seller seller = new Seller(
                "2001",
                "Seller",
                "seller@email.com",
                UserStatus.ACTIVE,
                new Warehouse(
                        WarehouseOwnerType.SELLER
                )
        );

        userRepository.save(seller);

        RegisterWarehouseService service =
                new RegisterWarehouseService(
                        userRepository,
                        warehouseRepository
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.register(
                        seller.getIdentification(),
                        WarehouseOwnerType.MARKETPLACE
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un administrador inactivo")
    void shouldRejectInactiveAdministrator() {
        InMemoryUserRepository userRepository =
                new InMemoryUserRepository();

        Administrator administrator =
                createAdministrator(UserStatus.INACTIVE);

        userRepository.save(administrator);

        RegisterWarehouseService service =
                new RegisterWarehouseService(
                        userRepository,
                        new InMemoryWarehouseRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.register(
                        administrator.getIdentification(),
                        WarehouseOwnerType.MARKETPLACE
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un tipo de propietario nulo")
    void shouldRejectNullOwnerType() {
        InMemoryUserRepository userRepository =
                new InMemoryUserRepository();

        Administrator administrator =
                createAdministrator(UserStatus.ACTIVE);

        userRepository.save(administrator);

        RegisterWarehouseService service =
                new RegisterWarehouseService(
                        userRepository,
                        new InMemoryWarehouseRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(
                        administrator.getIdentification(),
                        null
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio de usuarios nulo")
    void shouldRejectNullUserRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegisterWarehouseService(
                        null,
                        new InMemoryWarehouseRepository()
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio de bodegas nulo")
    void shouldRejectNullWarehouseRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegisterWarehouseService(
                        new InMemoryUserRepository(),
                        null
                )
        );
    }

    /**
     * Crea el servicio con repositorios vacíos.
     */
    private RegisterWarehouseService createService() {
        return new RegisterWarehouseService(
                new InMemoryUserRepository(),
                new InMemoryWarehouseRepository()
        );
    }

    /**
     * Crea un administrador para las pruebas.
     */
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