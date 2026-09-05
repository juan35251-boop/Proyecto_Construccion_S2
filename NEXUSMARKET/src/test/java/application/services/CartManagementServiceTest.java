package application.services;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Product;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.ports.output.CartRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del servicio encargado de gestionar
 * los carritos de compras.
 */
class CartManagementServiceTest {

    /**
     * Verifica que un comprador autorizado pueda crear un carrito.
     */
    @Test
    void shouldCreateCart() {
        InMemoryCartRepository repository =
                new InMemoryCartRepository();

        CartManagementService service =
                new CartManagementService(repository);

        Buyer buyer = createBuyer(
                "1001",
                UserStatus.ACTIVE,
                BuyerStatus.ACTIVE
        );

        Cart cart = service.createCart(buyer);

        assertEquals(buyer, cart.getBuyer());
        assertTrue(cart.isEmpty());
        assertTrue(repository.contains(cart));
    }

    /**
     * Verifica que pueda agregarse un producto publicado.
     */
    @Test
    void shouldAddProductToCart() {
        InMemoryCartRepository repository =
                new InMemoryCartRepository();

        CartManagementService service =
                new CartManagementService(repository);

        Buyer buyer = createActiveBuyer("1001");
        Cart cart = new Cart(buyer);
        Product product = createProduct(
                ProductStatus.PUBLISHED
        );

        service.addProduct(buyer, cart, product, 2);

        assertTrue(cart.containsProduct(product));
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertTrue(repository.contains(cart));
    }

    /**
     * Verifica que pueda cambiarse la cantidad de un producto.
     */
    @Test
    void shouldChangeProductQuantity() {
        InMemoryCartRepository repository =
                new InMemoryCartRepository();

        CartManagementService service =
                new CartManagementService(repository);

        Buyer buyer = createActiveBuyer("1001");
        Cart cart = new Cart(buyer);
        Product product = createProduct(
                ProductStatus.PUBLISHED
        );

        cart.addProduct(product, 2);

        service.changeProductQuantity(
                buyer,
                cart,
                product,
                5
        );

        assertEquals(5, cart.getItems().get(0).getQuantity());
        assertTrue(repository.contains(cart));
    }

    /**
     * Verifica que pueda eliminarse un producto.
     */
    @Test
    void shouldRemoveProductFromCart() {
        InMemoryCartRepository repository =
                new InMemoryCartRepository();

        CartManagementService service =
                new CartManagementService(repository);

        Buyer buyer = createActiveBuyer("1001");
        Cart cart = new Cart(buyer);
        Product product = createProduct(
                ProductStatus.PUBLISHED
        );

        cart.addProduct(product, 1);

        service.removeProduct(buyer, cart, product);

        assertTrue(cart.isEmpty());
        assertFalse(cart.containsProduct(product));
        assertTrue(repository.contains(cart));
    }

    /**
     * Verifica que el repositorio sea obligatorio.
     */
    @Test
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CartManagementService(null)
        );
    }

    /**
     * Verifica que no pueda crearse un carrito sin comprador.
     */
    @Test
    void shouldRejectNullBuyer() {
        CartManagementService service = createService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createCart(null)
        );
    }

    /**
     * Verifica que un comprador inactivo no pueda crear carritos.
     */
    @Test
    void shouldRejectInactiveBuyer() {
        CartManagementService service = createService();

        Buyer buyer = createBuyer(
                "1001",
                UserStatus.INACTIVE,
                BuyerStatus.ACTIVE
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.createCart(buyer)
        );
    }

    /**
     * Verifica que un comprador suspendido comercialmente
     * no pueda gestionar un carrito.
     */
    @Test
    void shouldRejectSuspendedBuyer() {
        CartManagementService service = createService();

        Buyer buyer = createBuyer(
                "1001",
                UserStatus.ACTIVE,
                BuyerStatus.SUSPENDED
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.createCart(buyer)
        );
    }

    /**
     * Verifica que la operación requiera un carrito.
     */
    @Test
    void shouldRejectNullCart() {
        CartManagementService service = createService();
        Buyer buyer = createActiveBuyer("1001");
        Product product = createProduct(
                ProductStatus.PUBLISHED
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addProduct(
                        buyer,
                        null,
                        product,
                        1
                )
        );
    }

    /**
     * Verifica que un comprador no pueda modificar
     * el carrito de otra persona.
     */
    @Test
    void shouldRejectDifferentBuyer() {
        CartManagementService service = createService();

        Buyer owner = createActiveBuyer("1001");
        Buyer differentBuyer = createActiveBuyer("1002");

        Cart cart = new Cart(owner);
        Product product = createProduct(
                ProductStatus.PUBLISHED
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.addProduct(
                        differentBuyer,
                        cart,
                        product,
                        1
                )
        );

        assertTrue(cart.isEmpty());
    }

    /**
     * Verifica que no pueda agregarse un producto suspendido.
     */
    @Test
    void shouldRejectUnpublishedProduct() {
        CartManagementService service = createService();

        Buyer buyer = createActiveBuyer("1001");
        Cart cart = new Cart(buyer);

        Product product = createProduct(
                ProductStatus.SUSPENDED
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.addProduct(
                        buyer,
                        cart,
                        product,
                        1
                )
        );

        assertTrue(cart.isEmpty());
    }

    /**
     * Verifica que la cantidad agregada sea mayor que cero.
     */
    @Test
    void shouldRejectNonPositiveQuantity() {
        CartManagementService service = createService();

        Buyer buyer = createActiveBuyer("1001");
        Cart cart = new Cart(buyer);

        Product product = createProduct(
                ProductStatus.PUBLISHED
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addProduct(
                        buyer,
                        cart,
                        product,
                        0
                )
        );

        assertTrue(cart.isEmpty());
    }

    /**
     * Crea el servicio con un repositorio en memoria.
     */
    private CartManagementService createService() {
        return new CartManagementService(
                new InMemoryCartRepository()
        );
    }

    /**
     * Crea un comprador activo.
     */
    private Buyer createActiveBuyer(
            String identification
    ) {
        return createBuyer(
                identification,
                UserStatus.ACTIVE,
                BuyerStatus.ACTIVE
        );
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
     * Crea un producto físico con el estado indicado.
     */
    private Product createProduct(ProductStatus status) {
        return new Product(
                ProductType.PHYSICAL,
                status
        );
    }

    /**
     * Repositorio de carritos utilizado durante las pruebas.
     */
    private static class InMemoryCartRepository
            implements CartRepository {

        private final List<Cart> carts = new ArrayList<>();

        @Override
        public void save(Cart cart) {
            carts.add(cart);
        }

        boolean contains(Cart cart) {
            return carts.contains(cart);
        }
    }
}