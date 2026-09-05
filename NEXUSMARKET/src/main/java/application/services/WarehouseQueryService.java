package application.services;

import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.SystemRole;
import application.ports.output.WarehouseRepository;

import java.util.List;

/**
 * Servicio de aplicación encargado de consultar bodegas.
 *
 * Permisos aplicados:
 *
 * - Administrador: consulta todas las bodegas.
 * - Supervisor: consulta todas las bodegas para monitoreo.
 * - Vendedor: consulta únicamente sus propias bodegas.
 * - Operador logístico: consulta bodegas del Marketplace.
 * - Comprador: no tiene acceso a la administración de bodegas.
 */
public class WarehouseQueryService {

    private final WarehouseRepository warehouseRepository;

    /**
     * Construye el servicio con el repositorio de bodegas.
     *
     * @param warehouseRepository repositorio utilizado en las consultas
     */
    public WarehouseQueryService(
            WarehouseRepository warehouseRepository
    ) {
        if (warehouseRepository == null) {
            throw new IllegalArgumentException(
                    "Warehouse repository must not be null."
            );
        }

        this.warehouseRepository = warehouseRepository;
    }

    /**
     * Obtiene las bodegas visibles para el usuario solicitado.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return lista de bodegas autorizadas
     */
    public List<Warehouse> findAccessibleWarehouses(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        if (hasGlobalAccess(requestedBy)) {
            return List.copyOf(warehouseRepository.findAll());
        }

        if (requestedBy instanceof Seller seller) {
            return seller.getWarehouses();
        }

        if (requestedBy.getRole()
                == SystemRole.LOGISTICS_OPERATOR) {
            return warehouseRepository.findAll()
                    .stream()
                    .filter(Warehouse::isMarketplaceWarehouse)
                    .toList();
        }

        throw new IllegalStateException(
                "User is not authorized to query warehouses."
        );
    }

    /**
     * Valida que el usuario exista y tenga un estado activo.
     */
    private void validateActiveRequester(User requestedBy) {
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
     * El administrador y el supervisor pueden consultar
     * todas las bodegas.
     */
    private boolean hasGlobalAccess(User user) {
        return user.getRole() == SystemRole.ADMINISTRATOR
                || user.getRole() == SystemRole.SUPERVISOR;
    }
}