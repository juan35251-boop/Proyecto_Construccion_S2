package application.services;

import application.domain.models.Invoice;
import application.domain.models.Order;
import application.ports.input.GenerateInvoiceUseCase;
import application.ports.output.InvoiceQueryRepository;
import application.ports.output.InvoiceRepository;
import application.ports.output.OrderRepository;

/**
 * Servicio de aplicación encargado de generar facturas
 * para pedidos que ya fueron pagados.
 *
 * Cada pedido solamente puede tener una factura.
 */
public class GenerateInvoiceService
        implements GenerateInvoiceUseCase {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceQueryRepository
            invoiceQueryRepository;
    private final OrderRepository orderRepository;

    /**
     * Constructor conservado para las pruebas
     * que utilizan objetos completos del dominio.
     *
     * @param invoiceRepository repositorio de facturas
     */
    public GenerateInvoiceService(
            InvoiceRepository invoiceRepository
    ) {
        validateInvoiceRepository(
                invoiceRepository
        );

        this.invoiceRepository = invoiceRepository;
        this.invoiceQueryRepository = null;
        this.orderRepository = null;
    }

    /**
     * Construye el servicio completo utilizado
     * por la API REST.
     *
     * @param invoiceRepository repositorio de escritura
     * @param invoiceQueryRepository repositorio de consulta
     * @param orderRepository repositorio de pedidos
     */
    public GenerateInvoiceService(
            InvoiceRepository invoiceRepository,
            InvoiceQueryRepository invoiceQueryRepository,
            OrderRepository orderRepository
    ) {
        validateInvoiceRepository(
                invoiceRepository
        );

        validateInvoiceQueryRepository(
                invoiceQueryRepository
        );

        validateOrderRepository(
                orderRepository
        );

        this.invoiceRepository = invoiceRepository;
        this.invoiceQueryRepository =
                invoiceQueryRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Genera y almacena una factura.
     *
     * Invoice valida que el pedido haya superado
     * el estado PENDING_PAYMENT.
     *
     * @param order pedido facturado
     * @return factura generada
     */
    @Override
    public Invoice generate(
            Order order
    ) {
        validateOrder(order);

        Invoice invoice =
                new Invoice(order);

        invoiceRepository.save(invoice);

        return invoice;
    }

    /**
     * Genera una factura desde el identificador
     * de un pedido persistido.
     *
     * Impide generar más de una factura para
     * el mismo pedido.
     *
     * @param orderId identificador del pedido
     * @return factura generada
     */
    @Override
    public Invoice generateByOrderId(
            String orderId
    ) {
        validateApiRepositories();

        validateText(
                orderId,
                "Order identifier"
        );

        if (invoiceQueryRepository
                .existsByOrderId(orderId)) {
            throw new IllegalStateException(
                    "Order already has an invoice."
            );
        }

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Order was not found."
                                )
                        );

        return generate(order);
    }

    /**
     * Valida el pedido.
     *
     * @param order pedido validado
     */
    private void validateOrder(
            Order order
    ) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Invoice must be associated with an order."
            );
        }
    }

    /**
     * Comprueba que estén configurados los repositorios
     * necesarios para trabajar mediante identificadores.
     */
    private void validateApiRepositories() {
        if (invoiceQueryRepository == null
                || orderRepository == null) {
            throw new IllegalStateException(
                    "Invoice lookup repositories are not configured."
            );
        }
    }

    /**
     * Valida el repositorio de escritura.
     */
    private void validateInvoiceRepository(
            InvoiceRepository repository
    ) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Invoice repository must not be null."
            );
        }
    }

    /**
     * Valida el repositorio de consulta.
     */
    private void validateInvoiceQueryRepository(
            InvoiceQueryRepository repository
    ) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Invoice query repository must not be null."
            );
        }
    }

    /**
     * Valida el repositorio de pedidos.
     */
    private void validateOrderRepository(
            OrderRepository repository
    ) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Order repository must not be null."
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
}