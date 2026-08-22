package application.domain.models;

import application.domain.valueobjects.OrderStatus;

public class Return {

    private final Order order;
    private final Buyer buyer;

    public Return(Order order, Buyer buyer) {
        validateOrder(order);
        validateBuyer(buyer);
        validateOrderOwner(order, buyer);
        validateEligibleOrder(order);

        this.order = order;
        this.buyer = buyer;
    }

    public Order getOrder() {
        return order;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public boolean belongsTo(Order order) {
        return this.order == order;
    }

    public boolean wasRequestedBy(Buyer buyer) {
        return this.buyer == buyer;
    }

    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Return must be associated with an order."
            );
        }
    }

    private void validateBuyer(Buyer buyer) {
        if (buyer == null) {
            throw new IllegalArgumentException(
                    "Return must be requested by a buyer."
            );
        }
    }

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