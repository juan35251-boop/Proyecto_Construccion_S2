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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del servicio encargado de crear pedidos.
 *
 * Se utiliza un repositorio en memoria para comprobar que
 * los pedidos sean creados y almacenados correctamente.
 */
class CreateOrderServiceTest {

    /**
     * Verifica que un comprador autorizado pueda crear
     * un pedido desde un carrito con productos.
     */
    @Test
    void shouldCreateOrderFromCart() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        CreateOrderService service =
                new CreateOrderService(repository);

        Buyer buyer = createBuyer(
                UserStatus.ACTIVE,
                BuyerStatus.ACTIVE
        );

        Cart cart = createCart(
                buyer,
                ProductStatus.PUBLISHED
        );

        Order order = service.create(cart);

        assertEquals(buyer, order.getBuyer());
        assertEquals(
                OrderStatus.PENDING_PAYMENT,
                order.getStatus()
        );
        assertEquals(1, order.getItems().size());
        assertEquals(2, order.getItems().get(0).getQuantity());
        assertTrue(repository.contains(order));
    }

    /**
     * Verifica que el repositorio de pedidos sea obligatorio.
     */
    @Test
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CreateOrderService(null)
        );
    }

    /**
     * Verifica que no pueda crearse un pedido sin carrito.
     */
    @Test
    void shouldRejectNullCart() {
        CreateOrderService service = createService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(null)
        );
    }

    /**
     * Verifica que un carrito vacío no produzca un pedido.
     */
    @Test
    void shouldRejectEmptyCart() {
        CreateOrderService service = createService();

        Buyer buyer = createBuyer(
                UserStatus.ACTIVE,
                BuyerStatus.ACTIVE
        );

        Cart emptyCart = new Cart(buyer);

        assertThrows(
                IllegalStateException.class,
                () -> service.create(emptyCart)
        );
    }

    /**
     * Verifica que un comprador inactivo no pueda
     * confirmar una compra.
     */
    @Test
    void shouldRejectInactiveBuyer() {
        CreateOrderService service = createService();

        Buyer buyer = createBuyer(
                UserStatus.INACTIVE,
                BuyerStatus.ACTIVE
        );

        Cart cart = createCart(
                buyer,
                ProductStatus.PUBLISHED
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.create(cart)
        );
    }

    /**
     * Verifica que un comprador comercialmente suspendido
     * no pueda confirmar una compra.
     */
    @Test
    void shouldRejectCommerciallySuspendedBuyer() {
        CreateOrderService service = createService();

        Buyer buyer = createBuyer(
                UserStatus.ACTIVE,
                BuyerStatus.SUSPENDED
        );

        Cart cart = createCart(
                buyer,
                ProductStatus.PUBLISHED
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.create(cart)
        );
    }

    /**
     * Verifica que un producto deje de ser válido si cambia
     * su estado antes de confirmar el pedido.
     */
    @Test
    void shouldRejectUnpublishedProductWhenCreatingOrder() {
        CreateOrderService service = createService();

        Buyer buyer = createBuyer(
                UserStatus.ACTIVE,
                BuyerStatus.ACTIVE
        );

        Product product = new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );

        Cart cart = new Cart(buyer);
        cart.addProduct(product, 2);

        /*
         * El producto estaba publicado cuando se agregó,
         * pero fue suspendido antes de confirmar el pedido.
         */
        product.changeStatus(ProductStatus.SUSPENDED);

        assertThrows(
                IllegalStateException.class,
                () -> service.create(cart)
        );
    }

    /**
     * Crea el servicio con un repositorio en memoria.
     */
    private CreateOrderService createService() {
        return new CreateOrderService(
                new InMemoryOrderRepository()
        );
    }

    /**
     * Crea un carrito con un producto y dos unidades.
     */
    private Cart createCart(
            Buyer buyer,
            ProductStatus productStatus
    ) {
        Product product = new Product(
                ProductType.PHYSICAL,
                productStatus
        );

        Cart cart = new Cart(buyer);
        cart.addProduct(product, 2);

        return cart;
    }

    /**
     * Crea un comprador con los estados requeridos.
     */
    private Buyer createBuyer(
            UserStatus userStatus,
            BuyerStatus buyerStatus
    ) {
        return new Buyer(
                "1001",
                "Test Buyer",
                "buyer@email.com",
                userStatus,
                "Main Street 10",
                buyerStatus
        );
    }

    /**
     * Repositorio de pedidos utilizado solamente
     * durante las pruebas.
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