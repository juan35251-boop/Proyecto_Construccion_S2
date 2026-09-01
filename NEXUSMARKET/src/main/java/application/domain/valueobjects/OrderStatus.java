package application.domain.valueobjects;

/**
 * Representa los estados posibles de una orden durante el proceso
 * de compra en NexusMarket.
 *
 * Las transiciones entre estos estados son controladas por la clase
 * {@link application.domain.models.Order}, evitando que una orden avance
 * en un orden incorrecto.
 */
public enum OrderStatus {

    /**
     * Indica que la orden fue creada, pero su pago todavía
     * no ha sido confirmado.
     */
    PENDING_PAYMENT,

    /**
     * Indica que el pago de la orden fue confirmado.
     *
     * Una orden física pagada puede iniciar su proceso de despacho.
     * Una orden digital pagada puede marcarse directamente como entregada.
     */
    PAID,

    /**
     * Indica que una orden con productos físicos fue despachada.
     *
     * Las órdenes completamente digitales no utilizan este estado.
     */
    DISPATCHED,

    /**
     * Indica que la orden fue entregada al comprador.
     *
     * En una orden física, este estado se alcanza después del despacho.
     * En una orden digital, se alcanza después del pago.
     */
    DELIVERED,

    /**
     * Indica que todo el proceso de la orden terminó.
     *
     * Una orden solamente puede finalizarse después de haber sido entregada.
     */
    FINALIZED
}