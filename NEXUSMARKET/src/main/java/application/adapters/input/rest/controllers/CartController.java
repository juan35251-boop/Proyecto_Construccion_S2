package application.adapters.input.rest.controllers;

import application.adapters.input.rest.dto.CartProductRequest;
import application.adapters.input.rest.dto.CartQuantityRequest;
import application.adapters.input.rest.dto.CartResponse;
import application.domain.models.Cart;
import application.ports.input.CartManagementUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST encargado de administrar
 * el carrito de compras.
 *
 * El comprador se identifica temporalmente mediante
 * el encabezado X-Requester-Identification.
 *
 * Solamente un comprador activo y habilitado comercialmente
 * puede crear y modificar su propio carrito.
 */
@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    private static final String REQUESTER_HEADER =
            "X-Requester-Identification";

    private final CartManagementUseCase
            cartManagementUseCase;

    /**
     * Construye el controlador con el caso de uso
     * requerido.
     *
     * @param cartManagementUseCase caso de uso del carrito
     */
    public CartController(
            CartManagementUseCase cartManagementUseCase
    ) {
        validateCartManagementUseCase(
                cartManagementUseCase
        );

        this.cartManagementUseCase =
                cartManagementUseCase;
    }

    /**
     * Crea un carrito para el comprador solicitante.
     *
     * Cada comprador solamente puede mantener
     * un carrito activo.
     *
     * @param requesterIdentification identificación del comprador
     * @return carrito creado
     */
    @PostMapping
    public ResponseEntity<CartResponse> createCart(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification
    ) {
        Cart cart =
                cartManagementUseCase
                        .createCartByBuyerIdentification(
                                requesterIdentification
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        CartResponse.from(cart)
                );
    }

    /**
     * Consulta el carrito propio del comprador.
     *
     * @param requesterIdentification identificación del comprador
     * @return carrito encontrado
     */
    @GetMapping("/mine")
    public ResponseEntity<CartResponse> findOwnCart(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification
    ) {
        Cart cart =
                cartManagementUseCase
                        .findCartByBuyerIdentification(
                                requesterIdentification
                        );

        return ResponseEntity.ok(
                CartResponse.from(cart)
        );
    }

    /**
     * Agrega un producto al carrito.
     *
     * Si el producto ya existe, aumenta la cantidad
     * del elemento actual.
     *
     * @param requesterIdentification identificación del comprador
     * @param cartId identificador del carrito
     * @param request producto y cantidad
     * @return carrito actualizado
     */
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartResponse> addProduct(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification,

            @PathVariable
            String cartId,

            @Valid
            @RequestBody
            CartProductRequest request
    ) {
        Cart cart =
                cartManagementUseCase
                        .addProductByIdentifiers(
                                requesterIdentification,
                                cartId,
                                request.productId(),
                                request.quantity()
                        );

        return ResponseEntity.ok(
                CartResponse.from(cart)
        );
    }

    /**
     * Cambia la cantidad solicitada
     * de un producto existente.
     *
     * @param requesterIdentification identificación del comprador
     * @param cartId identificador del carrito
     * @param productId identificador del producto
     * @param request nueva cantidad
     * @return carrito actualizado
     */
    @PatchMapping(
            "/{cartId}/items/{productId}"
    )
    public ResponseEntity<CartResponse>
    changeProductQuantity(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification,

            @PathVariable
            String cartId,

            @PathVariable
            String productId,

            @Valid
            @RequestBody
            CartQuantityRequest request
    ) {
        Cart cart =
                cartManagementUseCase
                        .changeProductQuantityByIdentifiers(
                                requesterIdentification,
                                cartId,
                                productId,
                                request.newQuantity()
                        );

        return ResponseEntity.ok(
                CartResponse.from(cart)
        );
    }

    /**
     * Elimina un producto del carrito.
     *
     * Se devuelve el carrito actualizado para que el cliente
     * conozca inmediatamente los productos restantes.
     *
     * @param requesterIdentification identificación del comprador
     * @param cartId identificador del carrito
     * @param productId identificador del producto
     * @return carrito actualizado
     */
    @DeleteMapping(
            "/{cartId}/items/{productId}"
    )
    public ResponseEntity<CartResponse> removeProduct(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification,

            @PathVariable
            String cartId,

            @PathVariable
            String productId
    ) {
        Cart cart =
                cartManagementUseCase
                        .removeProductByIdentifiers(
                                requesterIdentification,
                                cartId,
                                productId
                        );

        return ResponseEntity.ok(
                CartResponse.from(cart)
        );
    }

    /**
     * Valida que el caso de uso exista.
     *
     * @param useCase caso de uso validado
     */
    private void validateCartManagementUseCase(
            CartManagementUseCase useCase
    ) {
        if (useCase == null) {
            throw new IllegalArgumentException(
                    "Cart management use case must not be null."
            );
        }
    }
}