package application.services;

import application.domain.models.LogisticsOperator;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.ports.output.OrderRepository;
import application.ports.output.ShipmentRepository;

/**
 * Servicio de aplicación encargado de gestionar los envíos.
 *
 * Permite crear, despachar y confirmar la entrega de un envío.
 * Estas operaciones solamente pueden ser realizadas por un
 * operador logístico activo.
 */
public class ShipmentManagementService {

    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;

    /**
     * Construye el servicio con los repositorios necesarios.
     *
     * @param shipmentRepository repositorio utilizado para guardar envíos
     * @param orderRepository repositorio utilizado para actualizar pedidos
     */
    public ShipmentManagementService(
            ShipmentRepository shipmentRepository,
            OrderRepository orderRepository
    ) {
        validateRepositories(
                shipmentRepository,
                orderRepository
        );

        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Crea un envío para un pedido físico pagado.
     *
     * @param operator operador logístico responsable
     * @param order pedido que será enviado
     * @return envío creado
     */
    public Shipment createShipment(
            LogisticsOperator operator,
            Order order
    ) {
        validateOperator(operator);

        /*
         * Shipment comprueba que el pedido exista, contenga
         * productos físicos y se encuentre pagado.
         */
        Shipment shipment = new Shipment(order);

        shipmentRepository.save(shipment);

        return shipment;
    }

    /**
     * Registra el despacho físico de un envío.
     *
     * La operación cambia el pedido asociado de PAID
     * a DISPATCHED.
     *
     * @param operator operador logístico responsable
     * @param shipment envío que será despachado
     */
    public void dispatchShipment(
            LogisticsOperator operator,
            Shipment shipment
    ) {
        validateOperator(operator);
        validateShipment(shipment);

        shipment.dispatch();

        saveShipmentAndOrder(shipment);
    }

    /**
     * Confirma que el envío fue entregado.
     *
     * La operación cambia el pedido asociado de DISPATCHED
     * a DELIVERED.
     *
     * @param operator operador logístico responsable
     * @param shipment envío cuya entrega se confirma
     */
    public void confirmDelivery(
            LogisticsOperator operator,
            Shipment shipment
    ) {
        validateOperator(operator);
        validateShipment(shipment);

        shipment.confirmDelivery();

        saveShipmentAndOrder(shipment);
    }

    /**
     * Guarda el envío y el nuevo estado de su pedido.
     */
    private void saveShipmentAndOrder(
            Shipment shipment
    ) {
        Order order = shipment.getOrder();

        orderRepository.save(order);
        shipmentRepository.save(shipment);
    }

    /**
     * Valida que los repositorios requeridos existan.
     */
    private void validateRepositories(
            ShipmentRepository shipmentRepository,
            OrderRepository orderRepository
    ) {
        if (shipmentRepository == null) {
            throw new IllegalArgumentException(
                    "Shipment repository must not be null."
            );
        }

        if (orderRepository == null) {
            throw new IllegalArgumentException(
                    "Order repository must not be null."
            );
        }
    }

    /**
     * Valida que la operación sea realizada por
     * un operador logístico activo.
     */
    private void validateOperator(
            LogisticsOperator operator
    ) {
        if (operator == null) {
            throw new IllegalArgumentException(
                    "Shipment operation requires a logistics operator."
            );
        }

        if (!operator.isActive()) {
            throw new IllegalStateException(
                    "Only an active logistics operator can manage shipments."
            );
        }
    }

    /**
     * Valida que exista un envío.
     */
    private void validateShipment(Shipment shipment) {
        if (shipment == null) {
            throw new IllegalArgumentException(
                    "Shipment must not be null."
            );
        }
    }
}