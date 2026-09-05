package application.ports.output;

import application.domain.models.InventoryMovement;

import java.util.List;

/**
 * Puerto de salida para consultar movimientos de inventario.
 */
public interface InventoryMovementQueryRepository {

    /**
     * Obtiene todos los movimientos registrados.
     *
     * @return lista de movimientos de inventario
     */
    List<InventoryMovement> findAll();
}