package application.services;

import application.domain.models.Administrator;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.ports.output.UserRepository;
import application.ports.output.WarehouseRepository;

/**
 * Servicio de aplicación encargado de asociar una bodega
 * previamente registrada con un vendedor.
 *
 * Solamente los administradores activos pueden realizar
 * esta operación. Además, el vendedor únicamente puede
 * recibir bodegas clasificadas como bodegas de vendedor.
 */
public class AssociateWarehouseService {

    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;

    /**
     * Construye el servicio con sus repositorios.
     *
     * @param userRepository repositorio utilizado para guardar al vendedor
     * @param warehouseRepository repositorio utilizado para comprobar la bodega
     */
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
     * Asocia una bodega registrada con un vendedor.
     *
     * @param associatedBy administrador que realiza la asociación
     * @param seller vendedor que administrará la bodega
     * @param warehouse bodega que será asociada
     */
    public void associate(
            Administrator associatedBy,
            Seller seller,
            Warehouse warehouse
    ) {
        validateAdministrator(associatedBy);
        validateSeller(seller);
        validateWarehouse(warehouse);
        validateRegisteredWarehouse(warehouse);
        validateNotAlreadyAssociated(seller, warehouse);

        /*
         * El modelo Seller comprueba que la bodega sea realmente
         * una bodega perteneciente a la categoría SELLER.
         */
        seller.associateWarehouse(warehouse);

        /*
         * La asociación forma parte de la información del vendedor,
         * por eso se guarda nuevamente el vendedor actualizado.
         */
        userRepository.save(seller);
    }

    /**
     * Valida que los repositorios requeridos existan.
     */
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

    /**
     * Valida que la asociación sea realizada por
     * un administrador activo.
     */
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

    /**
     * Valida que el vendedor exista.
     *
     * No exigimos que el vendedor esté activo porque el administrador
     * puede preparar su información antes de habilitarlo comercialmente.
     */
    private void validateSeller(Seller seller) {
        if (seller == null) {
            throw new IllegalArgumentException(
                    "Seller must not be null."
            );
        }
    }

    /**
     * Valida que la bodega exista.
     */
    private void validateWarehouse(Warehouse warehouse) {
        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "Warehouse must not be null."
            );
        }
    }

    /**
     * Impide asociar una bodega que todavía no haya sido registrada.
     */
    private void validateRegisteredWarehouse(
            Warehouse warehouse
    ) {
        if (!warehouseRepository.exists(warehouse)) {
            throw new IllegalStateException(
                    "Warehouse must be registered before association."
            );
        }
    }

    /**
     * Impide registrar dos veces la misma relación.
     */
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
}