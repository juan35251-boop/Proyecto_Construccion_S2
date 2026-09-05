package application.services;

import application.domain.models.Administrator;
import application.domain.models.Warehouse;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.services.support.InMemoryWarehouseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del servicio de registro de bodegas.
 */
class RegisterWarehouseServiceTest {

    @Test
    @DisplayName("Debe registrar una bodega del Marketplace")
    void shouldRegisterMarketplaceWarehouse() {
        InMemoryWarehouseRepository repository =
                new InMemoryWarehouseRepository();

        RegisterWarehouseService service =
                new RegisterWarehouseService(repository);

        Warehouse warehouse = service.register(
                createAdministrator(UserStatus.ACTIVE),
                WarehouseOwnerType.MARKETPLACE
        );

        assertTrue(warehouse.isMarketplaceWarehouse());
        assertTrue(repository.exists(warehouse));
        assertEquals(1, repository.getSaveCount());
    }

    @Test
    @DisplayName("Debe registrar una bodega de vendedor")
    void shouldRegisterSellerWarehouse() {
        InMemoryWarehouseRepository repository =
                new InMemoryWarehouseRepository();

        RegisterWarehouseService service =
                new RegisterWarehouseService(repository);

        Warehouse warehouse = service.register(
                createAdministrator(UserStatus.ACTIVE),
                WarehouseOwnerType.SELLER
        );

        assertTrue(warehouse.isSellerWarehouse());
        assertTrue(repository.exists(warehouse));
    }

    @Test
    @DisplayName("Debe rechazar un administrador nulo")
    void shouldRejectNullAdministrator() {
        RegisterWarehouseService service =
                new RegisterWarehouseService(
                        new InMemoryWarehouseRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(
                        null,
                        WarehouseOwnerType.MARKETPLACE
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un administrador inactivo")
    void shouldRejectInactiveAdministrator() {
        RegisterWarehouseService service =
                new RegisterWarehouseService(
                        new InMemoryWarehouseRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.register(
                        createAdministrator(
                                UserStatus.INACTIVE
                        ),
                        WarehouseOwnerType.MARKETPLACE
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un tipo de propietario nulo")
    void shouldRejectNullOwnerType() {
        RegisterWarehouseService service =
                new RegisterWarehouseService(
                        new InMemoryWarehouseRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(
                        createAdministrator(
                                UserStatus.ACTIVE
                        ),
                        null
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio nulo")
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegisterWarehouseService(null)
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