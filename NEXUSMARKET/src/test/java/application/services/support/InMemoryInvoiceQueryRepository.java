package application.services.support;

import application.domain.models.Invoice;
import application.ports.output.InvoiceQueryRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio en memoria para probar consultas de facturas.
 */
public class InMemoryInvoiceQueryRepository
        implements InvoiceQueryRepository {

    private final List<Invoice> invoices =
            new ArrayList<>();

    /**
     * Agrega una factura al repositorio de prueba.
     */
    public void add(Invoice invoice) {
        invoices.add(invoice);
    }

    /**
     * Devuelve una copia de las facturas almacenadas.
     */
    @Override
    public List<Invoice> findAll() {
        return List.copyOf(invoices);
    }
}