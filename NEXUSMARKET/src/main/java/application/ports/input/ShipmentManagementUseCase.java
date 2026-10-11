package application.ports.input;

import application.domain.models.LogisticsOperator;
import application.domain.models.Order;
import application.domain.models.Shipment;

/**
 * Puerto de entrada que define el caso de uso
 * para administrar los envíos de NexusMarket.
 *
 * Permite crear, despachar y confirmar la entrega
 * de un envío físico.
 *
 * Estas operaciones solamente pueden ser realizadas
 * por un operador logístico activo.
 */
public interface ShipmentManagementUseCase {

    /**
     * Crea un envío para un pedido físico pagado.
     *
     * @param operator operador logístico responsable
     * @param order pedido que será enviado
     * @return envío creado
     *
     * @throws IllegalArgumentException si el operador
     *         o el pedido son nulos
     * @throws IllegalStateException si el operador está
     *         inactivo, el pedido no fue pagado o no
     *         contiene productos físicos
     */
    Shipment createShipment(
            LogisticsOperator operator,
            Order order
    );

    /**
     * Registra el despacho físico de un envío.
     *
     * La operación cambia el pedido asociado
     * de PAID a DISPATCHED.
     *
     * @param operator operador logístico responsable
     * @param shipment envío que será despachado
     *
     * @throws IllegalArgumentException si el operador
     *         o el envío son nulos
     * @throws IllegalStateException si el operador está
     *         inactivo o el envío no puede despacharse
     */
    void dispatchShipment(
            LogisticsOperator operator,
            Shipment shipment
    );

    /**
     * Confirma que el envío fue entregado.
     *
     * La operación cambia el pedido asociado
     * de DISPATCHED a DELIVERED.
     *
     * @param operator operador logístico responsable
     * @param shipment envío cuya entrega se confirma
     *
     * @throws IllegalArgumentException si el operador
     *         o el envío son nulos
     * @throws IllegalStateException si el operador está
     *         inactivo o el envío no fue despachado
     */
    void confirmDelivery(
            LogisticsOperator operator,
            Shipment shipment
    );
}