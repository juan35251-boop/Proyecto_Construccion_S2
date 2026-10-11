package application.ports.input;

import application.domain.models.InventoryMovement;
import application.domain.models.User;

import java.util.List;

/**
 * Puerto de entrada que define el caso de uso
 * para consultar movimientos de inventario.
 *
 * Los movimientos visibles dependen del rol,
 * los productos y las bodegas administradas
 * por el usuario solicitante.
 */
public interface InventoryMovementQueryUseCase {

    /**
     * Obtiene los movimientos de inventario
     * visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return lista de movimientos autorizados
     */
    List<InventoryMovement> findAccessibleMovements(
            User requestedBy
    );
}