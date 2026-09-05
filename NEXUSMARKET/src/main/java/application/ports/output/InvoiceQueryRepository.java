package application.ports.output;

import application.domain.models.Invoice;

import java.util.List;

/**
 * Puerto de salida para consultar facturas.
 */
public interface InvoiceQueryRepository {

    /**
     * Obtiene todas las facturas registradas.
     *
     * @return lista de facturas
     */
    List<Invoice> findAll();
}