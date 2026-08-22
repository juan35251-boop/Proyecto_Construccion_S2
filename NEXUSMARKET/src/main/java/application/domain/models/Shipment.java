package application.domain.models;

import application.domain.valueobjects.OrderStatus;

public class Shipment {

    private final Order order;

    public Shipment(Order order) {
        validateOrder(order);
        validatePhysicalOrder(order);
        validatePaidOrder(order);

        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public boolean belongsTo(Order order) {
        return this.order == order;
    }

    public void dispatch() {
        order.dispatch();
    }

    public void confirmDelivery() {
        order.markAsDelivered();
    }

    public boolean isDispatched() {
        return order.getStatus() == OrderStatus.DISPATCHED
                || order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.FINALIZED;
    }

    public boolean isDelivered() {
        return order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.FINALIZED;
    }

    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Shipment must be associated with an order."
            );
        }
    }

    private void validatePhysicalOrder(Order order) {
        if (!order.containsPhysicalProducts()) {
            throw new IllegalStateException(
                    "Digital-only orders do not require shipment."
            );
        }
    }

    private void validatePaidOrder(Order order) {
        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException(
                    "Shipment can only be created for a paid order."
            );
        }
    }
}