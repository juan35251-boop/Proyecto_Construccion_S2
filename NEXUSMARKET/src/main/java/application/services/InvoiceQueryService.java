package application.services;

import application.domain.models.Buyer;
import application.domain.models.Invoice;
import application.domain.models.OrderItem;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.output.InvoiceQueryRepository;

import java.util.List;

/**
 * Servicio encargado de consultar facturas.
 *
 * Permisos:
 *
 * - El comprador consulta sus propias facturas.
 * - El vendedor consulta facturas relacionadas con sus productos.
 * - El administrador y el supervisor consultan todas las facturas.
 * - El operador logístico no administra información financiera.
 */
public class InvoiceQueryService {

    private final InvoiceQueryRepository invoiceQueryRepository;

    /**
     * Construye el servicio con el repositorio requerido.
     *
     * @param invoiceQueryRepository repositorio de consulta de facturas
     */
    public InvoiceQueryService(
            InvoiceQueryRepository invoiceQueryRepository
    ) {
        if (invoiceQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Invoice query repository must not be null."
            );
        }

        this.invoiceQueryRepository = invoiceQueryRepository;
    }

    /**
     * Obtiene las facturas visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return facturas autorizadas
     */
    public List<Invoice> findAccessibleInvoices(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        List<Invoice> invoices =
                invoiceQueryRepository.findAll();

        if (hasGlobalAccess(requestedBy)) {
            return List.copyOf(invoices);
        }

        if (requestedBy instanceof Buyer buyer) {
            return invoices.stream()
                    .filter(invoice -> invoice.getBuyer() == buyer)
                    .toList();
        }

        if (requestedBy instanceof Seller seller) {
            return invoices.stream()
                    .filter(
                            invoice ->
                                    containsSellerProduct(
                                            invoice,
                                            seller
                                    )
                    )
                    .toList();
        }

        throw new IllegalStateException(
                "User is not authorized to query invoices."
        );
    }

    /**
     * Comprueba si la factura contiene productos del vendedor.
     */
    private boolean containsSellerProduct(
            Invoice invoice,
            Seller seller
    ) {
        for (OrderItem item : invoice.getItems()) {
            if (seller.managesProduct(item.getProduct())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Valida que quien consulta sea un usuario activo.
     */
    private void validateActiveRequester(User requestedBy) {
        if (requestedBy == null) {
            throw new IllegalArgumentException(
                    "Requesting user must not be null."
            );
        }

        if (!requestedBy.isActive()) {
            throw new IllegalStateException(
                    "Only active users can query invoices."
            );
        }
    }

    /**
     * Administradores y supervisores tienen acceso global de lectura.
     */
    private boolean hasGlobalAccess(User user) {
        return user.getRole() == SystemRole.ADMINISTRATOR
                || user.getRole() == SystemRole.SUPERVISOR;
    }
}