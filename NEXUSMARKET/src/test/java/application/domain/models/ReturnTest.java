package application.domain.models;

import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReturnTest {

    @Test
    void shouldCreateReturnForDeliveredOrder() {
        Buyer buyer = createBuyer("1001");
        Order order = createDeliveredOrder(buyer);

        Return returnProcess = new Return(order, buyer);

        assertEquals(order, returnProcess.getOrder());
        assertEquals(buyer, returnProcess.getBuyer());
        assertTrue(returnProcess.belongsTo(order));
        assertTrue(returnProcess.wasRequestedBy(buyer));
    }

    @Test
    void shouldCreateReturnForFinalizedOrder() {
        Buyer buyer = createBuyer("1001");
        Order order = createDeliveredOrder(buyer);
        order.finalizeOrder();

        Return returnProcess = new Return(order, buyer);

        assertEquals(order, returnProcess.getOrder());
    }

    @Test
    void shouldRejectNullOrder() {
        Buyer buyer = createBuyer("1001");

        assertThrows(
                IllegalArgumentException.class,
                () -> new Return(null, buyer)
        );
    }

    @Test
    void shouldRejectNullBuyer() {
        Buyer buyer = createBuyer("1001");
        Order order = createDeliveredOrder(buyer);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Return(order, null)
        );
    }

    @Test
    void shouldRejectDifferentBuyer() {
        Buyer orderBuyer = createBuyer("1001");
        Buyer differentBuyer = createBuyer("1002");
        Order order = createDeliveredOrder(orderBuyer);

        assertThrows(
                IllegalStateException.class,
                () -> new Return(order, differentBuyer)
        );
    }

    @Test
    void shouldRejectOrderNotDelivered() {
        Buyer buyer = createBuyer("1001");
        Order order = createOrder(buyer);
        order.markAsPaid();
        order.dispatch();

        assertThrows(
                IllegalStateException.class,
                () -> new Return(order, buyer)
        );
    }

    private Order createDeliveredOrder(Buyer buyer) {
        Order order = createOrder(buyer);

        order.markAsPaid();
        order.dispatch();
        order.markAsDelivered();

        return order;
    }

    private Order createOrder(Buyer buyer) {
        Product product = new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );

        Cart cart = new Cart(buyer);
        cart.addProduct(product, 1);

        return new Order(cart);
    }

    private Buyer createBuyer(String identification) {
        return new Buyer(
                identification,
                "Buyer " + identification,
                identification + "@email.com",
                UserStatus.ACTIVE,
                "Main Street 10",
                BuyerStatus.ACTIVE
        );
    }
}
