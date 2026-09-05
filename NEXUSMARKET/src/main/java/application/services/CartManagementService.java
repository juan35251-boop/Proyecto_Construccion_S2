package application.services;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Product;
import application.ports.output.CartRepository;

/**
 * Servicio de aplicación encargado de gestionar
 * el carrito de compras de un comprador.
 *
 * Permite crear un carrito, agregar productos, cambiar
 * cantidades y eliminar productos.
 */
public class CartManagementService {

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
    public Cart addProduct(
            Buyer buyer,
            Cart cart,
            Product product,
            int quantity
    ) {
        validateCartOwner(buyer, cart);

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
    public Cart changeProductQuantity(
            Buyer buyer,
            Cart cart,
            Product product,
            int newQuantity
    ) {
        validateCartOwner(buyer, cart);

        cart.changeProductQuantity(product, newQuantity);
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
     * Valida que exista el repositorio de carritos.
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