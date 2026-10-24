package application.adapters.output.persistence.jpa.mappers;

import application.adapters.output.persistence.jpa.entities.OrderJpaEntity;
import application.adapters.output.persistence.jpa.entities.ReturnJpaEntity;
import application.domain.models.Order;
import application.domain.models.Return;
import org.springframework.stereotype.Component;

/**
 * Convierte devoluciones entre el dominio
 * y las entidades utilizadas por JPA.
 */
@Component
public class ReturnJpaMapper {

    private final OrderJpaMapper orderJpaMapper;

    /**
     * Construye el mapper con el conversor de órdenes.
     *
     * @param orderJpaMapper mapper de órdenes
     */
    public ReturnJpaMapper(
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
     * Convierte una devolución en una entidad JPA.
     *
     * @param returnProcess devolución del dominio
     * @param orderEntity orden persistida
     * @return entidad de devolución
     */
    public ReturnJpaEntity toEntity(
            Return returnProcess,
            OrderJpaEntity orderEntity
    ) {
        validateReturn(returnProcess);
        validateOrderEntity(orderEntity);

        return new ReturnJpaEntity(
                returnProcess.getReturnId(),
                orderEntity
        );
    }

    /**
     * Actualiza una entidad existente.
     *
     * @param returnProcess devolución actual
     * @param orderEntity orden persistida
     * @param entity entidad que será actualizada
     */
    public void updateEntity(
            Return returnProcess,
            OrderJpaEntity orderEntity,
            ReturnJpaEntity entity
    ) {
        validateReturn(returnProcess);
        validateOrderEntity(orderEntity);
        validateReturnEntity(entity);

        entity.changeReturnId(
                returnProcess.getReturnId()
        );

        entity.changeOrder(orderEntity);
    }

    /**
     * Reconstruye una devolución del dominio.
     *
     * El comprador se obtiene desde la orden porque
     * solamente su propietario puede solicitarla.
     *
     * @param entity entidad persistida
     * @return devolución reconstruida
     */
    public Return toDomain(
            ReturnJpaEntity entity
    ) {
        validateReturnEntity(entity);
        validateOrderEntity(entity.getOrder());

        Order order =
                orderJpaMapper.toDomain(
                        entity.getOrder()
                );

        return new Return(
                entity.getReturnId(),
                order,
                order.getBuyer()
        );
    }

    private void validateReturn(
            Return returnProcess
    ) {
        if (returnProcess == null) {
            throw new IllegalArgumentException(
                    "Return must not be null."
            );
        }
    }

    private void validateOrderEntity(
            OrderJpaEntity entity
    ) {
        if (entity == null) {
            throw new IllegalArgumentException(
                    "Return order JPA entity must not be null."
            );
        }
    }

    private void validateReturnEntity(
            ReturnJpaEntity entity
    ) {
        if (entity == null) {
            throw new IllegalArgumentException(
                    "Return JPA entity must not be null."
            );
        }
    }
}