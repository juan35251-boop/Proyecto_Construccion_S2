package application.ports.input;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Product;

/**
 * Puerto de entrada que define el caso de uso
 * para administrar el carrito de compras.
 *
 * Permite crear un carrito, agregar productos,
 * cambiar cantidades y eliminar productos.
 */
public interface CartManagementUseCase {

    /**
     * Crea un carrito para un comprador autorizado.
     *
     * @param buyer comprador propietario del carrito
     * @return carrito creado
     *
     * @throws IllegalArgumentException si el comprador es nulo
     * @throws IllegalStateException si el comprador no puede comprar
     */
    Cart createCart(Buyer buyer);

    /**
     * Agrega un producto al carrito.
     *
     * Si el producto ya existe, aumenta su cantidad
     * en lugar de crear un elemento duplicado.
     *
     * @param buyer comprador que realiza la operación
     * @param cart carrito que será modificado
     * @param product producto que se desea agregar
     * @param quantity cantidad que se desea agregar
     * @return carrito actualizado
     *
     * @throws IllegalArgumentException si algún dato es inválido
     * @throws IllegalStateException si el comprador no está autorizado
     */
    Cart addProduct(
            Buyer buyer,
            Cart cart,
            Product product,
            int quantity
    );

    /**
     * Cambia la cantidad de un producto existente.
     *
     * @param buyer comprador que realiza la operación
     * @param cart carrito que será modificado
     * @param product producto cuya cantidad cambiará
     * @param newQuantity nueva cantidad
     * @return carrito actualizado
     *
     * @throws IllegalArgumentException si algún dato es inválido
     * @throws IllegalStateException si el comprador no está autorizado
     */
    Cart changeProductQuantity(
            Buyer buyer,
            Cart cart,
            Product product,
            int newQuantity
    );

    /**
     * Elimina un producto del carrito.
     *
     * @param buyer comprador que realiza la operación
     * @param cart carrito que será modificado
     * @param product producto que se desea eliminar
     * @return carrito actualizado
     *
     * @throws IllegalArgumentException si algún dato es inválido
     * @throws IllegalStateException si el comprador no está autorizado
     */
    Cart removeProduct(
            Buyer buyer,
            Cart cart,
            Product product
    );
}