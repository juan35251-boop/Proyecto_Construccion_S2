package application.services;

import application.domain.models.Administrator;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.UserStatus;
import application.ports.input.RegisterSellerUseCase;
import application.ports.output.UserRepository;
import application.ports.output.WarehouseRepository;

/**
 * Servicio encargado de registrar vendedores.
 *
 * Solamente un administrador activo puede registrar
 * vendedores. La primera bodega del vendedor también
 * se almacena para conservar su identificador permanente.
 */
public class RegisterSellerService
        implements RegisterSellerUseCase {

    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;

    /**
     * Constructor utilizado por la aplicación.
     */
    public RegisterSellerService(
            UserRepository userRepository,
            WarehouseRepository warehouseRepository
    ) {
        validateUserRepository(userRepository);
        validateWarehouseRepository(
                warehouseRepository
        );

        this.userRepository = userRepository;
        this.warehouseRepository =
                warehouseRepository;
    }

    @Override
    public Seller register(
            Administrator administrator,
            String identification,
            String fullName,
            String email,
            UserStatus status,
            Warehouse firstWarehouse
    ) {
        validateAdministrator(administrator);
        validateWarehouse(firstWarehouse);

        Seller seller = new Seller(
                identification,
                fullName,
                email,
                status,
                firstWarehouse
        );

        validateUniqueIdentification(
                seller.getIdentification()
        );

        validateUniqueEmail(
                seller.getEmail()
        );

        /*
         * La bodega debe existir en su repositorio antes
         * de formar parte de la información del vendedor.
         */
        warehouseRepository.save(firstWarehouse);
        userRepository.save(seller);

        return seller;
    }

    @Override
    public Seller register(
            String administratorIdentification,
            String identification,
            String fullName,
            String email,
            UserStatus status,
            Warehouse firstWarehouse
    ) {
        validateIdentification(
                administratorIdentification
        );

        User requestedBy = userRepository
                .findByIdentification(
                        administratorIdentification
                )
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Administrator was not found."
                        )
                );

        if (!(requestedBy
                instanceof Administrator administrator)) {
            throw new IllegalStateException(
                    "Only an administrator can register sellers."
            );
        }

        return register(
                administrator,
                identification,
                fullName,
                email,
                status,
                firstWarehouse
        );
    }

    private void validateUserRepository(
            UserRepository userRepository
    ) {
        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }
    }

    private void validateWarehouseRepository(
            WarehouseRepository warehouseRepository
    ) {
        if (warehouseRepository == null) {
            throw new IllegalArgumentException(
                    "Warehouse repository must not be null."
            );
        }
    }

    private void validateAdministrator(
            Administrator administrator
    ) {
        if (administrator == null) {
            throw new IllegalArgumentException(
                    "Seller registration requires an administrator."
            );
        }

        if (!administrator.isActive()) {
            throw new IllegalStateException(
                    "Only an active administrator can register sellers."
            );
        }
    }

    private void validateIdentification(
            String identification
    ) {
        if (identification == null
                || identification.isBlank()) {
            throw new IllegalArgumentException(
                    "Administrator identification must not be empty."
            );
        }
    }

    private void validateWarehouse(
            Warehouse warehouse
    ) {
        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "Seller registration requires a warehouse."
            );
        }

        if (!warehouse.isSellerWarehouse()) {
            throw new IllegalArgumentException(
                    "Seller registration requires a seller warehouse."
            );
        }
    }

    private void validateUniqueIdentification(
            String identification
    ) {
        if (userRepository.existsByIdentification(
                identification
        )) {
            throw new IllegalStateException(
                    "Identification is already registered."
            );
        }
    }

    private void validateUniqueEmail(
            String email
    ) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException(
                    "Email is already registered."
            );
        }
    }
}