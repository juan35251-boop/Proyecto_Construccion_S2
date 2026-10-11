package application.ports.input;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.User;
import application.domain.valueobjects.InventoryCondition;

/**
 * Puerto de entrada que define el caso de uso
 * para administrar el inventario de NexusMarket.
 *
 * Permite modificar las cantidades y condiciones
 * del inventario, conservando evidencia de los
 * movimientos realizados.
 */
public interface InventoryManagementUseCase {

    /**
     * Registra el ingreso de nuevas unidades.
     *
     * @param performedBy usuario que realiza la operación
     * @param inventory inventario que recibe las unidades
     * @param quantity cantidad que ingresa
     * @return movimiento generado
     */
    InventoryMovement registerEntry(
            User performedBy,
            Inventory inventory,
            int quantity
    );

    /**
     * Reserva unidades disponibles del inventario.
     *
     * @param performedBy usuario que realiza la reserva
     * @param inventory inventario afectado
     * @param quantity cantidad que se desea reservar
     * @return movimiento generado
     */
    InventoryMovement reserveQuantity(
            User performedBy,
            Inventory inventory,
            int quantity
    );

    /**
     * Ajusta la cantidad disponible a un nuevo valor.
     *
     * @param performedBy usuario que realiza el ajuste
     * @param inventory inventario que será ajustado
     * @param newQuantity nueva cantidad disponible
     * @return movimiento generado
     */
    InventoryMovement adjustQuantity(
            User performedBy,
            Inventory inventory,
            int newQuantity
    );

    /**
     * Registra el retorno de unidades al inventario.
     *
     * @param performedBy usuario que procesa la devolución
     * @param inventory inventario que recibe las unidades
     * @param quantity cantidad devuelta
     * @return movimiento generado
     */
    InventoryMovement registerReturn(
            User performedBy,
            Inventory inventory,
            int quantity
    );

    /**
     * Cambia la condición operativa del inventario.
     *
     * @param performedBy usuario que realiza el cambio
     * @param inventory inventario que será modificado
     * @param newCondition nueva condición
     */
    void changeCondition(
            User performedBy,
            Inventory inventory,
            InventoryCondition newCondition
    );
}