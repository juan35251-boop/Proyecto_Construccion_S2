package application.adapters.output.persistence.jpa.mappers;

import application.adapters.output.persistence.jpa.entities.OrderJpaEntity;
import application.adapters.output.persistence.jpa.entities.ShipmentJpaEntity;
import application.domain.models.Order;
import application.domain.models.Shipment;
import org.springframework.stereotype.Component;

/**
 * Convierte entre el modelo de dominio Shipment
 * y la entidad JPA ShipmentJpaEntity.
 */
@Component
public class ShipmentJpaMapper {

    private final OrderJpaMapper orderJpaMapper;

    /**
     * Construye el mapper con el conversor de pedidos.
     *
     * @param orderJpaMapper mapper utilizado para
     *                       reconstruir el pedido
     */
    public ShipmentJpaMapper(
            OrderJpaMapper orderJpaMapper
    ) {
        if (orderJpaMapper == null) {
            throw new IllegalArgumentException(
                    "Order JPA mapper must not be null."
            );
        }

        this.orderJpaMapper = orderJpaMapper;
    }

    /**
     * Convierte un envío del dominio en una entidad JPA.
     *
     * La entidad del pedido se recibe ya localizada porque
     * el pedido debe existir antes de crear un envío.
     *
     * @param shipment envío del dominio
     * @param orderEntity pedido persistido
     * @return entidad de envío
     */
    public ShipmentJpaEntity toEntity(
            Shipment shipment,
            OrderJpaEntity orderEntity
    ) {
        validateShipment(shipment);
        validateOrderEntity(orderEntity);

        return new ShipmentJpaEntity(
                shipment.getShipmentId(),
                orderEntity
        );
    }

    /**
     * Actualiza una entidad existente.
     *
     * @param shipment envío con la información actual
     * @param orderEntity pedido persistido
     * @param entity entidad que será actualizada
     */
    public void updateEntity(
            Shipment shipment,
            OrderJpaEntity orderEntity,
            ShipmentJpaEntity entity
    ) {
        validateShipment(shipment);
        validateOrderEntity(orderEntity);
        validateShipmentEntity(entity);

        entity.changeShipmentId(
                shipment.getShipmentId()
        );

        entity.changeOrder(orderEntity);
    }

    /**
     * Reconstruye un envío del dominio desde JPA.
     *
     * El constructor de restauración permite recuperar envíos
     * cuyos pedidos estén pagados, despachados, entregados
     * o finalizados.
     *
     * @param entity entidad persistida
     * @return envío del dominio
     */
    public Shipment toDomain(
            ShipmentJpaEntity entity
    ) {
        validateShipmentEntity(entity);
        validateOrderEntity(entity.getOrder());

        Order order =
                orderJpaMapper.toDomain(
                        entity.getOrder()
                );

        return new Shipment(
                entity.getShipmentId(),
                order
        );
    }

    /**
     * Valida el envío del dominio.
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
     * Valida la entidad del envío.
     */
    private void validateShipmentEntity(
            ShipmentJpaEntity entity
    ) {
        if (entity == null) {
            throw new IllegalArgumentException(
                    "Shipment JPA entity must not be null."
            );
        }
    }

    /**
     * Valida la entidad del pedido asociado.
     */
    private void validateOrderEntity(
            OrderJpaEntity orderEntity
    ) {
        if (orderEntity == null) {
            throw new IllegalArgumentException(
                    "Order JPA entity must not be null."
            );
        }
    }
}