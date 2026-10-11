package application.services;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.InventoryMovementQueryUseCase;
import application.ports.output.InventoryMovementQueryRepository;

import java.util.List;

/**
 * Servicio encargado de consultar movimientos de inventario.
 *
 * Implementa el puerto de entrada
 * InventoryMovementQueryUseCase, permitiendo consultar
 * movimientos sin depender directamente de esta implementación.
 *
 * Permisos:
 *
 * - Administradores y supervisores consultan todos.
 * - Vendedores consultan movimientos de sus productos
 *   y bodegas.
 * - Operadores logísticos consultan movimientos
 *   del Marketplace.
 * - Compradores no pueden consultar movimientos.
 */
public class InventoryMovementQueryService
        implements InventoryMovementQueryUseCase {

    private final InventoryMovementQueryRepository
            queryRepository;

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
    @Override
    public List<InventoryMovement> findAccessibleMovements(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        List<InventoryMovement> movements =
                queryRepository.findAll();

        /*
         * Administradores y supervisores tienen
         * acceso global de lectura.
         */
        if (hasGlobalAccess(requestedBy)) {
            return List.copyOf(movements);
        }

        /*
         * El vendedor consulta los movimientos
         * correspondientes a sus productos y bodegas.
         */
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

        /*
         * El operador logístico solamente consulta
         * movimientos de bodegas del Marketplace.
         */
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
     * Comprueba si el movimiento corresponde
     * a un producto y una bodega administrados
     * por el vendedor.
     *
     * @param movement movimiento consultado
     * @param seller vendedor que realiza la consulta
     * @return true si el movimiento pertenece al vendedor
     */
    private boolean belongsToSeller(
            InventoryMovement movement,
            Seller seller
    ) {
        Inventory inventory =
                movement.getInventory();

        return seller.managesProduct(
                inventory.getProduct()
        ) && seller.managesWarehouse(
                inventory.getWarehouse()
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
                    "Only active users can query inventory movements."
            );
        }
    }

    /**
     * Indica si el usuario tiene acceso global
     * de lectura.
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