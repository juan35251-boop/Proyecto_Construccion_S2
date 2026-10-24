package application.adapters.output.persistence.jpa.mappers;

import application.adapters.output.persistence.jpa.entities.InvoiceJpaEntity;
import application.adapters.output.persistence.jpa.entities.OrderJpaEntity;
import application.domain.models.Invoice;
import application.domain.models.Order;
import org.springframework.stereotype.Component;

/**
 * Convierte facturas entre el modelo del dominio
 * y las entidades utilizadas por JPA.
 */
@Component
public class InvoiceJpaMapper {

    private final OrderJpaMapper orderJpaMapper;

    /**
     * Construye el mapper con el conversor de pedidos.
     *
     * @param orderJpaMapper mapper utilizado para
     *                       reconstruir pedidos
     */
    public InvoiceJpaMapper(
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
     * Convierte una factura del dominio
     * en una entidad persistente.
     *
     * El pedido se recibe previamente localizado
     * porque debe existir en MySQL.
     *
     * @param invoice factura del dominio
     * @param orderEntity pedido persistido
     * @return entidad persistente
     */
    public InvoiceJpaEntity toEntity(
            Invoice invoice,
            OrderJpaEntity orderEntity
    ) {
        validateInvoice(invoice);
        validateOrderEntity(orderEntity);

        return new InvoiceJpaEntity(
                invoice.getInvoiceId(),
                orderEntity
        );
    }

    /**
     * Reconstruye una factura desde JPA.
     *
     * @param entity entidad persistente
     * @return factura del dominio
     */
    public Invoice toDomain(
            InvoiceJpaEntity entity
    ) {
        validateEntity(entity);
        validateOrderEntity(entity.getOrder());

        Order order =
                orderJpaMapper.toDomain(
                        entity.getOrder()
                );

        return new Invoice(
                entity.getInvoiceId(),
                order
        );
    }

    /**
     * Valida la factura del dominio.
     *
     * @param invoice factura validada
     */
    private void validateInvoice(
            Invoice invoice
    ) {
        if (invoice == null) {
            throw new IllegalArgumentException(
                    "Invoice must not be null."
            );
        }
    }

    /**
     * Valida la entidad de factura.
     *
     * @param entity entidad validada
     */
    private void validateEntity(
            InvoiceJpaEntity entity
    ) {
        if (entity == null) {
            throw new IllegalArgumentException(
                    "Invoice JPA entity must not be null."
            );
        }
    }

    /**
     * Valida la entidad del pedido.
     *
     * @param orderEntity pedido persistido
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