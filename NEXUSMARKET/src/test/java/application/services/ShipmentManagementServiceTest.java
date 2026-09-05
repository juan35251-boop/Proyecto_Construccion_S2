package application.services;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.LogisticsOperator;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Shipment;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.ports.output.OrderRepository;
import application.ports.output.ShipmentRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del servicio encargado
 * de gestionar los envíos.
 */
class ShipmentManagementServiceTest {

    /**
     * Verifica la creación de un envío para un pedido físico pagado.
     */
    @Test
    void shouldCreateShipmentForPaidPhysicalOrder() {
        TestContext context = createContext();
        Order order = createPaidOrder(ProductType.PHYSICAL);

        Shipment shipment = context.service.createShipment(
                createOperator(UserStatus.ACTIVE),
                order
        );

        assertEquals(order, shipment.getOrder());
        assertFalse(shipment.isDispatched());
        assertTrue(
                context.shipmentRepository.contains(shipment)
        );
    }

    /**
     * Verifica que el repositorio de envíos sea obligatorio.
     */
    @Test
    void shouldRejectNullShipmentRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ShipmentManagementService(
                        null,
                        new InMemoryOrderRepository()
                )
        );
    }

    /**
     * Verifica que el repositorio de pedidos sea obligatorio.
     */
    @Test
    void shouldRejectNullOrderRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ShipmentManagementService(
                        new InMemoryShipmentRepository(),
                        null
                )
        );
    }

    /**
     * Verifica que se requiera un operador logístico.
     */
    @Test
    void shouldRejectNullOperator() {
        TestContext context = createContext();
        Order order = createPaidOrder(ProductType.PHYSICAL);

        assertThrows(
                IllegalArgumentException.class,
                () -> context.service.createShipment(
                        null,
                        order
                )
        );
    }

    /**
     * Verifica que un operador inactivo no pueda crear envíos.
     */
    @Test
    void shouldRejectInactiveOperator() {
        TestContext context = createContext();
        Order order = createPaidOrder(ProductType.PHYSICAL);

        assertThrows(
                IllegalStateException.class,
                () -> context.service.createShipment(
                        createOperator(UserStatus.INACTIVE),
                        order
                )
        );
    }

    /**
     * Verifica que un pedido solamente digital no requiera envío.
     */
    @Test
    void shouldRejectDigitalOrder() {
        TestContext context = createContext();
        Order order = createPaidOrder(ProductType.DIGITAL);

        assertThrows(
                IllegalStateException.class,
                () -> context.service.createShipment(
                        createOperator(UserStatus.ACTIVE),
                        order
                )
        );
    }

    /**
     * Verifica que no pueda crearse un envío antes del pago.
     */
    @Test
    void shouldRejectOrderPendingPayment() {
        TestContext context = createContext();
        Order order = createOrder(ProductType.PHYSICAL);

        assertThrows(
                IllegalStateException.class,
                () -> context.service.createShipment(
                        createOperator(UserStatus.ACTIVE),
                        order
                )
        );
    }

    /**
     * Verifica que el despacho actualice el estado del pedido.
     */
    @Test
    void shouldDispatchShipment() {
        TestContext context = createContext();
        Order order = createPaidOrder(ProductType.PHYSICAL);
        Shipment shipment = new Shipment(order);

        context.service.dispatchShipment(
                createOperator(UserStatus.ACTIVE),
                shipment
        );

        assertEquals(
                OrderStatus.DISPATCHED,
                order.getStatus()
        );
        assertTrue(shipment.isDispatched());
        assertTrue(
                context.orderRepository.contains(order)
        );
        assertTrue(
                context.shipmentRepository.contains(shipment)
        );
    }

    /**
     * Verifica que la entrega actualice el pedido.
     */
    @Test
    void shouldConfirmShipmentDelivery() {
        TestContext context = createContext();
        Order order = createPaidOrder(ProductType.PHYSICAL);
        Shipment shipment = new Shipment(order);

        shipment.dispatch();

        context.service.confirmDelivery(
                createOperator(UserStatus.ACTIVE),
                shipment
        );

        assertEquals(
                OrderStatus.DELIVERED,
                order.getStatus()
        );
        assertTrue(shipment.isDelivered());
        assertTrue(
                context.orderRepository.contains(order)
        );
    }

    /**
     * Verifica que no se confirme una entrega antes del despacho.
     */
    @Test
    void shouldRejectDeliveryBeforeDispatch() {
        TestContext context = createContext();
        Order order = createPaidOrder(ProductType.PHYSICAL);
        Shipment shipment = new Shipment(order);

        assertThrows(
                IllegalStateException.class,
                () -> context.service.confirmDelivery(
                        createOperator(UserStatus.ACTIVE),
                        shipment
                )
        );

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertFalse(
                context.orderRepository.contains(order)
        );
    }

    /**
     * Verifica que la operación de despacho requiera un envío.
     */
    @Test
    void shouldRejectNullShipment() {
        TestContext context = createContext();

        assertThrows(
                IllegalArgumentException.class,
                () -> context.service.dispatchShipment(
                        createOperator(UserStatus.ACTIVE),
                        null
                )
        );
    }

    /**
     * Crea el servicio y sus repositorios en memoria.
     */
    private TestContext createContext() {
        InMemoryShipmentRepository shipmentRepository =
                new InMemoryShipmentRepository();

        InMemoryOrderRepository orderRepository =
                new InMemoryOrderRepository();

        ShipmentManagementService service =
                new ShipmentManagementService(
                        shipmentRepository,
                        orderRepository
                );

        return new TestContext(
                service,
                shipmentRepository,
                orderRepository
        );
    }

    /**
     * Crea un pedido pagado.
     */
    private Order createPaidOrder(ProductType productType) {
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
     * Crea un operador logístico con el estado indicado.
     */
    private LogisticsOperator createOperator(
            UserStatus status
    ) {
        return new LogisticsOperator(
                "3001",
                "Logistics Operator",
                "operator@email.com",
                status
        );
    }

    /**
     * Agrupa el servicio y los repositorios de la prueba.
     */
    private record TestContext(
            ShipmentManagementService service,
            InMemoryShipmentRepository shipmentRepository,
            InMemoryOrderRepository orderRepository
    ) {
    }

    /**
     * Repositorio de envíos utilizado durante las pruebas.
     */
    private static class InMemoryShipmentRepository
            implements ShipmentRepository {

        private final List<Shipment> shipments =
                new ArrayList<>();

        @Override
        public void save(Shipment shipment) {
            shipments.add(shipment);
        }

        boolean contains(Shipment shipment) {
            return shipments.contains(shipment);
        }
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
