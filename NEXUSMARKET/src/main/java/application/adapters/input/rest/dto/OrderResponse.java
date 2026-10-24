package application.adapters.input.rest.dto;

import application.domain.models.Order;
import application.domain.valueobjects.OrderStatus;

import java.util.List;

/**
 * Respuesta utilizada para exponer un pedido
 * mediante la API REST.
 *
 * @param orderId identificador del pedido
 * @param buyerIdentification identificación del comprador
 * @param status estado actual del pedido
 * @param containsPhysicalProducts indica si necesita despacho
 * @param items productos incluidos en el pedido
 */
public record OrderResponse(
        String orderId,
        String buyerIdentification,
        OrderStatus status,
        boolean containsPhysicalProducts,
        List<OrderItemResponse> items
) {

    /**
     * Convierte un pedido del dominio
     * en una respuesta de la API.
     *
     * @param order pedido que será convertido
     * @return respuesta del pedido
     */
    public static OrderResponse from(
            Order order
    ) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Order must not be null."
            );
        }

        if (order.getBuyer() == null) {
            throw new IllegalStateException(
                    "Order must contain a buyer."
            );
        }

        List<OrderItemResponse> itemResponses =
                order.getItems()
                        .stream()
                        .map(OrderItemResponse::from)
                        .toList();

        return new OrderResponse(
                order.getOrderId(),
                order.getBuyer().getIdentification(),
                order.getStatus(),
                order.containsPhysicalProducts(),
                itemResponses
        );
    }
}