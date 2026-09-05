package application.ports.output;

import application.domain.models.Cart;

/**
 * Puerto de salida encargado de guardar
 * los carritos de compras.
 */
public interface CartRepository {

    /**
     * Guarda o actualiza un carrito.
     *
     * @param cart carrito que se desea almacenar
     */
    void save(Cart cart);
}