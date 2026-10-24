package application.ports.input;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Product;

/**
 * Puerto de entrada que define las operaciones disponibles
 * para administrar el carrito de compras.
 *
 * El comprador solamente puede administrar su propio carrito
 * y debe encontrarse habilitado para realizar compras.
 */
public interface CartManagementUseCase {

    /**
     * Crea un carrito utilizando la identificación
     * del comprador.
     *
     * Esta operación está diseñada para la API REST.
     *
     * @param buyerIdentification identificación del comprador
     * @return carrito creado
     */
    Cart createCartByBuyerIdentification(
            String buyerIdentification
    );

    /**
     * Consulta el carrito perteneciente al comprador.
     *
     * @param buyerIdentification identificación del comprador
     * @return carrito encontrado
     */
    Cart findCartByBuyerIdentification(
            String buyerIdentification
    );

    /**
     * Agrega un producto utilizando identificadores.
     *
     * @param buyerIdentification identificación del comprador
     * @param cartId identificador del carrito
     * @param productId identificador del producto
     * @param quantity cantidad agregada
     * @return carrito actualizado
     */
    Cart addProductByIdentifiers(
            String buyerIdentification,
            String cartId,
            String productId,
            int quantity
    );

    /**
     * Cambia la cantidad de un producto utilizando
     * identificadores.
     *
     * @param buyerIdentification identificación del comprador
     * @param cartId identificador del carrito
     * @param productId identificador del producto
     * @param newQuantity nueva cantidad
     * @return carrito actualizado
     */
    Cart changeProductQuantityByIdentifiers(
            String buyerIdentification,
            String cartId,
            String productId,
            int newQuantity
    );

    /**
     * Elimina un producto utilizando identificadores.
     *
     * @param buyerIdentification identificación del comprador
     * @param cartId identificador del carrito
     * @param productId identificador del producto
     * @return carrito actualizado
     */
    Cart removeProductByIdentifiers(
            String buyerIdentification,
            String cartId,
            String productId
    );

    /**
     * Crea un carrito utilizando un comprador
     * previamente localizado.
     *
     * Este método se conserva para mantener compatibilidad
     * con las pruebas y componentes existentes.
     *
     * @param buyer comprador propietario
     * @return carrito creado
     */
    Cart createCart(Buyer buyer);

    /**
     * Agrega un producto utilizando objetos del dominio.
     *
     * @param buyer comprador responsable
     * @param cart carrito modificado
     * @param product producto agregado
     * @param quantity cantidad agregada
     * @return carrito actualizado
     */
    Cart addProduct(
            Buyer buyer,
            Cart cart,
            Product product,
            int quantity
    );

    /**
     * Cambia la cantidad utilizando objetos del dominio.
     *
     * @param buyer comprador responsable
     * @param cart carrito modificado
     * @param product producto modificado
     * @param newQuantity nueva cantidad
     * @return carrito actualizado
     */
    Cart changeProductQuantity(
            Buyer buyer,
            Cart cart,
            Product product,
            int newQuantity
    );

    /**
     * Elimina un producto utilizando objetos del dominio.
     *
     * @param buyer comprador responsable
     * @param cart carrito modificado
     * @param product producto eliminado
     * @return carrito actualizado
     */
    Cart removeProduct(
            Buyer buyer,
            Cart cart,
            Product product
    );
}