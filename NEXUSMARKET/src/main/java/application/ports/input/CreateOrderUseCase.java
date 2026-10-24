package application.ports.input;

import application.domain.models.Cart;
import application.domain.models.Order;

/**
 * Puerto de entrada que define el caso de uso
 * para crear pedidos en NexusMarket.
 *
 * Un pedido se crea a partir de un carrito válido
 * perteneciente a un comprador.
 */
public interface CreateOrderUseCase {

    /**
     * Crea un pedido utilizando directamente
     * un carrito del dominio.
     *
     * Este método se conserva para las pruebas unitarias
     * y para operaciones internas de la aplicación.
     *
     * @param cart carrito que será convertido en pedido
     * @return pedido creado
     */
    Order create(Cart cart);

    /**
     * Crea un pedido utilizando los identificadores
     * recibidos desde la API REST.
     *
     * El carrito debe existir y pertenecer al comprador
     * que realiza la operación.
     *
     * @param buyerIdentification identificación del comprador
     * @param cartId identificador del carrito
     * @return pedido creado
     *
     * @throws IllegalArgumentException si algún identificador
     *         es nulo o está vacío
     * @throws IllegalStateException si el carrito no existe
     *         o no pertenece al comprador
     */
    Order createByCartId(
            String buyerIdentification,
            String cartId
    );
}