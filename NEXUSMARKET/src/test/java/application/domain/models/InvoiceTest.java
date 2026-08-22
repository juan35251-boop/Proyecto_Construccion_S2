package application.domain.models;

import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvoiceTest {

    @Test
    void shouldCreateInvoiceForPaidOrder() {
        Order order = createPaidOrder();

        Invoice invoice = new Invoice(order);

        assertEquals(order, invoice.getOrder());
        assertEquals(order.getBuyer(), invoice.getBuyer());
        assertEquals(order.getItems(), invoice.getItems());
        assertTrue(invoice.belongsTo(order));
    }

    @Test
    void shouldRejectNullOrder() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Invoice(null)
        );
    }

    @Test
    void shouldRejectOrderPendingPayment() {
        Order order = createOrder();

        assertThrows(
                IllegalStateException.class,
                () -> new Invoice(order)
        );
    }

    @Test
    void shouldCreateInvoiceForDispatchedOrder() {
        Order order = createPaidOrder();
        order.dispatch();

        Invoice invoice = new Invoice(order);

        assertEquals(order, invoice.getOrder());
        assertTrue(invoice.belongsTo(order));
    }

    private Order createPaidOrder() {
        Order order = createOrder();
        order.markAsPaid();
        return order;
    }

    private Order createOrder() {
        Buyer buyer = new Buyer(
                "1001",
                "Juan Perez",
                "juan@email.com",
                UserStatus.ACTIVE,
                "Main Street 10",
                BuyerStatus.ACTIVE
        );

        Product product = new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );

        Cart cart = new Cart(buyer);
        cart.addProduct(product, 2);

        return new Order(cart);
    }
}
