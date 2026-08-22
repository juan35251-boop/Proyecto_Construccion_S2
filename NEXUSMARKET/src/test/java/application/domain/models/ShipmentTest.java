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

class ShipmentTest {

    @Test
    void shouldCreateShipmentForPaidPhysicalOrder() {
        Order order = createPaidOrder(ProductType.PHYSICAL);

        Shipment shipment = new Shipment(order);

        assertEquals(order, shipment.getOrder());
        assertTrue(shipment.belongsTo(order));
        assertFalse(shipment.isDispatched());
        assertFalse(shipment.isDelivered());
    }

    @Test
    void shouldRejectNullOrder() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Shipment(null)
        );
    }

    @Test
    void shouldRejectDigitalOrder() {
        Order digitalOrder = createPaidOrder(
                ProductType.DIGITAL
        );

        assertThrows(
                IllegalStateException.class,
                () -> new Shipment(digitalOrder)
        );
    }

    @Test
    void shouldRejectOrderPendingPayment() {
        Order order = createOrder(ProductType.PHYSICAL);

        assertThrows(
                IllegalStateException.class,
                () -> new Shipment(order)
        );
    }

    @Test
    void shouldDispatchShipment() {
        Order order = createPaidOrder(ProductType.PHYSICAL);
        Shipment shipment = new Shipment(order);

        shipment.dispatch();

        assertEquals(OrderStatus.DISPATCHED, order.getStatus());
        assertTrue(shipment.isDispatched());
        assertFalse(shipment.isDelivered());
    }

    @Test
    void shouldConfirmShipmentDelivery() {
        Order order = createPaidOrder(ProductType.PHYSICAL);
        Shipment shipment = new Shipment(order);

        shipment.dispatch();
        shipment.confirmDelivery();

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        assertTrue(shipment.isDispatched());
        assertTrue(shipment.isDelivered());
    }

    @Test
    void shouldRejectDeliveryBeforeDispatch() {
        Order order = createPaidOrder(ProductType.PHYSICAL);
        Shipment shipment = new Shipment(order);

        assertThrows(
                IllegalStateException.class,
                shipment::confirmDelivery
        );
    }

    private Order createPaidOrder(ProductType productType) {
        Order order = createOrder(productType);
        order.markAsPaid();
        return order;
    }

    private Order createOrder(ProductType productType) {
        Buyer buyer = new Buyer(
                "1001",
                "Juan Perez",
                "juan@email.com",
                UserStatus.ACTIVE,
                "Main Street 10",
                BuyerStatus.ACTIVE
        );

        Product product = new Product(
                productType,
                ProductStatus.PUBLISHED
        );

        Cart cart = new Cart(buyer);
        cart.addProduct(product, 1);

        return new Order(cart);
    }
}
