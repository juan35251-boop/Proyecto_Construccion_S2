package application.domain.valueobjects;

/**
 * Representa los tipos de productos que pueden comercializarse
 * dentro de NexusMarket.
 *
 * El tipo determina si el producto necesita almacenamiento y envío físico
 * o si puede entregarse mediante un proceso digital.
 */
public enum ProductType {

    /**
     * Representa un producto físico.
     *
     * Este tipo de producto puede requerir inventario, almacenamiento
     * en una bodega, despacho y entrega física.
     */
    PHYSICAL,

    /**
     * Representa un producto digital.
     *
     * Este tipo de producto no necesita despacho físico y puede marcarse
     * como entregado después de confirmar su pago.
     */
    DIGITAL
}