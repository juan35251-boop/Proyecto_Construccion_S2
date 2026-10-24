package application.ports.input;

import application.domain.models.LogisticsOperator;
import application.domain.models.Order;
import application.domain.models.Shipment;

/**
 * Puerto de entrada para administrar envíos físicos.
 *
 * Únicamente un operador logístico activo puede crear,
 * despachar o confirmar la entrega de un envío.
 */
public interface ShipmentManagementUseCase {

    /**
     * Crea un envío utilizando objetos del dominio.
     *
     * @param operator operador logístico
     * @param order pedido físico pagado
     * @return envío creado
     */
    Shipment createShipment(
            LogisticsOperator operator,
            Order order
    );

    /**
     * Despacha un envío utilizando objetos del dominio.
     *
     * @param operator operador logístico
     * @param shipment envío que será despachado
     */
    void dispatchShipment(
            LogisticsOperator operator,
            Shipment shipment
    );

    /**
     * Confirma la entrega utilizando objetos del dominio.
     *
     * @param operator operador logístico
     * @param shipment envío entregado
     */
    void confirmDelivery(
            LogisticsOperator operator,
            Shipment shipment
    );

    /**
     * Crea un envío mediante identificadores.
     *
     * @param operatorIdentification identificación del operador
     * @param orderId identificador del pedido
     * @return envío creado
     */
    Shipment createShipmentByIdentifiers(
            String operatorIdentification,
            String orderId
    );

    /**
     * Despacha un envío mediante identificadores.
     *
     * @param operatorIdentification identificación del operador
     * @param shipmentId identificador del envío
     * @return envío actualizado
     */
    Shipment dispatchShipmentByIdentifiers(
            String operatorIdentification,
            String shipmentId
    );

    /**
     * Confirma la entrega mediante identificadores.
     *
     * @param operatorIdentification identificación del operador
     * @param shipmentId identificador del envío
     * @return envío actualizado
     */
    Shipment confirmDeliveryByIdentifiers(
            String operatorIdentification,
            String shipmentId
    );
}