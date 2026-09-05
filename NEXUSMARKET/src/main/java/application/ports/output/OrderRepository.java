package application.ports.output;

import application.domain.models.Order;

/**
 * Puerto de salida encargado de almacenar los pedidos.
 *
 * La implementación concreta podrá utilizar posteriormente
 * una base de datos, pero el servicio no necesita conocerla.
 */
public interface OrderRepository {

    /**
     * Guarda o actualiza un pedido.
     *
     * @param order pedido que se desea almacenar
     */
    void save(Order order);
}