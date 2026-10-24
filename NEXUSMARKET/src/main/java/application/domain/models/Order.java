package application.domain.models;

import application.domain.valueobjects.OrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa un pedido creado a partir de un carrito.
 *
 * Cada pedido posee un identificador estable, un comprador,
 * productos confirmados y un estado dentro del proceso comercial.
 *
 * Los pedidos físicos deben ser despachados antes de entregarse.
 * Los pedidos digitales pueden entregarse después del pago.
 */
public class Order {

    /**
     * Identificador único del pedido.
     */
    private final String orderId;

    /**
     * Comprador propietario.
     */
    private final Buyer buyer;

    /**
     * Productos confirmados.
     */
    private final List<OrderItem> items;

    /**
     * Estado actual.
     */
    private OrderStatus status;

    /**
     * Crea un pedido nuevo desde un carrito.
     *
     * El pedido comienza en estado PENDING_PAYMENT.
     *
     * @param cart carrito confirmado
     */
    public Order(
            Cart cart
    ) {
        validateCart(cart);
        validateBuyerCanPurchase(
                cart.getBuyer()
        );

        this.orderId =
                UUID.randomUUID().toString();

        this.buyer = cart.getBuyer();
        this.items = copyCartItems(cart);
        this.status =
                OrderStatus.PENDING_PAYMENT;
    }

    /**
     * Reconstruye un pedido almacenado.
     *
     * Este constructor es utilizado por la persistencia JPA.
     * No vuelve a validar el estado comercial actual del comprador,
     * porque un pedido histórico debe poder consultarse aunque
     * posteriormente el usuario haya sido bloqueado.
     *
     * @param orderId identificador del pedido
     * @param buyer comprador propietario
     * @param items productos confirmados
     * @param status estado almacenado
     */
    public Order(
            String orderId,
            Buyer buyer,
            List<OrderItem> items,
            OrderStatus status
    ) {
        validateOrderId(orderId);
        validateBuyer(buyer);
        validateItems(items);
        validateStatus(status);

        this.orderId = orderId;
        this.buyer = buyer;
        this.items = new ArrayList<>(items);
        this.status = status;
    }

    /**
     * Obtiene el identificador único.
     *
     * @return identificador del pedido
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * Obtiene el comprador propietario.
     *
     * @return comprador
     */
    public Buyer getBuyer() {
        return buyer;
    }

    /**
     * Obtiene una copia de los productos.
     *
     * @return productos confirmados
     */
    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    /**
     * Obtiene el estado actual.
     *
     * @return estado del pedido
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Indica si el pedido está finalizado.
     *
     * @return true si está finalizado
     */
    public boolean isFinalized() {
        return status == OrderStatus.FINALIZED;
    }

    /**
     * Determina si el pedido contiene
     * al menos un producto físico.
     *
     * @return true si requiere despacho físico
     */
    public boolean containsPhysicalProducts() {
        for (OrderItem item : items) {
            if (item.getProduct().isPhysical()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Marca un pedido pendiente como pagado.
     */
    public void markAsPaid() {
        requireStatus(
                OrderStatus.PENDING_PAYMENT,
                "Only an order pending payment can be marked as paid."
        );

        status = OrderStatus.PAID;
    }

    /**
     * Despacha un pedido físico pagado.
     */
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

    /**
     * Marca el pedido como entregado.
     *
     * Un pedido físico debe estar despachado.
     * Un pedido digital debe estar pagado.
     */
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

    /**
     * Finaliza un pedido entregado.
     */
    public void finalizeOrder() {
        requireStatus(
                OrderStatus.DELIVERED,
                "Only a delivered order can be finalized."
        );

        status = OrderStatus.FINALIZED;
    }

    /**
     * Copia los productos del carrito.
     *
     * Las cantidades quedan independientes del carrito.
     */
    private List<OrderItem> copyCartItems(
            Cart cart
    ) {
        List<OrderItem> orderItems =
                new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Product product =
                    cartItem.getProduct();

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

    /**
     * Valida el carrito utilizado para crear el pedido.
     */
    private void validateCart(
            Cart cart
    ) {
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

    /**
     * Valida que el comprador pueda confirmar compras.
     */
    private void validateBuyerCanPurchase(
            Buyer buyer
    ) {
        validateBuyer(buyer);

        if (!buyer.canPurchase()) {
            throw new IllegalStateException(
                    "Buyer is not authorized to confirm purchases."
            );
        }
    }

    /**
     * Valida el identificador del pedido.
     */
    private void validateOrderId(
            String orderId
    ) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException(
                    "Order identifier must not be empty."
            );
        }
    }

    /**
     * Valida el comprador.
     */
    private void validateBuyer(
            Buyer buyer
    ) {
        if (buyer == null) {
            throw new IllegalArgumentException(
                    "Order must belong to a buyer."
            );
        }
    }

    /**
     * Valida los productos reconstruidos.
     */
    private void validateItems(
            List<OrderItem> items
    ) {
        if (items == null) {
            throw new IllegalArgumentException(
                    "Order items must not be null."
            );
        }

        if (items.isEmpty()) {
            throw new IllegalStateException(
                    "Order must contain at least one item."
            );
        }

        if (items.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "Order items must not contain null values."
            );
        }
    }

    /**
     * Valida el estado.
     */
    private void validateStatus(
            OrderStatus status
    ) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Order status must not be null."
            );
        }
    }

    /**
     * Exige un estado específico antes
     * de realizar una transición.
     */
    private void requireStatus(
            OrderStatus expectedStatus,
            String errorMessage
    ) {
        if (status != expectedStatus) {
            throw new IllegalStateException(
                    errorMessage
            );
        }
    }

    /**
     * Dos pedidos son iguales si poseen
     * el mismo identificador.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Order order)) {
            return false;
        }

        return orderId.equals(order.orderId);
    }

    /**
     * Genera el código hash mediante orderId.
     */
    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}