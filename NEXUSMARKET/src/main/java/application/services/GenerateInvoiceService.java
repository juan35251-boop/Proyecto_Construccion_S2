package application.services;

import application.domain.models.Invoice;
import application.domain.models.Order;
import application.ports.output.InvoiceRepository;

/**
 * Servicio de aplicación encargado de generar facturas
 * para los pedidos que ya fueron pagados.
 *
 * La factura se genera como consecuencia de la confirmación
 * del pago y no representa un nuevo cobro.
 */
public class GenerateInvoiceService {

    private final InvoiceRepository invoiceRepository;

    /**
     * Construye el servicio con el repositorio de facturas.
     *
     * @param invoiceRepository repositorio utilizado para guardar facturas
     */
    public GenerateInvoiceService(
            InvoiceRepository invoiceRepository
    ) {
        validateRepository(invoiceRepository);
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Genera y almacena la factura correspondiente a un pedido.
     *
     * El constructor de Invoice comprueba que el pedido exista
     * y que no continúe pendiente de pago.
     *
     * @param order pedido para el cual se genera la factura
     * @return factura generada
     */
    public Invoice generate(Order order) {
        Invoice invoice = new Invoice(order);

        invoiceRepository.save(invoice);

        return invoice;
    }

    /**
     * Valida que el repositorio de facturas exista.
     *
     * @param invoiceRepository repositorio que se desea validar
     */
    private void validateRepository(
            InvoiceRepository invoiceRepository
    ) {
        if (invoiceRepository == null) {
            throw new IllegalArgumentException(
                    "Invoice repository must not be null."
            );
        }
    }
}