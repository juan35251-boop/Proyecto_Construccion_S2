package application.services;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Product;
import application.domain.models.User;
import application.ports.input.CartManagementUseCase;
import application.ports.output.CartRepository;
import application.ports.output.ProductQueryRepository;
import application.ports.output.UserRepository;

/**
 * Servicio de aplicación encargado de gestionar
 * el carrito de compras de un comprador.
 *
 * Permite crear y consultar el carrito, agregar productos,
 * cambiar cantidades y eliminar productos.
 *
 * La API trabaja con identificadores, mientras que los métodos
 * anteriores basados en objetos se conservan para mantener
 * compatibles las pruebas existentes.
 */
public class CartManagementService
        implements CartManagementUseCase {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductQueryRepository productQueryRepository;

    /**
     * Construye el servicio completo para ser utilizado
     * desde la API REST.
     *
     * @param cartRepository repositorio de carritos
     * @param userRepository repositorio de usuarios
     * @param productQueryRepository repositorio de productos
     */
    public CartManagementService(
            CartRepository cartRepository,
            UserRepository userRepository,
            ProductQueryRepository productQueryRepository
    ) {
        validateCartRepository(cartRepository);
        validateUserRepository(userRepository);
        validateProductRepository(
                productQueryRepository
        );

        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productQueryRepository =
                productQueryRepository;
    }

    /**
     * Constructor conservado para las pruebas anteriores.
     *
     * Este constructor solamente permite utilizar las operaciones
     * que reciben objetos completos del dominio.
     *
     * @param cartRepository repositorio de carritos
     */
    public CartManagementService(
            CartRepository cartRepository
    ) {
        validateCartRepository(cartRepository);

        this.cartRepository = cartRepository;
        this.userRepository = null;
        this.productQueryRepository = null;
    }

    /**
     * Crea un carrito mediante la identificación
     * del comprador.
     *
     * Un comprador solamente puede mantener
     * un carrito activo.
     *
     * @param buyerIdentification identificación del comprador
     * @return carrito creado
     */
    @Override
    public Cart createCartByBuyerIdentification(
            String buyerIdentification
    ) {
        validateApiRepositories();

        Buyer buyer =
                findBuyer(buyerIdentification);

        if (cartRepository
                .existsByBuyerIdentification(
                        buyerIdentification
                )) {
            throw new IllegalStateException(
                    "Buyer already has an active cart."
            );
        }

        return createCart(buyer);
    }

    /**
     * Consulta el carrito perteneciente al comprador.
     *
     * @param buyerIdentification identificación del comprador
     * @return carrito encontrado
     */
    @Override
    public Cart findCartByBuyerIdentification(
            String buyerIdentification
    ) {
        validateApiRepositories();

        Buyer buyer =
                findBuyer(buyerIdentification);

        validateBuyerCanPurchase(buyer);

        Cart cart =
                cartRepository
                        .findByBuyerIdentification(
                                buyerIdentification
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Buyer cart was not found."
                                )
                        );

        validateCartOwner(buyer, cart);

        return cart;
    }

    /**
     * Agrega un producto mediante identificadores.
     *
     * @param buyerIdentification identificación del comprador
     * @param cartId identificador del carrito
     * @param productId identificador del producto
     * @param quantity cantidad agregada
     * @return carrito actualizado
     */
    @Override
    public Cart addProductByIdentifiers(
            String buyerIdentification,
            String cartId,
            String productId,
            int quantity
    ) {
        validateApiRepositories();

        Buyer buyer =
                findBuyer(buyerIdentification);

        Cart cart =
                findCart(cartId);

        Product product =
                findProduct(productId);

        return addProduct(
                buyer,
                cart,
                product,
                quantity
        );
    }

    /**
     * Cambia la cantidad mediante identificadores.
     *
     * @param buyerIdentification identificación del comprador
     * @param cartId identificador del carrito
     * @param productId identificador del producto
     * @param newQuantity nueva cantidad
     * @return carrito actualizado
     */
    @Override
    public Cart changeProductQuantityByIdentifiers(
            String buyerIdentification,
            String cartId,
            String productId,
            int newQuantity
    ) {
        validateApiRepositories();

        Buyer buyer =
                findBuyer(buyerIdentification);

        Cart cart =
                findCart(cartId);

        Product product =
                findProduct(productId);

        return changeProductQuantity(
                buyer,
                cart,
                product,
                newQuantity
        );
    }

    /**
     * Elimina un producto mediante identificadores.
     *
     * @param buyerIdentification identificación del comprador
     * @param cartId identificador del carrito
     * @param productId identificador del producto
     * @return carrito actualizado
     */
    @Override
    public Cart removeProductByIdentifiers(
            String buyerIdentification,
            String cartId,
            String productId
    ) {
        validateApiRepositories();

        Buyer buyer =
                findBuyer(buyerIdentification);

        Cart cart =
                findCart(cartId);

        Product product =
                findProduct(productId);

        return removeProduct(
                buyer,
                cart,
                product
        );
    }

    /**
     * Crea un carrito para un comprador autorizado.
     *
     * @param buyer comprador propietario
     * @return carrito creado
     */
    @Override
    public Cart createCart(
            Buyer buyer
    ) {
        validateBuyerCanPurchase(buyer);

        Cart cart = new Cart(buyer);
        cartRepository.save(cart);

        return cart;
    }

    /**
     * Agrega un producto al carrito.
     *
     * @param buyer comprador responsable
     * @param cart carrito modificado
     * @param product producto agregado
     * @param quantity cantidad agregada
     * @return carrito actualizado
     */
    @Override
    public Cart addProduct(
            Buyer buyer,
            Cart cart,
            Product product,
            int quantity
    ) {
        validateCartOwner(buyer, cart);

        cart.addProduct(
                product,
                quantity
        );

        cartRepository.save(cart);

        return cart;
    }

    /**
     * Cambia la cantidad de un producto.
     *
     * @param buyer comprador responsable
     * @param cart carrito modificado
     * @param product producto modificado
     * @param newQuantity nueva cantidad
     * @return carrito actualizado
     */
    @Override
    public Cart changeProductQuantity(
            Buyer buyer,
            Cart cart,
            Product product,
            int newQuantity
    ) {
        validateCartOwner(buyer, cart);

        cart.changeProductQuantity(
                product,
                newQuantity
        );

        cartRepository.save(cart);

        return cart;
    }

    /**
     * Elimina un producto del carrito.
     *
     * @param buyer comprador responsable
     * @param cart carrito modificado
     * @param product producto eliminado
     * @return carrito actualizado
     */
    @Override
    public Cart removeProduct(
            Buyer buyer,
            Cart cart,
            Product product
    ) {
        validateCartOwner(buyer, cart);

        cart.removeProduct(product);
        cartRepository.save(cart);

        return cart;
    }

    /**
     * Busca y valida un comprador.
     */
    private Buyer findBuyer(
            String identification
    ) {
        validateText(
                identification,
                "Buyer identification"
        );

        User user =
                userRepository
                        .findByIdentification(
                                identification
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Buyer was not found."
                                )
                        );

        if (!(user instanceof Buyer buyer)) {
            throw new IllegalStateException(
                    "Cart owner must be a buyer."
            );
        }

        return buyer;
    }

    /**
     * Busca un carrito mediante su identificador.
     */
    private Cart findCart(
            String cartId
    ) {
        validateText(
                cartId,
                "Cart identifier"
        );

        return cartRepository
                .findById(cartId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Cart was not found."
                        )
                );
    }

    /**
     * Busca un producto mediante su identificador.
     */
    private Product findProduct(
            String productId
    ) {
        validateText(
                productId,
                "Product identifier"
        );

        return productQueryRepository
                .findById(productId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Product was not found."
                        )
                );
    }

    /**
     * Comprueba que el comprador pueda comprar.
     */
    private void validateBuyerCanPurchase(
            Buyer buyer
    ) {
        if (buyer == null) {
            throw new IllegalArgumentException(
                    "Cart must belong to a buyer."
            );
        }

        if (!buyer.canPurchase()) {
            throw new IllegalStateException(
                    "Buyer is not authorized to manage a cart."
            );
        }
    }

    /**
     * Comprueba que el carrito pertenezca
     * al comprador solicitante.
     *
     * La comparación utiliza la identificación,
     * permitiendo trabajar con objetos reconstruidos
     * desde la base de datos.
     */
    private void validateCartOwner(
            Buyer buyer,
            Cart cart
    ) {
        validateBuyerCanPurchase(buyer);

        if (cart == null) {
            throw new IllegalArgumentException(
                    "Cart must not be null."
            );
        }

        String requesterIdentification =
                buyer.getIdentification();

        String ownerIdentification =
                cart.getBuyer()
                        .getIdentification();

        if (!requesterIdentification.equals(
                ownerIdentification
        )) {
            throw new IllegalStateException(
                    "Buyer can only manage their own cart."
            );
        }
    }

    /**
     * Valida el repositorio de carritos.
     */
    private void validateCartRepository(
            CartRepository cartRepository
    ) {
        if (cartRepository == null) {
            throw new IllegalArgumentException(
                    "Cart repository must not be null."
            );
        }
    }

    /**
     * Valida el repositorio de usuarios.
     */
    private void validateUserRepository(
            UserRepository userRepository
    ) {
        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }
    }

    /**
     * Valida el repositorio de productos.
     */
    private void validateProductRepository(
            ProductQueryRepository productQueryRepository
    ) {
        if (productQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Product query repository must not be null."
            );
        }
    }

    /**
     * Valida que los repositorios requeridos por la API
     * hayan sido configurados.
     */
    private void validateApiRepositories() {
        if (userRepository == null
                || productQueryRepository == null) {
            throw new IllegalStateException(
                    "Cart lookup repositories are not configured."
            );
        }
    }

    /**
     * Valida un texto obligatorio.
     */
    private void validateText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }
    }
}