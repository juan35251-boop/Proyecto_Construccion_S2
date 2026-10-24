package application.adapters.output.persistence.jpa.adapters;

import application.adapters.output.persistence.jpa.entities.OrderJpaEntity;
import application.adapters.output.persistence.jpa.entities.ShipmentJpaEntity;
import application.adapters.output.persistence.jpa.mappers.ShipmentJpaMapper;
import application.adapters.output.persistence.jpa.repositories.SpringDataOrderRepository;
import application.adapters.output.persistence.jpa.repositories.SpringDataShipmentRepository;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.ports.output.ShipmentQueryRepository;
import application.ports.output.ShipmentRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA encargado de implementar los puertos
 * de almacenamiento y consulta de envíos.
 *
 * El estado del pedido y del envío se almacena en MySQL.
 */
@Repository
@Transactional
public class JpaShipmentRepositoryAdapter
        implements ShipmentRepository,
        ShipmentQueryRepository {

    private final SpringDataShipmentRepository
            springDataShipmentRepository;

    private final SpringDataOrderRepository
            springDataOrderRepository;

    private final ShipmentJpaMapper shipmentJpaMapper;

    /**
     * Construye el adaptador con sus dependencias.
     *
     * @param springDataShipmentRepository repositorio JPA de envíos
     * @param springDataOrderRepository repositorio JPA de pedidos
     * @param shipmentJpaMapper conversor de envíos
     */
    public JpaShipmentRepositoryAdapter(
            SpringDataShipmentRepository
                    springDataShipmentRepository,
            SpringDataOrderRepository
                    springDataOrderRepository,
            ShipmentJpaMapper shipmentJpaMapper
    ) {
        validateDependencies(
                springDataShipmentRepository,
                springDataOrderRepository,
                shipmentJpaMapper
        );

        this.springDataShipmentRepository =
                springDataShipmentRepository;

        this.springDataOrderRepository =
                springDataOrderRepository;

        this.shipmentJpaMapper =
                shipmentJpaMapper;
    }

    /**
     * Guarda un envío nuevo o actualiza uno existente.
     *
     * El pedido debe estar almacenado antes de crear
     * el envío. Esto protege la relación entre ambas tablas.
     *
     * @param shipment envío que será almacenado
     */
    @Override
    public void save(Shipment shipment) {
        validateShipment(shipment);

        Order order = shipment.getOrder();

        validateOrder(order);

        String orderId = order.getOrderId();

        validateText(
                orderId,
                "Order identifier"
        );

        OrderJpaEntity orderEntity =
                springDataOrderRepository
                        .findByOrderId(orderId)
                        .orElseThrow(
                                () -> new IllegalStateException(
                                        "Order must be persisted before creating a shipment."
                                )
                        );

        Optional<ShipmentJpaEntity> storedShipment =
                springDataShipmentRepository
                        .findByShipmentId(
                                shipment.getShipmentId()
                        );

        ShipmentJpaEntity shipmentEntity;

        if (storedShipment.isPresent()) {
            shipmentEntity = storedShipment.get();

            shipmentJpaMapper.updateEntity(
                    shipment,
                    orderEntity,
                    shipmentEntity
            );
        } else {
            validateOrderWithoutAnotherShipment(
                    orderId
            );

            shipmentEntity =
                    shipmentJpaMapper.toEntity(
                            shipment,
                            orderEntity
                    );
        }

        springDataShipmentRepository.save(
                shipmentEntity
        );
    }

    /**
     * Busca un envío mediante su identificador.
     *
     * @param shipmentId identificador del envío
     * @return envío encontrado o vacío
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Shipment> findById(
            String shipmentId
    ) {
        validateText(
                shipmentId,
                "Shipment identifier"
        );

        return springDataShipmentRepository
                .findByShipmentId(shipmentId)
                .map(shipmentJpaMapper::toDomain);
    }

    /**
     * Busca un envío mediante el identificador
     * de su pedido.
     *
     * @param orderId identificador del pedido
     * @return envío encontrado o vacío
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Shipment> findByOrderId(
            String orderId
    ) {
        validateText(
                orderId,
                "Order identifier"
        );

        return springDataShipmentRepository
                .findByOrder_OrderId(orderId)
                .map(shipmentJpaMapper::toDomain);
    }

    /**
     * Comprueba si un pedido ya tiene un envío.
     *
     * @param orderId identificador del pedido
     * @return true si existe un envío
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByOrderId(
            String orderId
    ) {
        validateText(
                orderId,
                "Order identifier"
        );

        return springDataShipmentRepository
                .existsByOrder_OrderId(orderId);
    }

    /**
     * Obtiene todos los envíos registrados.
     *
     * @return lista de envíos
     */
    @Override
    @Transactional(readOnly = true)
    public List<Shipment> findAll() {
        return springDataShipmentRepository
                .findAll()
                .stream()
                .map(shipmentJpaMapper::toDomain)
                .toList();
    }

    /**
     * Impide crear más de un envío para el mismo pedido.
     */
    private void validateOrderWithoutAnotherShipment(
            String orderId
    ) {
        if (springDataShipmentRepository
                .existsByOrder_OrderId(orderId)) {
            throw new IllegalStateException(
                    "Order already has a shipment."
            );
        }
    }

    /**
     * Valida el envío.
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
     * Valida el pedido asociado.
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Shipment order must not be null."
            );
        }
    }

    /**
     * Valida un identificador obligatorio.
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
     * Valida las dependencias del adaptador.
     */
    private void validateDependencies(
            SpringDataShipmentRepository shipmentRepository,
            SpringDataOrderRepository orderRepository,
            ShipmentJpaMapper mapper
    ) {
        if (shipmentRepository == null) {
            throw new IllegalArgumentException(
                    "Spring Data shipment repository must not be null."
            );
        }

        if (orderRepository == null) {
            throw new IllegalArgumentException(
                    "Spring Data order repository must not be null."
            );
        }

        if (mapper == null) {
            throw new IllegalArgumentException(
                    "Shipment JPA mapper must not be null."
            );
        }
    }
}