package application.domain.valueobjects;

/**
 * Representa los tipos de movimientos que pueden registrarse sobre
 * un inventario de NexusMarket.
 *
 * El tipo determina el significado de la cantidad registrada en un
 * {@link application.domain.models.InventoryMovement}. La cantidad siempre
 * se almacena como un valor positivo; el tipo indica si corresponde a una
 * entrada, reserva, salida, ajuste o devolución.
 */
public enum InventoryMovementType {

    /**
     * Representa la entrada de nuevas unidades al inventario.
     */
    ENTRY,

    /**
     * Representa la reserva de unidades disponibles para una operación
     * de compra.
     */
    RESERVATION,

    /**
     * Representa la salida definitiva de unidades como consecuencia
     * de una venta.
     */
    SALE_EXIT,

    /**
     * Representa una corrección de la cantidad registrada en el inventario.
     *
     * Puede utilizarse después de una revisión o conteo de existencias.
     */
    ADJUSTMENT,

    /**
     * Representa el ingreso de unidades provenientes de una devolución.
     *
     * La condición del producto devuelto debe verificarse antes de
     * reincorporarlo como inventario disponible.
     */
    RETURN
}