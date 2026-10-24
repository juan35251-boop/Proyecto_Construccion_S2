package application.ports.input;

import application.domain.models.Invoice;
import application.domain.models.User;

import java.util.List;

/**
 * Puerto de entrada que define el caso de uso
 * para consultar facturas.
 *
 * Las facturas visibles dependen del rol y de
 * la relación del usuario con la compra.
 */
public interface InvoiceQueryUseCase {

    /**
     * Obtiene las facturas visibles para un usuario.
     *
     * @param requestedBy usuario solicitante
     * @return facturas autorizadas
     */
    List<Invoice> findAccessibleInvoices(
            User requestedBy
    );

    /**
     * Obtiene las facturas visibles utilizando
     * la identificación del solicitante.
     *
     * @param requesterIdentification identificación del usuario
     * @return facturas autorizadas
     */
    List<Invoice>
    findAccessibleInvoicesByRequesterIdentification(
            String requesterIdentification
    );

    /**
     * Consulta una factura específica respetando
     * los permisos del usuario.
     *
     * @param requesterIdentification identificación del solicitante
     * @param invoiceId identificador de la factura
     * @return factura autorizada
     */
    Invoice findAccessibleInvoiceByIdentifiers(
            String requesterIdentification,
            String invoiceId
    );
}