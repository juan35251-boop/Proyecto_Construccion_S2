package application.services;

import application.domain.models.LogisticsOperator;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.ports.input.ShipmentManagementUseCase;
import application.ports.output.OrderRepository;
import application.ports.output.ShipmentRepository;
import application.ports.output.UserRepository;

/**
 * Servicio de aplicación encargado de gestionar
 * los envíos físicos.
 *
 * Permite:
 *
 * - Crear un envío para un pedido físico pagado.
 * - Despachar el envío.
 * - Confirmar su entrega.
 *
 * Todas estas operaciones solamente pueden ser realizadas
 * por un operador logístico activo.
 */
public class ShipmentManagementService
        implements ShipmentManagementUseCase {

    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    /**
     * Construye el servicio para operaciones que ya
     * proporcionan objetos del dominio.
     *
     * Este constructor se conserva para las pruebas
     * unitarias existentes.
     *
     * @param shipmentRepository repositorio de envíos
     * @param orderRepository repositorio de pedidos
     */
    public ShipmentManagementService(
            ShipmentRepository shipmentRepository,
            OrderRepository orderRepository
    ) {
        validateShipmentRepository(shipmentRepository);
        validateOrderRepository(orderRepository);

        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = null;
    }

    /**
     * Construye el servicio completo utilizado por la API.
     *
     * @param shipmentRepository repositorio de envíos
     * @param orderRepository repositorio de pedidos
     * @param userRepository repositorio de usuarios
     */
    public ShipmentManagementService(
            ShipmentRepository shipmentRepository,
            OrderRepository orderRepository,
            UserRepository userRepository
    ) {
        validateShipmentRepository(shipmentRepository);
        validateOrderRepository(orderRepository);
        validateUserRepository(userRepository);

        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    /**
     * Crea un envío para un pedido físico pagado.
     *
     * Shipment comprueba que:
     *
     * - El pedido exista.
     * - Contenga productos físicos.
     * - Se encuentre en estado PAID.
     *
     * @param operator operador logístico responsable
     * @param order pedido que será enviado
     * @return envío creado
     */
    @Override
    public Shipment createShipment(
            LogisticsOperator operator,
            Order order
    ) {
        validateOperator(operator);
        validateOrder(order);
        validateOrderWithoutShipment(order);

        Shipment shipment = new Shipment(order);

        shipmentRepository.save(shipment);

        return shipment;
    }

    /**
     * Registra el despacho físico.
     *
     * El pedido cambia desde PAID hasta DISPATCHED.
     *
     * @param operator operador logístico responsable
     * @param shipment envío que será despachado
     */
    @Override
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
     * Confirma la entrega física.
     *
     * El pedido cambia desde DISPATCHED hasta DELIVERED.
     *
     * @param operator operador logístico responsable
     * @param shipment envío entregado
     */
    @Override
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
     * Crea un envío localizando al operador
     * y al pedido mediante sus identificadores.
     *
     * @param operatorIdentification identificación del operador
     * @param orderId identificador del pedido
     * @return envío creado
     */
    @Override
    public Shipment createShipmentByIdentifiers(
            String operatorIdentification,
            String orderId
    ) {
        LogisticsOperator operator =
                findLogisticsOperator(
                        operatorIdentification
                );

        validateText(
                orderId,
                "Order identifier"
        );

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Order was not found."
                        )
                );

        return createShipment(
                operator,
                order
        );
    }

    /**
     * Despacha un envío localizando sus datos
     * mediante identificadores.
     *
     * @param operatorIdentification identificación del operador
     * @param shipmentId identificador del envío
     * @return envío actualizado
     */
    @Override
    public Shipment dispatchShipmentByIdentifiers(
            String operatorIdentification,
            String shipmentId
    ) {
        LogisticsOperator operator =
                findLogisticsOperator(
                        operatorIdentification
                );

        Shipment shipment =
                findShipment(shipmentId);

        dispatchShipment(
                operator,
                shipment
        );

        return shipment;
    }

    /**
     * Confirma la entrega localizando el envío
     * mediante su identificador.
     *
     * @param operatorIdentification identificación del operador
     * @param shipmentId identificador del envío
     * @return envío actualizado
     */
    @Override
    public Shipment confirmDeliveryByIdentifiers(
            String operatorIdentification,
            String shipmentId
    ) {
        LogisticsOperator operator =
                findLogisticsOperator(
                        operatorIdentification
                );

        Shipment shipment =
                findShipment(shipmentId);

        confirmDelivery(
                operator,
                shipment
        );

        return shipment;
    }

    /**
     * Localiza y valida al operador logístico.
     */
    private LogisticsOperator findLogisticsOperator(
            String operatorIdentification
    ) {
        validateText(
                operatorIdentification,
                "Operator identification"
        );

        validateUserRepositoryAvailable();

        User user = userRepository
                .findByIdentification(
                        operatorIdentification
                )
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Logistics operator was not found."
                        )
                );

        if (!(user instanceof LogisticsOperator operator)) {
            throw new IllegalStateException(
                    "Shipment operations require a logistics operator."
            );
        }

        validateOperator(operator);

        return operator;
    }

    /**
     * Localiza un envío mediante su identificador.
     */
    private Shipment findShipment(
            String shipmentId
    ) {
        validateText(
                shipmentId,
                "Shipment identifier"
        );

        return shipmentRepository
                .findById(shipmentId)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Shipment was not found."
                        )
                );
    }

    /**
     * Guarda el envío y el nuevo estado de su pedido.
     */
    private void saveShipmentAndOrder(
            Shipment shipment
    ) {
        Order order = shipment.getOrder();

        /*
         * Primero se guarda el nuevo estado del pedido.
         * Después se actualiza el envío relacionado.
         */
        orderRepository.save(order);
        shipmentRepository.save(shipment);
    }

    /**
     * Evita crear más de un envío para el mismo pedido.
     */
    private void validateOrderWithoutShipment(
            Order order
    ) {
        String orderId = order.getOrderId();

        if (orderId != null
                && !orderId.isBlank()
                && shipmentRepository
                        .existsByOrderId(orderId)) {
            throw new IllegalStateException(
                    "Order already has a shipment."
            );
        }
    }

    /**
     * Valida que la operación sea realizada
     * por un operador logístico activo.
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
     * Valida que exista el pedido.
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Shipment order must not be null."
            );
        }
    }

    /**
     * Valida que exista el envío.
     */
    private void validateShipment(
            Shipment shipment
    ) {
        if (shipment == null) {
            throw new IllegalArgumentException(
                    "Shipment must not be null."
            );
        }
    }

    /**
     * Valida un texto obligatorio.
     */
    private void validateText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }
    }

    /**
     * Valida el repositorio de envíos.
     */
    private void validateShipmentRepository(
            ShipmentRepository shipmentRepository
    ) {
        if (shipmentRepository == null) {
            throw new IllegalArgumentException(
                    "Shipment repository must not be null."
            );
        }
    }

    /**
     * Valida el repositorio de pedidos.
     */
    private void validateOrderRepository(
            OrderRepository orderRepository
    ) {
        if (orderRepository == null) {
            throw new IllegalArgumentException(
                    "Order repository must not be null."
            );
        }
    }

    /**
     * Valida el repositorio de usuarios.
     */
    private void validateUserRepository(
            UserRepository userRepository
    ) {
        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }
    }

    /**
     * Comprueba que el repositorio de usuarios
     * esté disponible para operaciones por identificación.
     */
    private void validateUserRepositoryAvailable() {
        if (userRepository == null) {
            throw new IllegalStateException(
                    "User repository is required for identifier-based operations."
            );
        }
    }
}