package application.domain.models;

import application.domain.valueobjects.OrderStatus;

import java.util.Objects;
import java.util.UUID;

/**
 * Representa el proceso de envío asociado
 * a un pedido físico o mixto.
 *
 * Un envío solamente puede crearse para un pedido
 * pagado que contenga al menos un producto físico.
 *
 * El avance del envío se refleja directamente
 * mediante el estado del pedido asociado.
 */
public class Shipment {

    /**
     * Identificador único del envío.
     */
    private final String shipmentId;

    /**
     * Pedido físico o mixto asociado.
     */
    private final Order order;

    /**
     * Crea un nuevo envío para un pedido físico pagado.
     *
     * @param order pedido asociado al envío
     */
    public Shipment(Order order) {
        validateOrder(order);
        validatePhysicalOrder(order);
        validatePaidOrder(order);

        this.shipmentId =
                UUID.randomUUID().toString();

        this.order = order;
    }

    /**
     * Reconstruye un envío recuperado desde persistencia.
     *
     * Este constructor no exige que el pedido continúe
     * en PAID porque el envío recuperado puede encontrarse
     * despachado, entregado o finalizado.
     *
     * @param shipmentId identificador persistido
     * @param order pedido asociado
     */
    public Shipment(
            String shipmentId,
            Order order
    ) {
        validateText(
                shipmentId,
                "Shipment identifier"
        );

        validateOrder(order);
        validatePhysicalOrder(order);
        validateRestorableOrderStatus(order);

        this.shipmentId = shipmentId;
        this.order = order;
    }

    /**
     * Obtiene el identificador único del envío.
     *
     * @return identificador del envío
     */
    public String getShipmentId() {
        return shipmentId;
    }

    /**
     * Obtiene el pedido asociado al envío.
     *
     * @return pedido relacionado
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Determina si el envío pertenece al pedido recibido.
     *
     * Primero compara las referencias. Si son diferentes,
     * compara los identificadores persistentes.
     *
     * @param otherOrder pedido que se desea comparar
     * @return true si corresponde al mismo pedido
     */
    public boolean belongsTo(Order otherOrder) {
        if (order == otherOrder) {
            return true;
        }

        if (otherOrder == null) {
            return false;
        }

        String currentOrderId =
                order.getOrderId();

        String otherOrderId =
                otherOrder.getOrderId();

        return currentOrderId != null
                && currentOrderId.equals(otherOrderId);
    }

    /**
     * Despacha el pedido asociado.
     *
     * El pedido cambia desde PAID hasta DISPATCHED.
     */
    public void dispatch() {
        order.dispatch();
    }

    /**
     * Confirma la entrega física del pedido.
     *
     * El pedido cambia desde DISPATCHED hasta DELIVERED.
     */
    public void confirmDelivery() {
        order.markAsDelivered();
    }

    /**
     * Indica si el envío fue despachado.
     *
     * @return true si está despachado, entregado o finalizado
     */
    public boolean isDispatched() {
        return order.getStatus()
                == OrderStatus.DISPATCHED
                || order.getStatus()
                == OrderStatus.DELIVERED
                || order.getStatus()
                == OrderStatus.FINALIZED;
    }

    /**
     * Indica si el envío fue entregado.
     *
     * @return true si está entregado o finalizado
     */
    public boolean isDelivered() {
        return order.getStatus()
                == OrderStatus.DELIVERED
                || order.getStatus()
                == OrderStatus.FINALIZED;
    }

    /**
     * Valida que exista el pedido.
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Shipment must be associated with an order."
            );
        }
    }

    /**
     * Valida que el pedido contenga productos físicos.
     */
    private void validatePhysicalOrder(Order order) {
        if (!order.containsPhysicalProducts()) {
            throw new IllegalStateException(
                    "Digital-only orders do not require shipment."
            );
        }
    }

    /**
     * Valida que un envío nuevo se cree para
     * un pedido pagado.
     */
    private void validatePaidOrder(Order order) {
        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException(
                    "Shipment can only be created for a paid order."
            );
        }
    }

    /**
     * Valida el estado de un pedido recuperado
     * desde persistencia.
     */
    private void validateRestorableOrderStatus(
            Order order
    ) {
        OrderStatus status = order.getStatus();

        if (status != OrderStatus.PAID
                && status != OrderStatus.DISPATCHED
                && status != OrderStatus.DELIVERED
                && status != OrderStatus.FINALIZED) {
            throw new IllegalStateException(
                    "Persisted shipment has an invalid order status."
            );
        }
    }

    /**
     * Valida un texto obligatorio.
     */
    private void validateText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }
    }

    /**
     * Compara envíos mediante su identificador.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Shipment shipment)) {
            return false;
        }

        return shipmentId.equals(
                shipment.shipmentId
        );
    }

    /**
     * Genera el código hash mediante
     * el identificador del envío.
     */
    @Override
    public int hashCode() {
        return Objects.hash(shipmentId);
    }
}