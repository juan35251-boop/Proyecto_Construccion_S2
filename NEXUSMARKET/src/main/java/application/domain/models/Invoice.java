package application.domain.models;

import application.domain.valueobjects.OrderStatus;

import java.util.List;

/**
 * Representa la factura asociada a una orden de NexusMarket.
 *
 * Una factura solamente puede generarse cuando la orden ya no se encuentra
 * pendiente de pago. Desde la orden relacionada se puede consultar el
 * comprador y los productos incluidos en la compra.
 *
 * Esta clase conserva la relación con la orden, pero no duplica la
 * información del comprador ni de los elementos comprados.
 */
public class Invoice {

    /**
     * Orden a la que pertenece la factura.
     *
     * La orden asociada no puede reemplazarse después de crear la factura.
     */
    private final Order order;

    /**
     * Crea una factura asociada a una orden.
     *
     * La orden debe existir y no puede encontrarse en estado
     * {@link OrderStatus#PENDING_PAYMENT}.
     *
     * @param order orden asociada a la factura
     *
     * @throws IllegalArgumentException si la orden es nula
     * @throws IllegalStateException si la orden todavía está pendiente
     *                               de pago
     */
    public Invoice(Order order) {
        validateOrder(order);
        validatePaidOrder(order);

        this.order = order;
    }

    /**
     * Obtiene la orden asociada a la factura.
     *
     * @return la orden facturada
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Obtiene el comprador propietario de la orden.
     *
     * La información se obtiene directamente desde la orden relacionada.
     *
     * @return el comprador asociado a la factura
     */
    public Buyer getBuyer() {
        return order.getBuyer();
    }

    /**
     * Obtiene los elementos incluidos en la orden facturada.
     *
     * La lista se obtiene directamente mediante la orden asociada.
     *
     * @return los elementos de la orden
     */
    public List<OrderItem> getItems() {
        return order.getItems();
    }

    /**
     * Determina si la factura pertenece a la orden recibida.
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
     * Valida que la factura esté relacionada con una orden.
     *
     * @param order orden que se desea validar
     *
     * @throws IllegalArgumentException si la orden es nula
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Invoice must be associated with an order."
            );
        }
    }

    /**
     * Valida que la orden ya haya superado el estado pendiente de pago.
     *
     * De acuerdo con la regla implementada, la factura no puede generarse
     * mientras la orden se encuentre en
     * {@link OrderStatus#PENDING_PAYMENT}.
     *
     * @param order orden cuyo estado se desea validar
     *
     * @throws IllegalStateException si la orden está pendiente de pago
     */
    private void validatePaidOrder(Order order) {
        if (order.getStatus() == OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException(
                    "An invoice cannot be generated before payment."
            );
        }
    }
}