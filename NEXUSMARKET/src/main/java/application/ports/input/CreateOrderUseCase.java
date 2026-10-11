package application.ports.input;

import application.domain.models.Cart;
import application.domain.models.Order;

/**
 * Puerto de entrada que define el caso de uso
 * para crear pedidos en NexusMarket.
 *
 * Convierte el contenido de un carrito en un compromiso
 * comercial formal pendiente de pago.
 */
public interface CreateOrderUseCase {

    /**
     * Crea un pedido a partir de un carrito.
     *
     * El carrito debe existir, contener productos y
     * pertenecer a un comprador autorizado para comprar.
     *
     * Todos los productos deben continuar publicados
     * cuando se confirme la creación del pedido.
     *
     * @param cart carrito que se desea confirmar
     * @return pedido creado
     *
     * @throws IllegalArgumentException si el carrito es nulo
     * @throws IllegalStateException si el carrito está vacío,
     *         el comprador no puede comprar o algún producto
     *         ya no está publicado
     */
    Order create(Cart cart);
}