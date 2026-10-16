package application.ports.input;

import application.domain.models.Order;
import application.domain.models.User;

import java.util.List;

/**
 * Puerto de entrada para consultar los pedidos
 * visibles para un usuario.
 */
public interface OrderQueryUseCase {

    /**
     * Obtiene los pedidos que el usuario tiene
     * autorización para consultar.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return pedidos autorizados para el usuario
     */
    List<Order> findAccessibleOrders(
            User requestedBy
    );
}