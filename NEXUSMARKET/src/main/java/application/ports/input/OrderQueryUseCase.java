package application.ports.input;

import application.domain.models.Order;
import application.domain.models.User;

import java.util.List;

/**
 * Puerto de entrada que define las consultas
 * autorizadas sobre los pedidos.
 *
 * La información visible depende del rol
 * del usuario solicitante.
 */
public interface OrderQueryUseCase {

    /**
     * Consulta los pedidos visibles utilizando
     * directamente un usuario del dominio.
     *
     * @param requestedBy usuario solicitante
     * @return pedidos autorizados
     */
    List<Order> findAccessibleOrders(
            User requestedBy
    );

    /**
     * Consulta los pedidos visibles utilizando
     * la identificación recibida desde la API.
     *
     * @param requesterIdentification identificación del solicitante
     * @return pedidos autorizados
     */
    List<Order> findAccessibleOrdersByRequesterIdentification(
            String requesterIdentification
    );

    /**
     * Consulta un pedido específico y comprueba
     * que el solicitante tenga permiso para verlo.
     *
     * @param requesterIdentification identificación del solicitante
     * @param orderId identificador del pedido
     * @return pedido autorizado
     */
    Order findAccessibleOrderByIdentifiers(
            String requesterIdentification,
            String orderId
    );
}