package application.ports.output;

import application.domain.models.Order;

import java.util.Optional;

/**
 * Puerto de salida encargado de guardar
 * y localizar pedidos.
 */
public interface OrderRepository {

    /**
     * Guarda o actualiza un pedido.
     *
     * @param order pedido almacenado
     */
    void save(Order order);

    /**
     * Busca un pedido mediante su identificador.
     *
     * La implementación predeterminada mantiene compatibles
     * los repositorios de prueba existentes.
     *
     * @param orderId identificador del pedido
     * @return pedido encontrado o resultado vacío
     */
    default Optional<Order> findById(
            String orderId
    ) {
        return Optional.empty();
    }
}