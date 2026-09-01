package application.domain.valueobjects;

/**
 * Representa el estado comercial de un comprador en NexusMarket.
 *
 * Este estado determina si el comprador está autorizado comercialmente
 * para realizar compras. Es independiente del estado general del usuario,
 * representado por {@link UserStatus}.
 */
public enum BuyerStatus {

    /**
     * Indica que el comprador está habilitado comercialmente
     * para realizar compras.
     */
    ACTIVE,

    /**
     * Indica que el comprador está suspendido comercialmente
     * y no puede realizar compras.
     */
    SUSPENDED
}