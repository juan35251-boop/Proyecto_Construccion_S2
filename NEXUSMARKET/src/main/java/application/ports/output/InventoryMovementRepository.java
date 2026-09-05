package application.ports.output;

import application.domain.models.InventoryMovement;

/**
 * Puerto de salida que permite almacenar los movimientos
 * realizados sobre un inventario.
 *
 * Cada movimiento funciona como evidencia de una operación,
 * por ejemplo: ingreso, reserva, ajuste, venta o devolución.
 */
public interface InventoryMovementRepository {

    /**
     * Guarda un movimiento de inventario.
     *
     * @param movement movimiento que se desea registrar
     */
    void save(InventoryMovement movement);
}