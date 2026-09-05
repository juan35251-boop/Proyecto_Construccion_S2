package application.ports.output;

import application.domain.models.Invoice;

/**
 * Puerto de salida encargado de almacenar las facturas.
 *
 * Su implementación concreta se realizará posteriormente
 * en la capa de infraestructura.
 */
public interface InvoiceRepository {

    /**
     * Guarda una factura.
     *
     * @param invoice factura que se desea almacenar
     */
    void save(Invoice invoice);
}