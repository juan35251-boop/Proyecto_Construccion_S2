package application.services;

import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.SystemRole;
import application.ports.input.WarehouseQueryUseCase;
import application.ports.output.UserRepository;
import application.ports.output.WarehouseRepository;

import java.util.List;

/**
 * Servicio de aplicación encargado de consultar bodegas.
 *
 * Recibe la identificación del solicitante y obtiene
 * al usuario mediante UserRepository.
 *
 * Permisos aplicados:
 *
 * - Administrador: consulta todas las bodegas.
 * - Supervisor: consulta todas las bodegas.
 * - Vendedor: consulta sus propias bodegas.
 * - Operador logístico: consulta bodegas del Marketplace.
 * - Comprador: no tiene acceso.
 */
public class WarehouseQueryService
        implements WarehouseQueryUseCase {

    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;

    /**
     * Construye el servicio con los repositorios requeridos.
     *
     * @param userRepository repositorio de usuarios
     * @param warehouseRepository repositorio de bodegas
     */
    public WarehouseQueryService(
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
     * Obtiene las bodegas visibles para el usuario.
     *
     * @param requesterIdentification identificación
     *        del usuario que realiza la consulta
     * @return lista de bodegas autorizadas
     */
    @Override
    public List<Warehouse> findAccessibleWarehouses(
            String requesterIdentification
    ) {
        User requester = findRequester(
                requesterIdentification
        );

        validateActiveRequester(requester);

        /*
         * Administradores y supervisores pueden
         * consultar todas las bodegas registradas.
         */
        if (hasGlobalAccess(requester)) {
            return List.copyOf(
                    warehouseRepository.findAll()
            );
        }

        /*
         * El vendedor consulta únicamente
         * las bodegas que tiene asociadas.
         */
        if (requester instanceof Seller seller) {
            return seller.getWarehouses();
        }

        /*
         * El operador logístico solamente consulta
         * bodegas pertenecientes al Marketplace.
         */
        if (requester.getRole()
                == SystemRole.LOGISTICS_OPERATOR) {
            return warehouseRepository.findAll()
                    .stream()
                    .filter(
                            Warehouse::isMarketplaceWarehouse
                    )
                    .toList();
        }

        throw new IllegalStateException(
                "User is not authorized to query warehouses."
        );
    }

    /**
     * Valida que existan los repositorios requeridos.
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
     * Busca al usuario que realiza la consulta.
     */
    private User findRequester(
            String identification
    ) {
        if (identification == null
                || identification.isBlank()) {
            throw new IllegalArgumentException(
                    "Requester identification must not be empty."
            );
        }

        return userRepository
                .findByIdentification(identification)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Requesting user was not found."
                        )
                );
    }

    /**
     * Comprueba que el usuario se encuentre activo.
     */
    private void validateActiveRequester(
            User requester
    ) {
        if (!requester.isActive()) {
            throw new IllegalStateException(
                    "Only active users can query warehouses."
            );
        }
    }

    /**
     * Indica si el usuario puede consultar
     * todas las bodegas.
     */
    private boolean hasGlobalAccess(User user) {
        return user.getRole()
                == SystemRole.ADMINISTRATOR
                || user.getRole()
                == SystemRole.SUPERVISOR;
    }
}