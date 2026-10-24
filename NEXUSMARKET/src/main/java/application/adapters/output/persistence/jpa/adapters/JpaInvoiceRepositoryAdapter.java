package application.adapters.output.persistence.jpa.adapters;

import application.adapters.output.persistence.jpa.entities.InvoiceJpaEntity;
import application.adapters.output.persistence.jpa.entities.OrderJpaEntity;
import application.adapters.output.persistence.jpa.mappers.InvoiceJpaMapper;
import application.adapters.output.persistence.jpa.repositories.SpringDataInvoiceRepository;
import application.adapters.output.persistence.jpa.repositories.SpringDataOrderRepository;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.ports.output.InvoiceQueryRepository;
import application.ports.output.InvoiceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA encargado de almacenar
 * y consultar facturas en MySQL.
 */
@Repository
@Transactional
public class JpaInvoiceRepositoryAdapter
        implements InvoiceRepository,
        InvoiceQueryRepository {

    private final SpringDataInvoiceRepository
            springDataInvoiceRepository;

    private final SpringDataOrderRepository
            springDataOrderRepository;

    private final InvoiceJpaMapper invoiceJpaMapper;

    /**
     * Construye el adaptador con sus dependencias.
     *
     * @param springDataInvoiceRepository repositorio JPA de facturas
     * @param springDataOrderRepository repositorio JPA de pedidos
     * @param invoiceJpaMapper conversor de facturas
     */
    public JpaInvoiceRepositoryAdapter(
            SpringDataInvoiceRepository
                    springDataInvoiceRepository,
            SpringDataOrderRepository
                    springDataOrderRepository,
            InvoiceJpaMapper invoiceJpaMapper
    ) {
        validateDependencies(
                springDataInvoiceRepository,
                springDataOrderRepository,
                invoiceJpaMapper
        );

        this.springDataInvoiceRepository =
                springDataInvoiceRepository;

        this.springDataOrderRepository =
                springDataOrderRepository;

        this.invoiceJpaMapper = invoiceJpaMapper;
    }

    /**
     * Guarda una factura nueva.
     *
     * El pedido debe estar persistido previamente.
     * Un pedido solamente puede tener una factura.
     *
     * @param invoice factura almacenada
     */
    @Override
    public void save(
            Invoice invoice
    ) {
        validateInvoice(invoice);

        Order order = invoice.getOrder();

        validateOrder(order);

        String orderId = order.getOrderId();

        validateText(
                orderId,
                "Order identifier"
        );

        OrderJpaEntity orderEntity =
                springDataOrderRepository
                        .findByOrderId(orderId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Order must be persisted before creating an invoice."
                                )
                        );

        Optional<InvoiceJpaEntity> storedInvoice =
                springDataInvoiceRepository
                        .findByInvoiceId(
                                invoice.getInvoiceId()
                        );

        if (storedInvoice.isPresent()) {
            validateSameOrder(
                    storedInvoice.get(),
                    orderId
            );

            return;
        }

        validateOrderWithoutAnotherInvoice(
                orderId
        );

        InvoiceJpaEntity entity =
                invoiceJpaMapper.toEntity(
                        invoice,
                        orderEntity
                );

        springDataInvoiceRepository.save(entity);
    }

    /**
     * Busca una factura por su identificador.
     *
     * @param invoiceId identificador de la factura
     * @return factura encontrada o resultado vacío
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> findById(
            String invoiceId
    ) {
        validateText(
                invoiceId,
                "Invoice identifier"
        );

        return springDataInvoiceRepository
                .findByInvoiceId(invoiceId)
                .map(invoiceJpaMapper::toDomain);
    }

    /**
     * Busca la factura perteneciente a un pedido.
     *
     * @param orderId identificador del pedido
     * @return factura encontrada o resultado vacío
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> findByOrderId(
            String orderId
    ) {
        validateText(
                orderId,
                "Order identifier"
        );

        return springDataInvoiceRepository
                .findByOrder_OrderId(orderId)
                .map(invoiceJpaMapper::toDomain);
    }

    /**
     * Comprueba si un pedido ya tiene factura.
     *
     * @param orderId identificador del pedido
     * @return true si la factura existe
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

        return springDataInvoiceRepository
                .existsByOrder_OrderId(orderId);
    }

    /**
     * Obtiene todas las facturas.
     *
     * @return facturas registradas
     */
    @Override
    @Transactional(readOnly = true)
    public List<Invoice> findAll() {
        return springDataInvoiceRepository
                .findAll()
                .stream()
                .map(invoiceJpaMapper::toDomain)
                .toList();
    }

    /**
     * Impide crear más de una factura
     * para el mismo pedido.
     *
     * @param orderId identificador del pedido
     */
    private void validateOrderWithoutAnotherInvoice(
            String orderId
    ) {
        if (springDataInvoiceRepository
                .existsByOrder_OrderId(orderId)) {
            throw new IllegalStateException(
                    "Order already has an invoice."
            );
        }
    }

    /**
     * Comprueba que una factura existente no sea
     * asociada posteriormente con otro pedido.
     *
     * @param entity factura persistida
     * @param orderId identificador del pedido
     */
    private void validateSameOrder(
            InvoiceJpaEntity entity,
            String orderId
    ) {
        if (entity.getOrder() == null
                || !orderId.equals(
                        entity.getOrder()
                                .getOrderId()
                )) {
            throw new IllegalStateException(
                    "Invoice order cannot be changed."
            );
        }
    }

    /**
     * Valida la factura.
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
     * Valida el pedido relacionado.
     *
     * @param order pedido validado
     */
    private void validateOrder(
            Order order
    ) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Invoice order must not be null."
            );
        }
    }

    /**
     * Valida un identificador obligatorio.
     *
     * @param value identificador
     * @param fieldName nombre funcional
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
            SpringDataInvoiceRepository invoiceRepository,
            SpringDataOrderRepository orderRepository,
            InvoiceJpaMapper mapper
    ) {
        if (invoiceRepository == null) {
            throw new IllegalArgumentException(
                    "Spring Data invoice repository must not be null."
            );
        }

        if (orderRepository == null) {
            throw new IllegalArgumentException(
                    "Spring Data order repository must not be null."
            );
        }

        if (mapper == null) {
            throw new IllegalArgumentException(
                    "Invoice JPA mapper must not be null."
            );
        }
    }
}