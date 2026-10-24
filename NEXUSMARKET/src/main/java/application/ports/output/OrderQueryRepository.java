package application.ports.output;

import application.domain.models.Order;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida utilizado para consultar pedidos.
 *
 * Los permisos correspondientes a cada usuario
 * son aplicados por los servicios de aplicación.
 */
public interface OrderQueryRepository {

    /**
     * Busca un pedido mediante su identificador.
     *
     * @param orderId identificador del pedido
     * @return pedido encontrado o resultado vacío
     */
    default Optional<Order> findById(
            String orderId
    ) {
        return Optional.empty();
    }

    /**
     * Obtiene los pedidos de un comprador.
     *
     * @param buyerIdentification identificación del comprador
     * @return pedidos pertenecientes al comprador
     */
    default List<Order> findByBuyerIdentification(
            String buyerIdentification
    ) {
        return List.of();
    }

    /**
     * Obtiene todos los pedidos registrados.
     *
     * @return lista de pedidos
     */
    List<Order> findAll();
}