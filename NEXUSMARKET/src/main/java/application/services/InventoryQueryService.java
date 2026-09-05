package application.services;

import application.domain.models.Inventory;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.output.InventoryQueryRepository;

import java.util.List;

/**
 * Servicio de aplicación encargado de consultar el inventario.
 *
 * Aplica los permisos definidos para cada participante:
 *
 * - El administrador y el supervisor pueden consultar todo.
 * - El vendedor consulta inventarios de sus productos y bodegas.
 * - El operador logístico consulta inventarios del Marketplace.
 * - El comprador no puede consultar inventarios.
 */
public class InventoryQueryService {

    private final InventoryQueryRepository inventoryQueryRepository;

    /**
     * Construye el servicio con el repositorio de consulta.
     *
     * @param inventoryQueryRepository repositorio de consulta de inventarios
     */
    public InventoryQueryService(
            InventoryQueryRepository inventoryQueryRepository
    ) {
        if (inventoryQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Inventory query repository must not be null."
            );
        }

        this.inventoryQueryRepository = inventoryQueryRepository;
    }

    /**
     * Obtiene los inventarios visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return inventarios autorizados
     */
    public List<Inventory> findAccessibleInventory(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        List<Inventory> inventories =
                inventoryQueryRepository.findAll();

        if (hasGlobalAccess(requestedBy)) {
            return List.copyOf(inventories);
        }

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

        throw new IllegalStateException(
                "Buyer is not authorized to query inventory."
        );
    }

    /**
     * Valida que el usuario exista y esté activo.
     */
    private void validateActiveRequester(User requestedBy) {
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
     * El administrador y el supervisor pueden consultar
     * todos los inventarios para administración y monitoreo.
     */
    private boolean hasGlobalAccess(User user) {
        return user.getRole() == SystemRole.ADMINISTRATOR
                || user.getRole() == SystemRole.SUPERVISOR;
    }
}