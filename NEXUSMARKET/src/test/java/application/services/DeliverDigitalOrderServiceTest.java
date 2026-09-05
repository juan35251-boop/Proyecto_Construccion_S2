package application.services;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.ports.output.OrderRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del servicio encargado
 * de entregar pedidos digitales.
 */
class DeliverDigitalOrderServiceTest {

    /**
     * Verifica que un pedido digital pagado
     * pueda entregarse automáticamente.
     */
    @Test
    void shouldDeliverPaidDigitalOrder() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        DeliverDigitalOrderService service =
                new DeliverDigitalOrderService(repository);

        Order order = createOrder(ProductType.DIGITAL);
        order.markAsPaid();

        Order deliveredOrder = service.deliver(order);

        assertEquals(
                OrderStatus.DELIVERED,
                deliveredOrder.getStatus()
        );
        assertTrue(repository.contains(order));
    }

    /**
     * Verifica que el repositorio sea obligatorio.
     */
    @Test
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DeliverDigitalOrderService(null)
        );
    }

    /**
     * Verifica que la entrega requiera un pedido.
     */
    @Test
    void shouldRejectNullOrder() {
        DeliverDigitalOrderService service = createService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.deliver(null)
        );
    }

    /**
     * Verifica que un pedido digital deba estar pagado
     * antes de su entrega.
     */
    @Test
    void shouldRejectDigitalOrderPendingPayment() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        DeliverDigitalOrderService service =
                new DeliverDigitalOrderService(repository);

        Order order = createOrder(ProductType.DIGITAL);

        assertThrows(
                IllegalStateException.class,
                () -> service.deliver(order)
        );

        assertEquals(
                OrderStatus.PENDING_PAYMENT,
                order.getStatus()
        );
        assertFalse(repository.contains(order));
    }

    /**
     * Verifica que un pedido físico no pueda utilizar
     * el proceso de entrega digital.
     */
    @Test
    void shouldRejectPhysicalOrder() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        DeliverDigitalOrderService service =
                new DeliverDigitalOrderService(repository);

        Order order = createOrder(ProductType.PHYSICAL);
        order.markAsPaid();

        assertThrows(
                IllegalStateException.class,
                () -> service.deliver(order)
        );

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertFalse(repository.contains(order));
    }

    /**
     * Verifica que un pedido mixto requiera envío físico.
     */
    @Test
    void shouldRejectMixedOrder() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        DeliverDigitalOrderService service =
                new DeliverDigitalOrderService(repository);

        Order order = createOrder(
                ProductType.DIGITAL,
                ProductType.PHYSICAL
        );

        order.markAsPaid();

        assertThrows(
                IllegalStateException.class,
                () -> service.deliver(order)
        );

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertFalse(repository.contains(order));
    }

    /**
     * Verifica que un pedido entregado no pueda
     * entregarse por segunda vez.
     */
    @Test
    void shouldRejectAlreadyDeliveredOrder() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        DeliverDigitalOrderService service =
                new DeliverDigitalOrderService(repository);

        Order order = createOrder(ProductType.DIGITAL);
        order.markAsPaid();
        order.markAsDelivered();

        assertThrows(
                IllegalStateException.class,
                () -> service.deliver(order)
        );

        assertFalse(repository.contains(order));
    }

    /**
     * Crea el servicio con un repositorio en memoria.
     */
    private DeliverDigitalOrderService createService() {
        return new DeliverDigitalOrderService(
                new InMemoryOrderRepository()
        );
    }

    /**
     * Crea un pedido con uno o varios tipos de producto.
     *
     * @param productTypes tipos de productos incluidos
     * @return pedido pendiente de pago
     */
    private Order createOrder(
            ProductType... productTypes
    ) {
        Buyer buyer = new Buyer(
                "1001",
                "Test Buyer",
                "buyer@email.com",
                UserStatus.ACTIVE,
                "Main Street 10",
                BuyerStatus.ACTIVE
        );

        Cart cart = new Cart(buyer);

        for (ProductType productType : productTypes) {
            Product product = new Product(
                    productType,
                    ProductStatus.PUBLISHED
            );

            cart.addProduct(product, 1);
        }

        return new Order(cart);
    }

    /**
     * Repositorio de pedidos utilizado durante las pruebas.
     */
    private static class InMemoryOrderRepository
            implements OrderRepository {

        private final List<Order> orders =
                new ArrayList<>();

        @Override
        public void save(Order order) {
            orders.add(order);
        }

        boolean contains(Order order) {
            return orders.contains(order);
        }
    }
}
