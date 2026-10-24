package application.adapters.input.rest.controllers;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.Product;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.ports.input.CartManagementUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias del controlador REST
 * encargado del carrito de compras.
 *
 * El caso de uso es simulado con Mockito para comprobar
 * solamente el comportamiento HTTP del controlador.
 */
@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    private static final String BUYER_IDENTIFICATION =
            "BUYER-1001";

    private static final String CART_ID =
            "cart-001";

    private static final String PRODUCT_ID =
            "product-001";

    @Mock
    private CartManagementUseCase
            cartManagementUseCase;

    @Mock
    private Cart cart;

    @Mock
    private Buyer buyer;

    @Mock
    private CartItem cartItem;

    @Mock
    private Product product;

    private MockMvc mockMvc;

    /**
     * Construye el controlador antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        CartController controller =
                new CartController(
                        cartManagementUseCase
                );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    /**
     * Comprueba la creación de un carrito vacío.
     */
    @Test
    void shouldCreateCart()
            throws Exception {
        configureEmptyCart();

        when(cartManagementUseCase
                .createCartByBuyerIdentification(
                        BUYER_IDENTIFICATION
                ))
                .thenReturn(cart);

        mockMvc.perform(
                        post("/api/v1/carts")
                                .header(
                                        "X-Requester-Identification",
                                        BUYER_IDENTIFICATION
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.cartId")
                                .value(CART_ID)
                )
                .andExpect(
                        jsonPath("$.buyerIdentification")
                                .value(BUYER_IDENTIFICATION)
                )
                .andExpect(
                        jsonPath("$.items")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$.items")
                                .isEmpty()
                )
                .andExpect(
                        jsonPath("$.totalItems")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.empty")
                                .value(true)
                );

        verify(cartManagementUseCase)
                .createCartByBuyerIdentification(
                        BUYER_IDENTIFICATION
                );
    }

    /**
     * Comprueba la consulta del carrito propio.
     */
    @Test
    void shouldFindOwnCart()
            throws Exception {
        configureCartWithItem(2);

        when(cartManagementUseCase
                .findCartByBuyerIdentification(
                        BUYER_IDENTIFICATION
                ))
                .thenReturn(cart);

        mockMvc.perform(
                        get("/api/v1/carts/mine")
                                .header(
                                        "X-Requester-Identification",
                                        BUYER_IDENTIFICATION
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.cartId")
                                .value(CART_ID)
                )
                .andExpect(
                        jsonPath("$.items[0].productId")
                                .value(PRODUCT_ID)
                )
                .andExpect(
                        jsonPath("$.items[0].productType")
                                .value("PHYSICAL")
                )
                .andExpect(
                        jsonPath("$.items[0].productStatus")
                                .value("PUBLISHED")
                )
                .andExpect(
                        jsonPath("$.items[0].quantity")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.totalItems")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.empty")
                                .value(false)
                );

        verify(cartManagementUseCase)
                .findCartByBuyerIdentification(
                        BUYER_IDENTIFICATION
                );
    }

    /**
     * Comprueba la adición de un producto.
     */
    @Test
    void shouldAddProduct()
            throws Exception {
        configureCartWithItem(3);

        when(cartManagementUseCase
                .addProductByIdentifiers(
                        BUYER_IDENTIFICATION,
                        CART_ID,
                        PRODUCT_ID,
                        3
                ))
                .thenReturn(cart);

        mockMvc.perform(
                        post(
                                "/api/v1/carts/{cartId}/items",
                                CART_ID
                        )
                                .header(
                                        "X-Requester-Identification",
                                        BUYER_IDENTIFICATION
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "productId": "product-001",
                                          "quantity": 3
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.items[0].productId")
                                .value(PRODUCT_ID)
                )
                .andExpect(
                        jsonPath("$.items[0].quantity")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.totalItems")
                                .value(3)
                );

        verify(cartManagementUseCase)
                .addProductByIdentifiers(
                        BUYER_IDENTIFICATION,
                        CART_ID,
                        PRODUCT_ID,
                        3
                );
    }

    /**
     * Comprueba el cambio de cantidad.
     */
    @Test
    void shouldChangeProductQuantity()
            throws Exception {
        configureCartWithItem(5);

        when(cartManagementUseCase
                .changeProductQuantityByIdentifiers(
                        BUYER_IDENTIFICATION,
                        CART_ID,
                        PRODUCT_ID,
                        5
                ))
                .thenReturn(cart);

        mockMvc.perform(
                        patch(
                                "/api/v1/carts/{cartId}/items/{productId}",
                                CART_ID,
                                PRODUCT_ID
                        )
                                .header(
                                        "X-Requester-Identification",
                                        BUYER_IDENTIFICATION
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "newQuantity": 5
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.items[0].quantity")
                                .value(5)
                )
                .andExpect(
                        jsonPath("$.totalItems")
                                .value(5)
                );

        verify(cartManagementUseCase)
                .changeProductQuantityByIdentifiers(
                        BUYER_IDENTIFICATION,
                        CART_ID,
                        PRODUCT_ID,
                        5
                );
    }

    /**
     * Comprueba la eliminación de un producto.
     */
    @Test
    void shouldRemoveProduct()
            throws Exception {
        configureEmptyCart();

        when(cartManagementUseCase
                .removeProductByIdentifiers(
                        BUYER_IDENTIFICATION,
                        CART_ID,
                        PRODUCT_ID
                ))
                .thenReturn(cart);

        mockMvc.perform(
                        delete(
                                "/api/v1/carts/{cartId}/items/{productId}",
                                CART_ID,
                                PRODUCT_ID
                        )
                                .header(
                                        "X-Requester-Identification",
                                        BUYER_IDENTIFICATION
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.items")
                                .isEmpty()
                )
                .andExpect(
                        jsonPath("$.totalItems")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.empty")
                                .value(true)
                );

        verify(cartManagementUseCase)
                .removeProductByIdentifiers(
                        BUYER_IDENTIFICATION,
                        CART_ID,
                        PRODUCT_ID
                );
    }

    /**
     * Comprueba que no se pueda agregar
     * una cantidad igual a cero.
     */
    @Test
    void shouldRejectNonPositiveProductQuantity()
            throws Exception {
        mockMvc.perform(
                        post(
                                "/api/v1/carts/{cartId}/items",
                                CART_ID
                        )
                                .header(
                                        "X-Requester-Identification",
                                        BUYER_IDENTIFICATION
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "productId": "product-001",
                                          "quantity": 0
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    /**
     * Comprueba que no se pueda cambiar
     * la cantidad a un valor negativo.
     */
    @Test
    void shouldRejectNegativeNewQuantity()
            throws Exception {
        mockMvc.perform(
                        patch(
                                "/api/v1/carts/{cartId}/items/{productId}",
                                CART_ID,
                                PRODUCT_ID
                        )
                                .header(
                                        "X-Requester-Identification",
                                        BUYER_IDENTIFICATION
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "newQuantity": -1
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    /**
     * Comprueba que el identificador del producto
     * sea obligatorio.
     */
    @Test
    void shouldRejectEmptyProductIdentifier()
            throws Exception {
        mockMvc.perform(
                        post(
                                "/api/v1/carts/{cartId}/items",
                                CART_ID
                        )
                                .header(
                                        "X-Requester-Identification",
                                        BUYER_IDENTIFICATION
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "productId": "",
                                          "quantity": 2
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    /**
     * Comprueba la validación del caso de uso
     * requerido por el controlador.
     */
    @Test
    void shouldRejectNullCartManagementUseCase() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CartController(null)
        );
    }

    /**
     * Configura un carrito vacío.
     */
    private void configureEmptyCart() {
        configureCartIdentity();

        when(cart.getItems())
                .thenReturn(List.of());

        when(cart.isEmpty())
                .thenReturn(true);
    }

    /**
     * Configura un carrito con un producto.
     *
     * @param quantity cantidad del producto
     */
    private void configureCartWithItem(
            int quantity
    ) {
        configureCartIdentity();

        when(cart.getItems())
                .thenReturn(
                        List.of(cartItem)
                );

        when(cart.isEmpty())
                .thenReturn(false);

        when(cartItem.getProduct())
                .thenReturn(product);

        when(cartItem.getQuantity())
                .thenReturn(quantity);

        when(product.getProductId())
                .thenReturn(PRODUCT_ID);

        when(product.getProductType())
                .thenReturn(ProductType.PHYSICAL);

        when(product.getStatus())
                .thenReturn(ProductStatus.PUBLISHED);
    }

    /**
     * Configura los datos básicos del carrito.
     */
    private void configureCartIdentity() {
        when(cart.getCartId())
                .thenReturn(CART_ID);

        when(cart.getBuyer())
                .thenReturn(buyer);

        when(buyer.getIdentification())
                .thenReturn(
                        BUYER_IDENTIFICATION
                );
    }
}