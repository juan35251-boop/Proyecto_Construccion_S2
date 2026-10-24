package application.adapters.input.rest.dto;

import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.valueobjects.OrderStatus;

/**
 * Representación REST de un envío.
 *
 * @param shipmentId identificador del envío
 * @param orderId identificador del pedido
 * @param buyerIdentification identificación del comprador
 * @param orderStatus estado actual del pedido
 * @param dispatched indica si el envío fue despachado
 * @param delivered indica si el envío fue entregado
 */
public record ShipmentResponse(
        String shipmentId,
        String orderId,
        String buyerIdentification,
        OrderStatus orderStatus,
        boolean dispatched,
        boolean delivered
) {

    /**
     * Convierte un envío del dominio
     * en una respuesta REST.
     *
     * @param shipment envío que será convertido
     * @return respuesta del envío
     */
    public static ShipmentResponse from(
            Shipment shipment
    ) {
        if (shipment == null) {
            throw new IllegalArgumentException(
                    "Shipment must not be null."
            );
        }

        Order order = shipment.getOrder();

        if (order == null) {
            throw new IllegalStateException(
                    "Shipment must contain an order."
            );
        }

        if (order.getBuyer() == null) {
            throw new IllegalStateException(
                    "Shipment order must contain a buyer."
            );
        }

        return new ShipmentResponse(
                shipment.getShipmentId(),
                order.getOrderId(),
                order.getBuyer().getIdentification(),
                order.getStatus(),
                shipment.isDispatched(),
                shipment.isDelivered()
        );
    }
}