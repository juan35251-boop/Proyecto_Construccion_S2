package application.services;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Return;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.ports.output.ReturnRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del servicio encargado de registrar
 * solicitudes de devolución.
 */
class RequestReturnServiceTest {

    /**
     * Verifica que un comprador activo pueda solicitar
     * la devolución de un pedido entregado.
     */
    @Test
    void shouldRequestReturnForDeliveredOrder() {
        InMemoryReturnRepository repository =
                new InMemoryReturnRepository();

        RequestReturnService service =
                new RequestReturnService(repository);

        Buyer buyer = createBuyer("1001");
        Order order = createDeliveredOrder(buyer);

        Return returnProcess = service.request(buyer, order);

        assertEquals(order, returnProcess.getOrder());
        assertEquals(buyer, returnProcess.getBuyer());
        assertTrue(repository.contains(returnProcess));
    }

    /**
     * Verifica que un pedido finalizado también
     * pueda tener una solicitud de devolución.
     */
    @Test
    void shouldRequestReturnForFinalizedOrder() {
        InMemoryReturnRepository repository =
                new InMemoryReturnRepository();

        RequestReturnService service =
                new RequestReturnService(repository);

        Buyer buyer = createBuyer("1001");
        Order order = createDeliveredOrder(buyer);
        order.finalizeOrder();

        Return returnProcess = service.request(buyer, order);

        assertTrue(repository.contains(returnProcess));
    }

    /**
     * Verifica que un comprador comercialmente suspendido
     * conserve la posibilidad de devolver una compra anterior.
     */
    @Test
    void shouldAllowCommerciallySuspendedBuyer() {
        InMemoryReturnRepository repository =
                new InMemoryReturnRepository();

        RequestReturnService service =
                new RequestReturnService(repository);

        Buyer buyer = createBuyer("1001");
        Order order = createDeliveredOrder(buyer);

        buyer.changeCommercialStatus(BuyerStatus.SUSPENDED);

        Return returnProcess = service.request(buyer, order);

        assertTrue(repository.contains(returnProcess));
    }

    /**
     * Verifica que el repositorio sea obligatorio.
     */
    @Test
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RequestReturnService(null)
        );
    }

    /**
     * Verifica que la solicitud requiera un comprador.
     */
    @Test
    void shouldRejectNullBuyer() {
        RequestReturnService service = createService();

        Buyer buyer = createBuyer("1001");
        Order order = createDeliveredOrder(buyer);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.request(null, order)
        );
    }

    /**
     * Verifica que la solicitud requiera un pedido.
     */
    @Test
    void shouldRejectNullOrder() {
        RequestReturnService service = createService();
        Buyer buyer = createBuyer("1001");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.request(buyer, null)
        );
    }

    /**
     * Verifica que un comprador inactivo no pueda
     * solicitar devoluciones.
     */
    @Test
    void shouldRejectInactiveBuyer() {
        InMemoryReturnRepository repository =
                new InMemoryReturnRepository();

        RequestReturnService service =
                new RequestReturnService(repository);

        Buyer buyer = createBuyer("1001");
        Order order = createDeliveredOrder(buyer);

        buyer.changeStatus(UserStatus.INACTIVE);

        assertThrows(
                IllegalStateException.class,
                () -> service.request(buyer, order)
        );

        assertTrue(repository.getReturns().isEmpty());
    }

    /**
     * Verifica que un comprador no pueda devolver
     * el pedido de otra persona.
     */
    @Test
    void shouldRejectDifferentBuyer() {
        InMemoryReturnRepository repository =
                new InMemoryReturnRepository();

        RequestReturnService service =
                new RequestReturnService(repository);

        Buyer owner = createBuyer("1001");
        Buyer differentBuyer = createBuyer("1002");
        Order order = createDeliveredOrder(owner);

        assertThrows(
                IllegalStateException.class,
                () -> service.request(
                        differentBuyer,
                        order
                )
        );

        assertFalse(repository.containsOrder(order));
    }

    /**
     * Verifica que un pedido sin entregar todavía
     * no pueda devolverse.
     */
    @Test
    void shouldRejectOrderNotDelivered() {
        InMemoryReturnRepository repository =
                new InMemoryReturnRepository();

        RequestReturnService service =
                new RequestReturnService(repository);

        Buyer buyer = createBuyer("1001");
        Order order = createOrder(buyer);

        order.markAsPaid();
        order.dispatch();

        assertThrows(
                IllegalStateException.class,
                () -> service.request(buyer, order)
        );

        assertFalse(repository.containsOrder(order));
    }

    /**
     * Crea el servicio con un repositorio en memoria.
     */
    private RequestReturnService createService() {
        return new RequestReturnService(
                new InMemoryReturnRepository()
        );
    }

    /**
     * Crea un pedido y completa el proceso de entrega.
     */
    private Order createDeliveredOrder(Buyer buyer) {
        Order order = createOrder(buyer);

        order.markAsPaid();
        order.dispatch();
        order.markAsDelivered();

        return order;
    }

    /**
     * Crea un pedido físico pendiente de pago.
     */
    private Order createOrder(Buyer buyer) {
        Product product = new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );

        Cart cart = new Cart(buyer);
        cart.addProduct(product, 1);

        return new Order(cart);
    }

    /**
     * Crea un comprador activo.
     */
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

    /**
     * Repositorio de devoluciones utilizado en las pruebas.
     */
    private static class InMemoryReturnRepository
            implements ReturnRepository {

        private final List<Return> returns =
                new ArrayList<>();

        @Override
        public void save(Return returnProcess) {
            returns.add(returnProcess);
        }

        boolean contains(Return returnProcess) {
            return returns.contains(returnProcess);
        }

        boolean containsOrder(Order order) {
            for (Return returnProcess : returns) {
                if (returnProcess.belongsTo(order)) {
                    return true;
                }
            }

            return false;
        }

        List<Return> getReturns() {
            return List.copyOf(returns);
        }
    }
}