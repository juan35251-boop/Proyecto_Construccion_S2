package application.domain.models;

import application.domain.valueobjects.OrderStatus;

/**
 * Representa una devolución solicitada por un comprador.
 *
 * Toda devolución debe estar asociada a una orden entregada o finalizada.
 * Además, solamente el comprador propietario de la orden puede solicitarla.
 *
 * En el alcance actual, la devolución representa el proceso relacionado
 * con la orden completa.
 */
public class Return {

    /**
     * Orden sobre la cual se solicita la devolución.
     */
    private final Order order;

    /**
     * Comprador que solicita la devolución.
     *
     * Debe ser el mismo comprador propietario de la orden.
     */
    private final Buyer buyer;

    /**
     * Crea una devolución para una orden.
     *
     * La orden y el comprador deben existir. El comprador debe ser el
     * propietario de la orden y esta debe encontrarse entregada
     * o finalizada.
     *
     * @param order orden que se desea devolver
     * @param buyer comprador que solicita la devolución
     *
     * @throws IllegalArgumentException si la orden es nula
     * @throws IllegalArgumentException si el comprador es nulo
     * @throws IllegalStateException si el comprador no es propietario
     *                               de la orden
     * @throws IllegalStateException si la orden no está entregada
     *                               ni finalizada
     */
    public Return(Order order, Buyer buyer) {
        validateOrder(order);
        validateBuyer(buyer);
        validateOrderOwner(order, buyer);
        validateEligibleOrder(order);

        this.order = order;
        this.buyer = buyer;
    }

    /**
     * Obtiene la orden asociada a la devolución.
     *
     * @return la orden que se desea devolver
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Obtiene el comprador que solicitó la devolución.
     *
     * @return el comprador solicitante
     */
    public Buyer getBuyer() {
        return buyer;
    }

    /**
     * Determina si la devolución pertenece a la orden recibida.
     *
     * La comparación se realiza por identidad, por lo que ambas referencias
     * deben apuntar a la misma instancia de {@link Order}.
     *
     * @param order orden que se desea comparar
     * @return {@code true} si corresponde a la misma instancia;
     *         de lo contrario, {@code false}
     */
    public boolean belongsTo(Order order) {
        return this.order == order;
    }

    /**
     * Determina si la devolución fue solicitada por el comprador recibido.
     *
     * La comparación se realiza por identidad, por lo que ambas referencias
     * deben apuntar a la misma instancia de {@link Buyer}.
     *
     * @param buyer comprador que se desea comparar
     * @return {@code true} si corresponde al comprador solicitante;
     *         de lo contrario, {@code false}
     */
    public boolean wasRequestedBy(Buyer buyer) {
        return this.buyer == buyer;
    }

    /**
     * Valida que la devolución esté relacionada con una orden.
     *
     * @param order orden que se desea validar
     *
     * @throws IllegalArgumentException si la orden es nula
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Return must be associated with an order."
            );
        }
    }

    /**
     * Valida que la devolución sea solicitada por un comprador.
     *
     * @param buyer comprador que se desea validar
     *
     * @throws IllegalArgumentException si el comprador es nulo
     */
    private void validateBuyer(Buyer buyer) {
        if (buyer == null) {
            throw new IllegalArgumentException(
                    "Return must be requested by a buyer."
            );
        }
    }

    /**
     * Valida que el comprador sea el propietario de la orden.
     *
     * La propiedad se comprueba comparando la identidad del comprador
     * registrado en la orden con el comprador solicitante.
     *
     * @param order orden que se desea devolver
     * @param buyer comprador que solicita la devolución
     *
     * @throws IllegalStateException si el comprador no es propietario
     *                               de la orden
     */
    private void validateOrderOwner(
            Order order,
            Buyer buyer
    ) {
        if (order.getBuyer() != buyer) {
            throw new IllegalStateException(
                    "Buyer can only return their own order."
            );
        }
    }

    /**
     * Valida que el estado de la orden permita solicitar una devolución.
     *
     * Solamente las órdenes entregadas o finalizadas son elegibles.
     *
     * @param order orden cuyo estado se desea validar
     *
     * @throws IllegalStateException si la orden no está entregada
     *                               ni finalizada
     */
    private void validateEligibleOrder(Order order) {
        boolean delivered =
                order.getStatus() == OrderStatus.DELIVERED;

        boolean finalized =
                order.getStatus() == OrderStatus.FINALIZED;

        if (!delivered && !finalized) {
            throw new IllegalStateException(
                    "Only delivered or finalized orders can be returned."
            );
        }
    }
}