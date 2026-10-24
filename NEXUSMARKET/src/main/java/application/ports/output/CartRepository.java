package application.ports.output;

import application.domain.models.Cart;

import java.util.Optional;

/**
 * Puerto de salida encargado de almacenar
 * y consultar carritos de compra.
 *
 * El dominio no conoce si la implementación utiliza
 * MySQL, memoria u otra tecnología.
 */
public interface CartRepository {

    /**
     * Guarda o actualiza un carrito.
     *
     * @param cart carrito almacenado
     */
    void save(Cart cart);

    /**
     * Busca un carrito mediante su identificador.
     *
     * La implementación predeterminada conserva compatibilidad
     * con los repositorios de prueba existentes.
     *
     * @param cartId identificador del carrito
     * @return carrito encontrado o resultado vacío
     */
    default Optional<Cart> findById(
            String cartId
    ) {
        return Optional.empty();
    }

    /**
     * Busca el carrito perteneciente a un comprador.
     *
     * @param buyerIdentification identificación del comprador
     * @return carrito encontrado o resultado vacío
     */
    default Optional<Cart> findByBuyerIdentification(
            String buyerIdentification
    ) {
        return Optional.empty();
    }

    /**
     * Comprueba si el comprador ya posee un carrito.
     *
     * @param buyerIdentification identificación del comprador
     * @return true si ya tiene un carrito
     */
    default boolean existsByBuyerIdentification(
            String buyerIdentification
    ) {
        return findByBuyerIdentification(
                buyerIdentification
        ).isPresent();
    }
}