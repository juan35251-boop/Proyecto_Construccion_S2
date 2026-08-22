package application.domain.models;

import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderTest {

    @Test
    void shouldCreateOrderFromCart() {
        Cart cart = createCart(ProductType.PHYSICAL);

        Order order = new Order(cart);

        assertEquals(cart.getBuyer(), order.getBuyer());
        assertEquals(1, order.getItems().size());
        assertEquals(2, order.getItems().get(0).getQuantity());
        assertEquals(
                OrderStatus.PENDING_PAYMENT,
                order.getStatus()
        );
        assertTrue(order.containsPhysicalProducts());
        assertFalse(order.isFinalized());
    }

    @Test
    void shouldRejectNullCart() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Order(null)
        );
    }

    @Test
    void shouldRejectEmptyCart() {
        Cart emptyCart = new Cart(createActiveBuyer());

        assertThrows(
                IllegalStateException.class,
                () -> new Order(emptyCart)
        );
    }

    @Test
    void shouldRejectSuspendedBuyer() {
        Buyer buyer = createActiveBuyer();
        buyer.changeCommercialStatus(BuyerStatus.SUSPENDED);

        Cart cart = new Cart(buyer);
        cart.addProduct(
                createProduct(ProductType.PHYSICAL),
                1
        );

        assertThrows(
                IllegalStateException.class,
                () -> new Order(cart)
        );
    }

    @Test
    void shouldRejectUnpublishedProductAtConfirmation() {
        Cart cart = new Cart(createActiveBuyer());
        Product product = createProduct(ProductType.PHYSICAL);

        cart.addProduct(product, 1);
        product.changeStatus(ProductStatus.SUSPENDED);

        assertThrows(
                IllegalStateException.class,
                () -> new Order(cart)
        );
    }

    @Test
    void shouldCompletePhysicalOrderLifecycle() {
        Order order = new Order(
                createCart(ProductType.PHYSICAL)
        );

        order.markAsPaid();
        assertEquals(OrderStatus.PAID, order.getStatus());

        order.dispatch();
        assertEquals(OrderStatus.DISPATCHED, order.getStatus());

        order.markAsDelivered();
        assertEquals(OrderStatus.DELIVERED, order.getStatus());

        order.finalizeOrder();
        assertEquals(OrderStatus.FINALIZED, order.getStatus());
        assertTrue(order.isFinalized());

        assertThrows(
                IllegalStateException.class,
                order::markAsPaid
        );
    }

    @Test
    void shouldCompleteDigitalOrderWithoutDispatch() {
        Order order = new Order(
                createCart(ProductType.DIGITAL)
        );

        order.markAsPaid();
        order.markAsDelivered();
        order.finalizeOrder();

        assertEquals(OrderStatus.FINALIZED, order.getStatus());
        assertFalse(order.containsPhysicalProducts());
    }

    @Test
    void shouldRejectDispatchBeforePayment() {
        Order order = new Order(
                createCart(ProductType.PHYSICAL)
        );

        assertThrows(
                IllegalStateException.class,
                order::dispatch
        );
    }

    @Test
    void shouldRejectDigitalOrderDispatch() {
        Order order = new Order(
                createCart(ProductType.DIGITAL)
        );

        order.markAsPaid();

        assertThrows(
                IllegalStateException.class,
                order::dispatch
        );
    }

    @Test
    void shouldRejectPhysicalDeliveryBeforeDispatch() {
        Order order = new Order(
                createCart(ProductType.PHYSICAL)
        );

        order.markAsPaid();

        assertThrows(
                IllegalStateException.class,
                order::markAsDelivered
        );
    }

    @Test
    void shouldRejectFinalizationBeforeDelivery() {
        Order order = new Order(
                createCart(ProductType.PHYSICAL)
        );

        order.markAsPaid();
        order.dispatch();

        assertThrows(
                IllegalStateException.class,
                order::finalizeOrder
        );
    }

    @Test
    void shouldCopyCartItemQuantity() {
        Cart cart = createCart(ProductType.PHYSICAL);
        Order order = new Order(cart);
        Product product = cart.getItems().get(0).getProduct();

        cart.changeProductQuantity(product, 5);

        assertEquals(5, cart.getItems().get(0).getQuantity());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }

    @Test
    void shouldRejectInvalidOrderItem() {
        Product product = createProduct(ProductType.PHYSICAL);

        assertThrows(
                IllegalArgumentException.class,
                () -> new OrderItem(null, 1)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new OrderItem(product, 0)
        );
    }

    private Cart createCart(ProductType productType) {
        Cart cart = new Cart(createActiveBuyer());
        cart.addProduct(createProduct(productType), 2);
        return cart;
    }

    private Buyer createActiveBuyer() {
        return new Buyer(
                "1001",
                "Juan Perez",
                "juan@email.com",
                UserStatus.ACTIVE,
                "Main Street 10",
                BuyerStatus.ACTIVE
        );
    }

    private Product createProduct(ProductType productType) {
        return new Product(
                productType,
                ProductStatus.PUBLISHED
        );
    }
}
