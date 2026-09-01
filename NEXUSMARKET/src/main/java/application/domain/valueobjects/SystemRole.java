package application.domain.valueobjects;

/**
 * Representa los roles que puede tener un usuario dentro de NexusMarket.
 *
 * Cada clase concreta que hereda de
 * {@link application.domain.models.User} devuelve uno de estos valores
 * mediante la implementación del método {@code getRole()}.
 *
 * Los roles permiten identificar las responsabilidades y autorizaciones
 * de cada tipo de usuario.
 */
public enum SystemRole {

    /**
     * Representa al comprador que puede administrar su carrito,
     * confirmar compras y solicitar devoluciones.
     */
    BUYER,

    /**
     * Representa al vendedor que registra productos y administra
     * sus bodegas e inventarios.
     */
    SELLER,

    /**
     * Representa al usuario encargado de apoyar la gestión logística,
     * los movimientos de inventario y los procesos de envío.
     */
    LOGISTICS_OPERATOR,

    /**
     * Representa al usuario encargado de las operaciones administrativas,
     * como el procesamiento de reembolsos.
     */
    ADMINISTRATOR,

    /**
     * Representa al usuario encargado de supervisar y hacer seguimiento
     * a los procesos del sistema.
     */
    SUPERVISOR
}