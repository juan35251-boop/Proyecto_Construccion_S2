package application.services;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Product;
import application.ports.input.CartManagementUseCase;
import application.ports.output.CartRepository;

/**
 * Servicio de aplicación encargado de gestionar
 * el carrito de compras de un comprador.
 *
 * Implementa el puerto de entrada CartManagementUseCase,
 * permitiendo que un futuro controlador gestione el carrito
 * sin depender directamente de esta implementación.
 *
 * Permite crear un carrito, agregar productos, cambiar
 * cantidades y eliminar productos.
 */
public class CartManagementService
        implements CartManagementUseCase {

    private final CartRepository cartRepository;

    /**
     * Construye el servicio con el repositorio de carritos.
     *
     * @param cartRepository repositorio utilizado para guardar carritos
     */
    public CartManagementService(
            CartRepository cartRepository
    ) {
        validateRepository(cartRepository);
        this.cartRepository = cartRepository;
    }

    /**
     * Crea un carrito para un comprador autorizado.
     *
     * @param buyer comprador propietario del carrito
     * @return carrito creado
     */
    @Override
    public Cart createCart(Buyer buyer) {
        validateBuyerCanPurchase(buyer);

        Cart cart = new Cart(buyer);
        cartRepository.save(cart);

        return cart;
    }

    /**
     * Agrega un producto al carrito.
     *
     * Si el producto ya existe, Cart aumenta su cantidad
     * en lugar de crear un elemento duplicado.
     *
     * @param buyer comprador que realiza la operación
     * @param cart carrito que será modificado
     * @param product producto que se desea agregar
     * @param quantity cantidad que se desea agregar
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

        /*
         * Cart valida que el producto sea válido,
         * esté publicado y que la cantidad sea mayor que cero.
         */
        cart.addProduct(product, quantity);

        cartRepository.save(cart);

        return cart;
    }

    /**
     * Cambia la cantidad de un producto existente.
     *
     * @param buyer comprador que realiza la operación
     * @param cart carrito que será modificado
     * @param product producto cuya cantidad cambiará
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

        /*
         * Cart valida que el producto exista dentro
         * del carrito y que la nueva cantidad sea válida.
         */
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
     * @param buyer comprador que realiza la operación
     * @param cart carrito que será modificado
     * @param product producto que se desea eliminar
     * @return carrito actualizado
     */
    @Override
    public Cart removeProduct(
            Buyer buyer,
            Cart cart,
            Product product
    ) {
        validateCartOwner(buyer, cart);

        /*
         * Cart comprueba que el producto exista
         * antes de eliminarlo.
         */
        cart.removeProduct(product);

        cartRepository.save(cart);

        return cart;
    }

    /**
     * Valida que exista el repositorio de carritos.
     *
     * @param cartRepository repositorio de carritos
     */
    private void validateRepository(
            CartRepository cartRepository
    ) {
        if (cartRepository == null) {
            throw new IllegalArgumentException(
                    "Cart repository must not be null."
            );
        }
    }

    /**
     * Comprueba que el comprador pueda realizar compras.
     *
     * Para administrar un carrito, tanto su cuenta
     * como su estado comercial deben permitir compras.
     *
     * @param buyer comprador que realiza la operación
     */
    private void validateBuyerCanPurchase(Buyer buyer) {
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
     * Comprueba que el carrito exista y pertenezca
     * al comprador que realiza la operación.
     *
     * @param buyer comprador que realiza la operación
     * @param cart carrito que se desea administrar
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

        if (cart.getBuyer() != buyer) {
            throw new IllegalStateException(
                    "Buyer can only manage their own cart."
            );
        }
    }
}