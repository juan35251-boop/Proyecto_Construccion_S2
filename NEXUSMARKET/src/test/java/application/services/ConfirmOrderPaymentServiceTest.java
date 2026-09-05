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
 * Pruebas unitarias del servicio encargado de confirmar
 * el pago de los pedidos.
 */
class ConfirmOrderPaymentServiceTest {

    /**
     * Verifica que el propietario pueda confirmar el pago
     * de un pedido pendiente.
     */
    @Test
    void shouldConfirmOrderPayment() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        ConfirmOrderPaymentService service =
                new ConfirmOrderPaymentService(repository);

        Buyer buyer = createBuyer(
                "1001",
                UserStatus.ACTIVE,
                BuyerStatus.ACTIVE
        );

        Order order = createOrder(buyer);

        Order updatedOrder = service.confirm(buyer, order);

        assertEquals(OrderStatus.PAID, updatedOrder.getStatus());
        assertTrue(repository.contains(order));
    }

    /**
     * Verifica que el repositorio sea obligatorio.
     */
    @Test
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ConfirmOrderPaymentService(null)
        );
    }

    /**
     * Verifica que la confirmación requiera un comprador.
     */
    @Test
    void shouldRejectNullBuyer() {
        ConfirmOrderPaymentService service = createService();

        Buyer buyer = createBuyer(
                "1001",
                UserStatus.ACTIVE,
                BuyerStatus.ACTIVE
        );

        Order order = createOrder(buyer);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.confirm(null, order)
        );
    }

    /**
     * Verifica que la confirmación requiera un pedido.
     */
    @Test
    void shouldRejectNullOrder() {
        ConfirmOrderPaymentService service = createService();

        Buyer buyer = createBuyer(
                "1001",
                UserStatus.ACTIVE,
                BuyerStatus.ACTIVE
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.confirm(buyer, null)
        );
    }

    /**
     * Verifica que un comprador inactivo no pueda
     * confirmar pagos.
     */
    @Test
    void shouldRejectInactiveBuyer() {
        ConfirmOrderPaymentService service = createService();

        Buyer buyer = createBuyer(
                "1001",
                UserStatus.INACTIVE,
                BuyerStatus.ACTIVE
        );

        Order order = createOrderWithoutBuyerValidation(buyer);

        assertThrows(
                IllegalStateException.class,
                () -> service.confirm(buyer, order)
        );
    }

    /**
     * Verifica que un comprador comercialmente suspendido
     * no pueda confirmar pagos.
     */
    @Test
    void shouldRejectSuspendedBuyer() {
        ConfirmOrderPaymentService service = createService();

        Buyer buyer = createBuyer(
                "1001",
                UserStatus.ACTIVE,
                BuyerStatus.ACTIVE
        );

        Order order = createOrder(buyer);

        /*
         * El comprador fue suspendido después de crear el pedido,
         * pero antes de confirmar su pago.
         */
        buyer.changeCommercialStatus(BuyerStatus.SUSPENDED);

        assertThrows(
                IllegalStateException.class,
                () -> service.confirm(buyer, order)
        );
    }

    /**
     * Verifica que un comprador no pueda pagar
     * el pedido de otra persona.
     */
    @Test
    void shouldRejectDifferentBuyer() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        ConfirmOrderPaymentService service =
                new ConfirmOrderPaymentService(repository);

        Buyer owner = createBuyer(
                "1001",
                UserStatus.ACTIVE,
                BuyerStatus.ACTIVE
        );

        Buyer differentBuyer = createBuyer(
                "1002",
                UserStatus.ACTIVE,
                BuyerStatus.ACTIVE
        );

        Order order = createOrder(owner);

        assertThrows(
                IllegalStateException.class,
                () -> service.confirm(differentBuyer, order)
        );

        assertFalse(repository.contains(order));
    }

    /**
     * Verifica que un pedido ya pagado no pueda
     * recibir una segunda confirmación.
     */
    @Test
    void shouldRejectAlreadyPaidOrder() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        ConfirmOrderPaymentService service =
                new ConfirmOrderPaymentService(repository);

        Buyer buyer = createBuyer(
                "1001",
                UserStatus.ACTIVE,
                BuyerStatus.ACTIVE
        );

        Order order = createOrder(buyer);
        order.markAsPaid();

        assertThrows(
                IllegalStateException.class,
                () -> service.confirm(buyer, order)
        );

        assertFalse(repository.contains(order));
    }

    /**
     * Crea el servicio con un repositorio en memoria.
     */
    private ConfirmOrderPaymentService createService() {
        return new ConfirmOrderPaymentService(
                new InMemoryOrderRepository()
        );
    }

    /**
     * Crea un pedido pendiente de pago para un comprador activo.
     */
    private Order createOrder(Buyer buyer) {
        return createOrderWithoutBuyerValidation(buyer);
    }

    /**
     * Crea primero el pedido y luego permite modificar
     * el estado del comprador para probar casos inválidos.
     */
    private Order createOrderWithoutBuyerValidation(
            Buyer buyer
    ) {
        UserStatus originalUserStatus = buyer.getStatus();
        BuyerStatus originalBuyerStatus =
                buyer.getCommercialStatus();

        /*
         * Order solamente puede crearse inicialmente con
         * un comprador autorizado.
         */
        buyer.changeStatus(UserStatus.ACTIVE);
        buyer.changeCommercialStatus(BuyerStatus.ACTIVE);

        Product product = new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );

        Cart cart = new Cart(buyer);
        cart.addProduct(product, 1);

        Order order = new Order(cart);

        /*
         * Se restauran los estados originales para realizar
         * la prueba correspondiente.
         */
        buyer.changeStatus(originalUserStatus);
        buyer.changeCommercialStatus(originalBuyerStatus);

        return order;
    }

    /**
     * Crea un comprador con los estados indicados.
     */
    private Buyer createBuyer(
            String identification,
            UserStatus userStatus,
            BuyerStatus buyerStatus
    ) {
        return new Buyer(
                identification,
                "Buyer " + identification,
                identification + "@email.com",
                userStatus,
                "Main Street 10",
                buyerStatus
        );
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