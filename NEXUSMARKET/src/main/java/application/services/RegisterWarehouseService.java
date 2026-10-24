package application.services;

import application.domain.models.Administrator;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseOwnerType;
import application.ports.input.RegisterWarehouseUseCase;
import application.ports.output.UserRepository;
import application.ports.output.WarehouseRepository;

/**
 * Servicio de aplicación encargado de registrar bodegas.
 *
 * Recibe la identificación del solicitante y utiliza
 * UserRepository para obtener al usuario correspondiente.
 *
 * Solamente un administrador activo puede registrar
 * bodegas en NexusMarket.
 */
public class RegisterWarehouseService
        implements RegisterWarehouseUseCase {

    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;

    /**
     * Construye el servicio con los repositorios requeridos.
     *
     * @param userRepository repositorio utilizado para
     *        consultar al administrador
     * @param warehouseRepository repositorio utilizado
     *        para guardar la bodega
     */
    public RegisterWarehouseService(
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
     * Registra una nueva bodega.
     *
     * @param administratorIdentification identificación
     *        del administrador que realiza el registro
     * @param ownerType tipo de propietario de la bodega
     * @return bodega registrada
     */
    @Override
    public Warehouse register(
            String administratorIdentification,
            WarehouseOwnerType ownerType
    ) {
        validateIdentification(
                administratorIdentification
        );

        validateOwnerType(ownerType);

        User requester = userRepository
                .findByIdentification(
                        administratorIdentification
                )
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Requesting user was not found."
                        )
                );

        Administrator administrator =
                validateAdministrator(requester);

        validateActiveAdministrator(administrator);

        Warehouse warehouse =
                new Warehouse(ownerType);

        warehouseRepository.save(warehouse);

        return warehouse;
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
     * Valida la identificación recibida.
     */
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

    /**
     * Comprueba que el usuario encontrado
     * sea un administrador.
     */
    private Administrator validateAdministrator(
            User user
    ) {
        if (!(user instanceof Administrator administrator)) {
            throw new IllegalStateException(
                    "Only an administrator can register warehouses."
            );
        }

        return administrator;
    }

    /**
     * Comprueba que el administrador esté activo.
     */
    private void validateActiveAdministrator(
            Administrator administrator
    ) {
        if (!administrator.isActive()) {
            throw new IllegalStateException(
                    "Only an active administrator can register warehouses."
            );
        }
    }

    /**
     * Valida el tipo de propietario de la bodega.
     */
    private void validateOwnerType(
            WarehouseOwnerType ownerType
    ) {
        if (ownerType == null) {
            throw new IllegalArgumentException(
                    "Warehouse owner type must not be null."
            );
        }
    }
}