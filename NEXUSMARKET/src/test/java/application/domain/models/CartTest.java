package application.domain.models;

import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartTest {

    @Test
    void shouldCreateEmptyCartForBuyer() {
        Buyer buyer = createBuyer();
        Cart cart = new Cart(buyer);

        assertEquals(buyer, cart.getBuyer());
        assertTrue(cart.isEmpty());
    }

    @Test
    void shouldRejectNullBuyer() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Cart(null)
        );
    }

    @Test
    void shouldAddPublishedProduct() {
        Cart cart = new Cart(createBuyer());
        Product product = createProduct(ProductStatus.PUBLISHED);

        cart.addProduct(product, 2);

        assertFalse(cart.isEmpty());
        assertTrue(cart.containsProduct(product));
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }

    @Test
    void shouldIncreaseQuantityWhenProductAlreadyExists() {
        Cart cart = new Cart(createBuyer());
        Product product = createProduct(ProductStatus.PUBLISHED);

        cart.addProduct(product, 2);
        cart.addProduct(product, 3);

        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    void shouldRejectUnpublishedProduct() {
        Cart cart = new Cart(createBuyer());
        Product suspendedProduct = createProduct(
                ProductStatus.SUSPENDED
        );

        assertThrows(
                IllegalStateException.class,
                () -> cart.addProduct(suspendedProduct, 1)
        );

        assertTrue(cart.isEmpty());
    }

    @Test
    void shouldRejectNonPositiveProductQuantity() {
        Cart cart = new Cart(createBuyer());
        Product product = createProduct(ProductStatus.PUBLISHED);

        assertThrows(
                IllegalArgumentException.class,
                () -> cart.addProduct(product, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> cart.addProduct(product, -1)
        );
    }

    @Test
    void shouldChangeProductQuantity() {
        Cart cart = new Cart(createBuyer());
        Product product = createProduct(ProductStatus.PUBLISHED);

        cart.addProduct(product, 2);
        cart.changeProductQuantity(product, 5);

        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    void shouldRemoveProduct() {
        Cart cart = new Cart(createBuyer());
        Product product = createProduct(ProductStatus.PUBLISHED);

        cart.addProduct(product, 2);
        cart.removeProduct(product);

        assertTrue(cart.isEmpty());
        assertFalse(cart.containsProduct(product));
    }

    @Test
    void shouldRejectOperationForMissingProduct() {
        Cart cart = new Cart(createBuyer());
        Product product = createProduct(ProductStatus.PUBLISHED);

        assertThrows(
                IllegalStateException.class,
                () -> cart.changeProductQuantity(product, 2)
        );

        assertThrows(
                IllegalStateException.class,
                () -> cart.removeProduct(product)
        );
    }

    @Test
    void shouldRejectCartItemWithoutProduct() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CartItem(null, 1)
        );
    }

    private Buyer createBuyer() {
        return new Buyer(
                "1001",
                "Juan Perez",
                "juan@email.com",
                UserStatus.ACTIVE,
                "Main Street 10",
                BuyerStatus.ACTIVE
        );
    }

    private Product createProduct(ProductStatus status) {
        return new Product(
                ProductType.PHYSICAL,
                status
        );
    }
}