package application.domain.valueobjects;

/**
 * Representa la condición en la que se encuentra un inventario.
 *
 * La condición permite determinar si las existencias pueden utilizarse
 * normalmente o si están dañadas. Se utiliza en la clase
 * {@link application.domain.models.Inventory}.
 */
public enum InventoryCondition {

    /**
     * Indica que el inventario se encuentra disponible para ser reservado.
     *
     * Este valor no garantiza que existan unidades; la cantidad disponible
     * se controla por separado en el inventario.
     */
    AVAILABLE,

    /**
     * Indica que el inventario está dañado y no puede reservarse.
     */
    DAMAGED
}
