package application.domain.models;

import application.domain.valueobjects.OrderStatus;

/**
 * Representa el proceso de envío asociado a una orden.
 *
 * Un envío solamente puede crearse para una orden pagada que contenga
 * al menos un producto físico. Las órdenes completamente digitales no
 * necesitan un proceso de envío.
 *
 * Esta clase no mantiene un estado propio. El avance del envío se refleja
 * directamente mediante los estados de la {@link Order} asociada.
 */
public class Shipment {

    /**
     * Orden física o mixta asociada al envío.
     */
    private final Order order;

    /**
     * Crea un envío para una orden pagada que contenga productos físicos.
     *
     * La orden debe existir, contener al menos un producto físico y
     * encontrarse exactamente en estado {@link OrderStatus#PAID}.
     *
     * @param order orden asociada al envío
     *
     * @throws IllegalArgumentException si la orden es nula
     * @throws IllegalStateException si la orden contiene únicamente
     *                               productos digitales
     * @throws IllegalStateException si la orden no está pagada
     */
    public Shipment(Order order) {
        validateOrder(order);
        validatePhysicalOrder(order);
        validatePaidOrder(order);

        this.order = order;
    }

    /**
     * Obtiene la orden asociada al envío.
     *
     * @return la orden relacionada
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Determina si el envío pertenece a la orden recibida.
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
     * Despacha la orden asociada al envío.
     *
     * La operación se delega a {@link Order#dispatch()}, que comprueba
     * que la orden esté pagada y que contenga productos físicos.
     *
     * @throws IllegalStateException si la orden no cumple las condiciones
     *                               necesarias para ser despachada
     */
    public void dispatch() {
        order.dispatch();
    }

    /**
     * Confirma la entrega de la orden asociada.
     *
     * La operación se delega a {@link Order#markAsDelivered()}, que exige
     * que una orden física haya sido despachada previamente.
     *
     * @throws IllegalStateException si la orden todavía no está despachada
     */
    public void confirmDelivery() {
        order.markAsDelivered();
    }

    /**
     * Indica si el envío ya fue despachado.
     *
     * También devuelve {@code true} para órdenes entregadas o finalizadas,
     * porque esos estados solamente pueden alcanzarse después del despacho
     * de una orden física.
     *
     * @return {@code true} si la orden está despachada, entregada
     *         o finalizada; de lo contrario, {@code false}
     */
    public boolean isDispatched() {
        return order.getStatus() == OrderStatus.DISPATCHED
                || order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.FINALIZED;
    }

    /**
     * Indica si el envío ya fue entregado.
     *
     * También devuelve {@code true} si la orden está finalizada, porque una
     * orden solamente puede finalizarse después de su entrega.
     *
     * @return {@code true} si la orden está entregada o finalizada;
     *         de lo contrario, {@code false}
     */
    public boolean isDelivered() {
        return order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.FINALIZED;
    }

    /**
     * Valida que el envío esté relacionado con una orden.
     *
     * @param order orden que se desea validar
     *
     * @throws IllegalArgumentException si la orden es nula
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Shipment must be associated with an order."
            );
        }
    }

    /**
     * Valida que la orden contenga al menos un producto físico.
     *
     * Las órdenes completamente digitales no requieren transporte.
     *
     * @param order orden que se desea validar
     *
     * @throws IllegalStateException si la orden solamente contiene
     *                               productos digitales
     */
    private void validatePhysicalOrder(Order order) {
        if (!order.containsPhysicalProducts()) {
            throw new IllegalStateException(
                    "Digital-only orders do not require shipment."
            );
        }
    }

    /**
     * Valida que la orden se encuentre pagada al crear el envío.
     *
     * @param order orden cuyo estado se desea validar
     *
     * @throws IllegalStateException si la orden no está exactamente
     *                               en estado {@link OrderStatus#PAID}
     */
    private void validatePaidOrder(Order order) {
        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException(
                    "Shipment can only be created for a paid order."
            );
        }
    }
}