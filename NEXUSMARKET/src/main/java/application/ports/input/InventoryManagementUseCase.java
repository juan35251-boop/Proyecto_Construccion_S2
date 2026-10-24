package application.ports.input;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.User;
import application.domain.valueobjects.InventoryCondition;

/**
 * Puerto de entrada que define el caso de uso
 * para administrar el inventario de NexusMarket.
 *
 * Permite modificar cantidades y condiciones del inventario,
 * conservando evidencia de los movimientos realizados.
 */
public interface InventoryManagementUseCase {

    /**
     * Registra el ingreso de unidades utilizando identificadores.
     *
     * @param performedByIdentification identificación del usuario
     * @param inventoryId identificador del inventario
     * @param quantity cantidad que ingresa
     * @return movimiento generado
     */
    InventoryMovement registerEntry(
            String performedByIdentification,
            String inventoryId,
            int quantity
    );

    /**
     * Reserva unidades utilizando identificadores.
     *
     * @param performedByIdentification identificación del usuario
     * @param inventoryId identificador del inventario
     * @param quantity cantidad que se desea reservar
     * @return movimiento generado
     */
    InventoryMovement reserveQuantity(
            String performedByIdentification,
            String inventoryId,
            int quantity
    );

    /**
     * Ajusta la cantidad disponible utilizando identificadores.
     *
     * @param performedByIdentification identificación del usuario
     * @param inventoryId identificador del inventario
     * @param newQuantity nueva cantidad disponible
     * @return movimiento generado
     */
    InventoryMovement adjustQuantity(
            String performedByIdentification,
            String inventoryId,
            int newQuantity
    );

    /**
     * Registra una devolución utilizando identificadores.
     *
     * @param performedByIdentification identificación del usuario
     * @param inventoryId identificador del inventario
     * @param quantity cantidad devuelta
     * @return movimiento generado
     */
    InventoryMovement registerReturn(
            String performedByIdentification,
            String inventoryId,
            int quantity
    );

    /**
     * Cambia la condición del inventario utilizando identificadores.
     *
     * @param performedByIdentification identificación del usuario
     * @param inventoryId identificador del inventario
     * @param newCondition nueva condición
     */
    void changeCondition(
            String performedByIdentification,
            String inventoryId,
            InventoryCondition newCondition
    );

    /**
     * Registra el ingreso de nuevas unidades.
     *
     * Este método se conserva para mantener compatibilidad
     * con las pruebas y componentes existentes.
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
     * Ajusta la cantidad disponible.
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