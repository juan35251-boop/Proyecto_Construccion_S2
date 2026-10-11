package application.ports.input;

import application.domain.models.Order;
import application.domain.models.User;

import java.util.List;

/**
 * Puerto de entrada que define el caso de uso
 * para consultar pedidos.
 *
 * Los pedidos visibles dependen del rol y las
 * responsabilidades del usuario solicitante.
 */
public interface OrderQueryUseCase {

    /**
     * Obtiene los pedidos visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return lista de pedidos autorizados
     */
    List<Order> findAccessibleOrders(
            User requestedBy
    );
}