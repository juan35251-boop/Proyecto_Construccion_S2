package application.services;

import application.domain.models.Inventory;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.InventoryQueryUseCase;
import application.ports.output.InventoryQueryRepository;

import java.util.List;

/**
 * Servicio de aplicación encargado de consultar el inventario.
 *
 * Implementa el puerto de entrada InventoryQueryUseCase,
 * permitiendo consultar inventarios sin depender directamente
 * de esta implementación.
 *
 * Aplica los permisos definidos para cada participante:
 *
 * - El administrador puede consultar todo.
 * - El supervisor puede consultar todo.
 * - El vendedor consulta inventarios de sus productos
 *   y sus bodegas.
 * - El operador logístico consulta inventarios
 *   del Marketplace.
 * - El comprador no puede consultar inventarios.
 */
public class InventoryQueryService
        implements InventoryQueryUseCase {

    private final InventoryQueryRepository
            inventoryQueryRepository;

    /**
     * Construye el servicio con el repositorio
     * de consulta.
     *
     * @param inventoryQueryRepository repositorio
     *                                 de consulta de inventarios
     */
    public InventoryQueryService(
            InventoryQueryRepository inventoryQueryRepository
    ) {
        if (inventoryQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Inventory query repository must not be null."
            );
        }

        this.inventoryQueryRepository =
                inventoryQueryRepository;
    }

    /**
     * Obtiene los inventarios visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return inventarios autorizados
     */
    @Override
    public List<Inventory> findAccessibleInventory(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        List<Inventory> inventories =
                inventoryQueryRepository.findAll();

        /*
         * Administradores y supervisores pueden
         * consultar todos los inventarios.
         */
        if (hasGlobalAccess(requestedBy)) {
            return List.copyOf(inventories);
        }

        /*
         * El vendedor solamente puede consultar
         * inventarios que cumplan dos condiciones:
         *
         * 1. El producto pertenece a su catálogo.
         * 2. La bodega está bajo su administración.
         */
        if (requestedBy instanceof Seller seller) {
            return inventories.stream()
                    .filter(
                            inventory ->
                                    seller.managesProduct(
                                            inventory.getProduct()
                                    )
                    )
                    .filter(
                            inventory ->
                                    seller.managesWarehouse(
                                            inventory.getWarehouse()
                                    )
                    )
                    .toList();
        }

        /*
         * El operador logístico solamente consulta
         * inventarios ubicados en bodegas del Marketplace.
         */
        if (requestedBy.getRole()
                == SystemRole.LOGISTICS_OPERATOR) {
            return inventories.stream()
                    .filter(
                            inventory ->
                                    inventory.getWarehouse()
                                            .isMarketplaceWarehouse()
                    )
                    .toList();
        }

        /*
         * El comprador no tiene acceso a la información
         * interna de inventarios.
         */
        throw new IllegalStateException(
                "Buyer is not authorized to query inventory."
        );
    }

    /**
     * Valida que el usuario exista y esté activo.
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
                    "Only active users can query inventory."
            );
        }
    }

    /**
     * Indica si el usuario posee acceso global
     * de consulta sobre los inventarios.
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