package application.services;

import application.domain.models.Administrator;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.ports.input.AssociateWarehouseUseCase;
import application.ports.output.UserRepository;
import application.ports.output.WarehouseRepository;

/**
 * Servicio encargado de asociar una bodega
 * previamente registrada con un vendedor.
 */
public class AssociateWarehouseService
        implements AssociateWarehouseUseCase {

    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;

    public AssociateWarehouseService(
            UserRepository userRepository,
            WarehouseRepository warehouseRepository
    ) {
        validateRepositories(
                userRepository,
                warehouseRepository
        );

        this.userRepository = userRepository;
        this.warehouseRepository = warehouseRepository;
    }

    /**
     * Asocia los objetos de dominio recibidos.
     */
    @Override
    public void associate(
            Administrator associatedBy,
            Seller seller,
            Warehouse warehouse
    ) {
        validateAdministrator(associatedBy);
        validateSeller(seller);
        validateWarehouse(warehouse);
        validateRegisteredWarehouse(warehouse);

        validateNotAlreadyAssociated(
                seller,
                warehouse
        );

        seller.associateWarehouse(warehouse);
        userRepository.save(seller);
    }

    /**
     * Localiza al administrador, vendedor y bodega
     * antes de realizar la asociación.
     */
    @Override
    public void associate(
            String administratorIdentification,
            String sellerIdentification,
            String warehouseId
    ) {
        validateIdentifier(
                administratorIdentification,
                "Administrator identification"
        );

        validateIdentifier(
                sellerIdentification,
                "Seller identification"
        );

        validateIdentifier(
                warehouseId,
                "Warehouse identifier"
        );

        User administratorUser =
                userRepository
                        .findByIdentification(
                                administratorIdentification
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Administrator was not found."
                                )
                        );

        if (!(administratorUser
                instanceof Administrator administrator)) {
            throw new IllegalStateException(
                    "Only an administrator can associate warehouses."
            );
        }

        User sellerUser =
                userRepository
                        .findByIdentification(
                                sellerIdentification
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Seller was not found."
                                )
                        );

        if (!(sellerUser instanceof Seller seller)) {
            throw new IllegalStateException(
                    "Warehouse owner must be a seller."
            );
        }

        Warehouse warehouse =
                warehouseRepository
                        .findById(warehouseId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Warehouse was not found."
                                )
                        );

        associate(
                administrator,
                seller,
                warehouse
        );
    }

    private void validateRepositories(
            UserRepository userRepository,
            WarehouseRepository warehouseRepository
    ) {
        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }

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
                    "Administrator must not be null."
            );
        }

        if (!administrator.isActive()) {
            throw new IllegalStateException(
                    "Only an active administrator can associate warehouses."
            );
        }
    }

    private void validateSeller(
            Seller seller
    ) {
        if (seller == null) {
            throw new IllegalArgumentException(
                    "Seller must not be null."
            );
        }
    }

    private void validateWarehouse(
            Warehouse warehouse
    ) {
        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "Warehouse must not be null."
            );
        }

        if (!warehouse.isSellerWarehouse()) {
            throw new IllegalArgumentException(
                    "A seller can only receive seller warehouses."
            );
        }
    }

    private void validateRegisteredWarehouse(
            Warehouse warehouse
    ) {
        if (!warehouseRepository.exists(warehouse)) {
            throw new IllegalStateException(
                    "Warehouse must be registered before association."
            );
        }
    }

    private void validateNotAlreadyAssociated(
            Seller seller,
            Warehouse warehouse
    ) {
        if (seller.managesWarehouse(warehouse)) {
            throw new IllegalStateException(
                    "Warehouse is already associated with this seller."
            );
        }
    }

    private void validateIdentifier(
            String identifier,
            String fieldName
    ) {
        if (identifier == null
                || identifier.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }
    }
}