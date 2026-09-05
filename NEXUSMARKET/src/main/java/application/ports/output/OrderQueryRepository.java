package application.ports.output;

import application.domain.models.Order;

import java.util.List;

/**
 * Puerto de salida utilizado para consultar pedidos.
 *
 * Los permisos y filtros correspondientes a cada rol
 * son responsabilidad del servicio de aplicación.
 */
public interface OrderQueryRepository {

    /**
     * Obtiene todos los pedidos registrados.
     *
     * @return lista de pedidos
     */
    List<Order> findAll();
}