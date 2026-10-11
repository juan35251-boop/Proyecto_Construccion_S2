package application.ports.input;

import application.domain.models.Invoice;
import application.domain.models.User;

import java.util.List;

/**
 * Puerto de entrada que define el caso de uso
 * para consultar facturas.
 *
 * Las facturas visibles dependen del rol y la
 * relación del usuario con la compra.
 */
public interface InvoiceQueryUseCase {

    /**
     * Obtiene las facturas visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return lista de facturas autorizadas
     */
    List<Invoice> findAccessibleInvoices(
            User requestedBy
    );
}