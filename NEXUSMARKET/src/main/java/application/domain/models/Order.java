package application.domain.models;

import application.domain.valueobjects.OrderStatus;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private final Buyer buyer;
    private final List<OrderItem> items;
    private OrderStatus status;

    public Order(Cart cart) {
        validateCart(cart);
        validateBuyerCanPurchase(cart.getBuyer());

        this.buyer = cart.getBuyer();
        this.items = copyCartItems(cart);
        this.status = OrderStatus.PENDING_PAYMENT;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public boolean isFinalized() {
        return status == OrderStatus.FINALIZED;
    }

    public boolean containsPhysicalProducts() {
        for (OrderItem item : items) {
            if (item.getProduct().isPhysical()) {
                return true;
            }
        }

        return false;
    }

    public void markAsPaid() {
        requireStatus(
                OrderStatus.PENDING_PAYMENT,
                "Only an order pending payment can be marked as paid."
        );

        status = OrderStatus.PAID;
    }

    public void dispatch() {
        requireStatus(
                OrderStatus.PAID,
                "Only a paid order can be dispatched."
        );

        if (!containsPhysicalProducts()) {
            throw new IllegalStateException(
                    "A digital order does not require physical dispatch."
            );
        }

        status = OrderStatus.DISPATCHED;
    }

    public void markAsDelivered() {
        if (containsPhysicalProducts()) {
            requireStatus(
                    OrderStatus.DISPATCHED,
                    "A physical order must be dispatched before delivery."
            );
        } else {
            requireStatus(
                    OrderStatus.PAID,
                    "A digital order must be paid before delivery."
            );
        }

        status = OrderStatus.DELIVERED;
    }

    public void finalizeOrder() {
        requireStatus(
                OrderStatus.DELIVERED,
                "Only a delivered order can be finalized."
        );

        status = OrderStatus.FINALIZED;
    }

    private List<OrderItem> copyCartItems(Cart cart) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            if (!product.isPublished()) {
                throw new IllegalStateException(
                        "All products must be published when confirming the order."
                );
            }

            orderItems.add(
                    new OrderItem(
                            product,
                            cartItem.getQuantity()
                    )
            );
        }

        return orderItems;
    }

    private void validateCart(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException(
                    "Order must be created from a cart."
            );
        }

        if (cart.isEmpty()) {
            throw new IllegalStateException(
                    "An empty cart cannot produce an order."
            );
        }
    }

    private void validateBuyerCanPurchase(Buyer buyer) {
        if (!buyer.canPurchase()) {
            throw new IllegalStateException(
                    "Buyer is not authorized to confirm purchases."
            );
        }
    }

    private void requireStatus(
            OrderStatus expectedStatus,
            String errorMessage
    ) {
        if (status != expectedStatus) {
            throw new IllegalStateException(errorMessage);
        }
    }
}
