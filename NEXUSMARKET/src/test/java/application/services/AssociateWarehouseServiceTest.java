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
 * Pruebas del servicio que asocia bodegas con vendedores.
 */
class AssociateWarehouseServiceTest {

    @Test
    @DisplayName("Debe asociar una bodega registrada con el vendedor")
    void shouldAssociateRegisteredWarehouse() {
        InMemoryUserRepository userRepository =
                new InMemoryUserRepository();

        InMemoryWarehouseRepository warehouseRepository =
                new InMemoryWarehouseRepository();

        Warehouse warehouse =
                new Warehouse(WarehouseOwnerType.SELLER);

        warehouseRepository.save(warehouse);

        Seller seller = createSeller();

        AssociateWarehouseService service =
                new AssociateWarehouseService(
                        userRepository,
                        warehouseRepository
                );

        service.associate(
                createAdministrator(UserStatus.ACTIVE),
                seller,
                warehouse
        );

        assertTrue(seller.managesWarehouse(warehouse));
        assertTrue(userRepository.contains(seller));
        assertEquals(1, userRepository.getSaveCount());
    }

    @Test
    @DisplayName("Debe rechazar una bodega no registrada")
    void shouldRejectUnregisteredWarehouse() {
        AssociateWarehouseService service =
                new AssociateWarehouseService(
                        new InMemoryUserRepository(),
                        new InMemoryWarehouseRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.associate(
                        createAdministrator(
                                UserStatus.ACTIVE
                        ),
                        createSeller(),
                        new Warehouse(
                                WarehouseOwnerType.SELLER
                        )
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar una asociación duplicada")
    void shouldRejectDuplicatedAssociation() {
        InMemoryWarehouseRepository warehouseRepository =
                new InMemoryWarehouseRepository();

        Seller seller = createSeller();
        Warehouse warehouse =
                seller.getWarehouses().get(0);

        warehouseRepository.save(warehouse);

        AssociateWarehouseService service =
                new AssociateWarehouseService(
                        new InMemoryUserRepository(),
                        warehouseRepository
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.associate(
                        createAdministrator(
                                UserStatus.ACTIVE
                        ),
                        seller,
                        warehouse
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar una bodega del Marketplace")
    void shouldRejectMarketplaceWarehouse() {
        InMemoryWarehouseRepository warehouseRepository =
                new InMemoryWarehouseRepository();

        Warehouse warehouse =
                new Warehouse(
                        WarehouseOwnerType.MARKETPLACE
                );

        warehouseRepository.save(warehouse);

        AssociateWarehouseService service =
                new AssociateWarehouseService(
                        new InMemoryUserRepository(),
                        warehouseRepository
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.associate(
                        createAdministrator(
                                UserStatus.ACTIVE
                        ),
                        createSeller(),
                        warehouse
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un administrador inactivo")
    void shouldRejectInactiveAdministrator() {
        AssociateWarehouseService service =
                new AssociateWarehouseService(
                        new InMemoryUserRepository(),
                        new InMemoryWarehouseRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.associate(
                        createAdministrator(
                                UserStatus.INACTIVE
                        ),
                        createSeller(),
                        new Warehouse(
                                WarehouseOwnerType.SELLER
                        )
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un vendedor nulo")
    void shouldRejectNullSeller() {
        AssociateWarehouseService service =
                new AssociateWarehouseService(
                        new InMemoryUserRepository(),
                        new InMemoryWarehouseRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.associate(
                        createAdministrator(
                                UserStatus.ACTIVE
                        ),
                        null,
                        new Warehouse(
                                WarehouseOwnerType.SELLER
                        )
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar una bodega nula")
    void shouldRejectNullWarehouse() {
        AssociateWarehouseService service =
                new AssociateWarehouseService(
                        new InMemoryUserRepository(),
                        new InMemoryWarehouseRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.associate(
                        createAdministrator(
                                UserStatus.ACTIVE
                        ),
                        createSeller(),
                        null
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio de usuarios nulo")
    void shouldRejectNullUserRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AssociateWarehouseService(
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
                () -> new AssociateWarehouseService(
                        new InMemoryUserRepository(),
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

    private Seller createSeller() {
        return new Seller(
                "2001",
                "Seller",
                "seller@email.com",
                UserStatus.ACTIVE,
                new Warehouse(
                        WarehouseOwnerType.SELLER
                )
        );
    }
}