package application.domain.models;

import application.domain.valueobjects.OrderStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una orden de compra creada a partir de un carrito.
 *
 * La orden conserva el comprador, copia los productos y cantidades
 * presentes en el carrito y controla el avance del proceso mediante
 * {@link OrderStatus}.
 *
 * Las órdenes con productos físicos deben ser despachadas antes de
 * entregarse. Las órdenes completamente digitales pueden pasar
 * directamente de pagadas a entregadas.
 */
public class Order {

    /**
     * Comprador propietario de la orden.
     */
    private final Buyer buyer;

    /**
     * Elementos incluidos en la orden.
     *
     * Se crean a partir de los elementos que estaban en el carrito
     * al momento de confirmar la compra.
     */
    private final List<OrderItem> items;

    /**
     * Estado actual de la orden dentro del proceso de compra.
     */
    private OrderStatus status;

    /**
     * Crea una orden a partir de un carrito.
     *
     * El carrito debe existir, contener productos y pertenecer a un
     * comprador autorizado para realizar compras. Todos los productos
     * deben encontrarse publicados al confirmar la orden.
     *
     * La orden inicia en estado
     * {@link OrderStatus#PENDING_PAYMENT}.
     *
     * @param cart carrito desde el cual se crea la orden
     *
     * @throws IllegalArgumentException si el carrito es nulo
     * @throws IllegalStateException si el carrito está vacío
     * @throws IllegalStateException si el comprador no puede comprar
     * @throws IllegalStateException si algún producto ya no está publicado
     */
    public Order(Cart cart) {
        validateCart(cart);
        validateBuyerCanPurchase(cart.getBuyer());

        this.buyer = cart.getBuyer();
        this.items = copyCartItems(cart);
        this.status = OrderStatus.PENDING_PAYMENT;
    }

    /**
     * Obtiene el comprador propietario de la orden.
     *
     * @return el comprador asociado
     */
    public Buyer getBuyer() {
        return buyer;
    }

    /**
     * Obtiene una copia no modificable de los elementos de la orden.
     *
     * Esto evita que otras partes del sistema agreguen o eliminen
     * elementos directamente.
     *
     * @return una copia de los elementos de la orden
     */
    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    /**
     * Obtiene el estado actual de la orden.
     *
     * @return el estado de la orden
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Indica si el proceso de la orden ya fue finalizado.
     *
     * @return {@code true} si la orden está en estado
     *         {@link OrderStatus#FINALIZED}; de lo contrario,
     *         {@code false}
     */
    public boolean isFinalized() {
        return status == OrderStatus.FINALIZED;
    }

    /**
     * Determina si la orden contiene al menos un producto físico.
     *
     * Una orden mixta, con productos físicos y digitales, también se
     * considera una orden que requiere despacho físico.
     *
     * @return {@code true} si contiene al menos un producto físico;
     *         de lo contrario, {@code false}
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
     * Marca la orden como pagada.
     *
     * La operación solamente se permite cuando la orden se encuentra
     * pendiente de pago.
     *
     * @throws IllegalStateException si la orden no está en estado
     *                               {@link OrderStatus#PENDING_PAYMENT}
     */
    public void markAsPaid() {
        requireStatus(
                OrderStatus.PENDING_PAYMENT,
                "Only an order pending payment can be marked as paid."
        );

        status = OrderStatus.PAID;
    }

    /**
     * Marca una orden física como despachada.
     *
     * La orden debe estar pagada y contener al menos un producto físico.
     * Las órdenes completamente digitales no requieren despacho.
     *
     * @throws IllegalStateException si la orden no está pagada
     * @throws IllegalStateException si la orden solamente contiene
     *                               productos digitales
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
     * Marca la orden como entregada.
     *
     * Si contiene productos físicos, primero debe estar despachada.
     * Si contiene únicamente productos digitales, solo necesita estar
     * pagada para considerarse entregada.
     *
     * @throws IllegalStateException si una orden física no está despachada
     * @throws IllegalStateException si una orden digital no está pagada
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
     * Finaliza el proceso de la orden.
     *
     * La orden solamente puede finalizarse después de haber sido entregada.
     *
     * @throws IllegalStateException si la orden no está en estado
     *                               {@link OrderStatus#DELIVERED}
     */
    public void finalizeOrder() {
        requireStatus(
                OrderStatus.DELIVERED,
                "Only a delivered order can be finalized."
        );

        status = OrderStatus.FINALIZED;
    }

    /**
     * Copia los elementos del carrito y los convierte en elementos
     * propios de la orden.
     *
     * Las cantidades quedan independientes del carrito. Si posteriormente
     * se cambia una cantidad en el carrito, la orden no se modifica.
     *
     * Antes de copiar cada elemento se comprueba que su producto continúe
     * publicado.
     *
     * @param cart carrito cuyos elementos se desean copiar
     * @return una nueva lista de elementos de orden
     *
     * @throws IllegalStateException si algún producto no está publicado
     */
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

    /**
     * Valida que la orden se cree a partir de un carrito válido
     * y que este contenga al menos un producto.
     *
     * @param cart carrito que se desea validar
     *
     * @throws IllegalArgumentException si el carrito es nulo
     * @throws IllegalStateException si el carrito está vacío
     */
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

    /**
     * Valida que el comprador esté autorizado para confirmar la compra.
     *
     * Esta regla utiliza {@link Buyer#canPurchase()}, que comprueba tanto
     * el estado general del usuario como su estado comercial.
     *
     * @param buyer comprador que se desea validar
     *
     * @throws IllegalStateException si el comprador no puede comprar
     */
    private void validateBuyerCanPurchase(Buyer buyer) {
        if (!buyer.canPurchase()) {
            throw new IllegalStateException(
                    "Buyer is not authorized to confirm purchases."
            );
        }
    }

    /**
     * Comprueba que la orden se encuentre en el estado requerido para
     * ejecutar una transición.
     *
     * Este método centraliza la validación de los cambios de estado y evita
     * que la orden avance en un orden incorrecto.
     *
     * @param expectedStatus estado requerido para ejecutar la operación
     * @param errorMessage mensaje utilizado si el estado no corresponde
     *
     * @throws IllegalStateException si la orden no se encuentra en
     *                               el estado esperado
     */
    private void requireStatus(
            OrderStatus expectedStatus,
            String errorMessage
    ) {
        if (status != expectedStatus) {
            throw new IllegalStateException(errorMessage);
        }
    }
}