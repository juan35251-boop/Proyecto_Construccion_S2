package application.services;

import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.SystemRole;
import application.ports.input.WarehouseQueryUseCase;
import application.ports.output.WarehouseRepository;

import java.util.List;

/**
 * Servicio de aplicación encargado de consultar bodegas.
 *
 * Implementa el puerto de entrada WarehouseQueryUseCase,
 * permitiendo consultar bodegas sin depender directamente
 * de esta implementación.
 *
 * Permisos aplicados:
 *
 * - Administrador: consulta todas las bodegas.
 * - Supervisor: consulta todas las bodegas.
 * - Vendedor: consulta sus propias bodegas.
 * - Operador logístico: consulta bodegas del Marketplace.
 * - Comprador: no tiene acceso a las bodegas.
 */
public class WarehouseQueryService
        implements WarehouseQueryUseCase {

    private final WarehouseRepository warehouseRepository;

    /**
     * Construye el servicio con el repositorio de bodegas.
     *
     * @param warehouseRepository repositorio utilizado
     *                            en las consultas
     */
    public WarehouseQueryService(
            WarehouseRepository warehouseRepository
    ) {
        if (warehouseRepository == null) {
            throw new IllegalArgumentException(
                    "Warehouse repository must not be null."
            );
        }

        this.warehouseRepository =
                warehouseRepository;
    }

    /**
     * Obtiene las bodegas visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return lista de bodegas autorizadas
     */
    @Override
    public List<Warehouse> findAccessibleWarehouses(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        /*
         * Administradores y supervisores pueden
         * consultar todas las bodegas registradas.
         */
        if (hasGlobalAccess(requestedBy)) {
            return List.copyOf(
                    warehouseRepository.findAll()
            );
        }

        /*
         * El vendedor consulta las bodegas
         * que tiene asociadas.
         */
        if (requestedBy instanceof Seller seller) {
            return seller.getWarehouses();
        }

        /*
         * El operador logístico solamente puede consultar
         * bodegas pertenecientes al Marketplace.
         */
        if (requestedBy.getRole()
                == SystemRole.LOGISTICS_OPERATOR) {
            return warehouseRepository.findAll()
                    .stream()
                    .filter(
                            Warehouse::isMarketplaceWarehouse
                    )
                    .toList();
        }

        /*
         * Los compradores y los demás roles
         * no tienen autorización.
         */
        throw new IllegalStateException(
                "User is not authorized to query warehouses."
        );
    }

    /**
     * Valida que el usuario exista
     * y tenga un estado activo.
     *
     * @param requestedBy usuario que realiza la consulta
     */
    private void validateActiveRequester(
            User requestedBy
    ) {
        if (requestedBy == null) {
            throw new IllegalArgumentException(
                    "Requesting user must not be null."
            );
        }

        if (!requestedBy.isActive()) {
            throw new IllegalStateException(
                    "Only active users can query warehouses."
            );
        }
    }

    /**
     * Indica si el usuario puede consultar
     * todas las bodegas.
     *
     * @param user usuario que realiza la consulta
     * @return true si es administrador o supervisor
     */
    private boolean hasGlobalAccess(User user) {
        return user.getRole()
                == SystemRole.ADMINISTRATOR
                || user.getRole()
                == SystemRole.SUPERVISOR;
    }
}