package application.domain.models;

import application.domain.valueobjects.OrderStatus;

import java.util.List;

public class Invoice {

    private final Order order;

    public Invoice(Order order) {
        validateOrder(order);
        validatePaidOrder(order);

        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public Buyer getBuyer() {
        return order.getBuyer();
    }

    public List<OrderItem> getItems() {
        return order.getItems();
    }

    public boolean belongsTo(Order order) {
        return this.order == order;
    }

    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Invoice must be associated with an order."
            );
        }
    }

    private void validatePaidOrder(Order order) {
        if (order.getStatus() == OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException(
                    "An invoice cannot be generated before payment."
            );
        }
    }
}