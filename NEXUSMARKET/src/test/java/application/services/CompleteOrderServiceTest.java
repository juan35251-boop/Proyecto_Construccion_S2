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
 * de finalizar pedidos.
 */
class CompleteOrderServiceTest {

    /**
     * Verifica que pueda finalizarse un pedido físico entregado.
     */
    @Test
    void shouldCompleteDeliveredPhysicalOrder() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        CompleteOrderService service =
                new CompleteOrderService(repository);

        Order order = createDeliveredOrder(
                ProductType.PHYSICAL
        );

        Order completedOrder = service.complete(order);

        assertEquals(
                OrderStatus.FINALIZED,
                completedOrder.getStatus()
        );
        assertTrue(completedOrder.isFinalized());
        assertTrue(repository.contains(order));
    }

    /**
     * Verifica que también pueda finalizarse
     * un pedido digital entregado.
     */
    @Test
    void shouldCompleteDeliveredDigitalOrder() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        CompleteOrderService service =
                new CompleteOrderService(repository);

        Order order = createDeliveredOrder(
                ProductType.DIGITAL
        );

        service.complete(order);

        assertEquals(
                OrderStatus.FINALIZED,
                order.getStatus()
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
                () -> new CompleteOrderService(null)
        );
    }

    /**
     * Verifica que la operación requiera un pedido.
     */
    @Test
    void shouldRejectNullOrder() {
        CompleteOrderService service =
                new CompleteOrderService(
                        new InMemoryOrderRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.complete(null)
        );
    }

    /**
     * Verifica que un pedido físico despachado,
     * pero todavía no entregado, no pueda finalizarse.
     */
    @Test
    void shouldRejectOrderNotDelivered() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        CompleteOrderService service =
                new CompleteOrderService(repository);

        Order order = createPaidOrder(
                ProductType.PHYSICAL
        );
        order.dispatch();

        assertThrows(
                IllegalStateException.class,
                () -> service.complete(order)
        );

        assertEquals(
                OrderStatus.DISPATCHED,
                order.getStatus()
        );
        assertFalse(repository.contains(order));
    }

    /**
     * Verifica que un pedido ya finalizado
     * no pueda finalizarse nuevamente.
     */
    @Test
    void shouldRejectAlreadyFinalizedOrder() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        CompleteOrderService service =
                new CompleteOrderService(repository);

        Order order = createDeliveredOrder(
                ProductType.PHYSICAL
        );
        order.finalizeOrder();

        assertThrows(
                IllegalStateException.class,
                () -> service.complete(order)
        );

        assertFalse(repository.contains(order));
    }

    /**
     * Crea un pedido y completa su entrega.
     */
    private Order createDeliveredOrder(
            ProductType productType
    ) {
        Order order = createPaidOrder(productType);

        if (order.containsPhysicalProducts()) {
            order.dispatch();
        }

        order.markAsDelivered();

        return order;
    }

    /**
     * Crea un pedido pagado.
     */
    private Order createPaidOrder(
            ProductType productType
    ) {
        Order order = createOrder(productType);
        order.markAsPaid();
        return order;
    }

    /**
     * Crea un pedido pendiente de pago.
     */
    private Order createOrder(ProductType productType) {
        Buyer buyer = new Buyer(
                "1001",
                "Test Buyer",
                "buyer@email.com",
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