package application.services;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.output.InventoryMovementQueryRepository;

import java.util.List;

/**
 * Servicio encargado de consultar movimientos de inventario.
 *
 * Permisos:
 *
 * - Administradores y supervisores consultan todos los movimientos.
 * - Vendedores consultan movimientos de sus productos y bodegas.
 * - Operadores logísticos consultan movimientos del Marketplace.
 * - Compradores no pueden consultar movimientos de inventario.
 */
public class InventoryMovementQueryService {

    private final InventoryMovementQueryRepository queryRepository;

    /**
     * Construye el servicio con el repositorio de consulta.
     *
     * @param queryRepository repositorio de movimientos
     */
    public InventoryMovementQueryService(
            InventoryMovementQueryRepository queryRepository
    ) {
        if (queryRepository == null) {
            throw new IllegalArgumentException(
                    "Inventory movement query repository must not be null."
            );
        }

        this.queryRepository = queryRepository;
    }

    /**
     * Obtiene los movimientos visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return movimientos autorizados
     */
    public List<InventoryMovement> findAccessibleMovements(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        List<InventoryMovement> movements =
                queryRepository.findAll();

        if (hasGlobalAccess(requestedBy)) {
            return List.copyOf(movements);
        }

        if (requestedBy instanceof Seller seller) {
            return movements.stream()
                    .filter(
                            movement ->
                                    belongsToSeller(
                                            movement,
                                            seller
                                    )
                    )
                    .toList();
        }

        if (requestedBy.getRole()
                == SystemRole.LOGISTICS_OPERATOR) {
            return movements.stream()
                    .filter(
                            movement ->
                                    movement.getInventory()
                                            .getWarehouse()
                                            .isMarketplaceWarehouse()
                    )
                    .toList();
        }

        throw new IllegalStateException(
                "User is not authorized to query inventory movements."
        );
    }

    /**
     * Comprueba si el movimiento corresponde a un producto
     * y una bodega administrados por el vendedor.
     */
    private boolean belongsToSeller(
            InventoryMovement movement,
            Seller seller
    ) {
        Inventory inventory = movement.getInventory();

        return seller.managesProduct(inventory.getProduct())
                && seller.managesWarehouse(
                        inventory.getWarehouse()
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
                    "Only active users can query inventory movements."
            );
        }
    }

    /**
     * Administradores y supervisores tienen acceso global de lectura.
     */
    private boolean hasGlobalAccess(User user) {
        return user.getRole() == SystemRole.ADMINISTRATOR
                || user.getRole() == SystemRole.SUPERVISOR;
    }
}