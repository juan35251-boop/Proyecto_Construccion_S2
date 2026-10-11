package application.services;

import application.domain.models.Buyer;
import application.domain.models.Invoice;
import application.domain.models.OrderItem;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.InvoiceQueryUseCase;
import application.ports.output.InvoiceQueryRepository;

import java.util.List;

/**
 * Servicio encargado de consultar facturas.
 *
 * Implementa el puerto de entrada InvoiceQueryUseCase,
 * permitiendo consultar facturas sin depender directamente
 * de esta implementación.
 *
 * Permisos:
 *
 * - El comprador consulta sus propias facturas.
 * - El vendedor consulta facturas relacionadas
 *   con sus productos.
 * - El administrador consulta todas las facturas.
 * - El supervisor consulta todas las facturas.
 * - El operador logístico no administra
 *   información financiera.
 */
public class InvoiceQueryService
        implements InvoiceQueryUseCase {

    private final InvoiceQueryRepository
            invoiceQueryRepository;

    /**
     * Construye el servicio con el repositorio requerido.
     *
     * @param invoiceQueryRepository repositorio
     *                               de consulta de facturas
     */
    public InvoiceQueryService(
            InvoiceQueryRepository invoiceQueryRepository
    ) {
        if (invoiceQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Invoice query repository must not be null."
            );
        }

        this.invoiceQueryRepository =
                invoiceQueryRepository;
    }

    /**
     * Obtiene las facturas visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return facturas autorizadas
     */
    @Override
    public List<Invoice> findAccessibleInvoices(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        List<Invoice> invoices =
                invoiceQueryRepository.findAll();

        /*
         * Administradores y supervisores pueden
         * consultar todas las facturas.
         */
        if (hasGlobalAccess(requestedBy)) {
            return List.copyOf(invoices);
        }

        /*
         * El comprador solamente puede consultar
         * las facturas que le pertenecen.
         */
        if (requestedBy instanceof Buyer buyer) {
            return invoices.stream()
                    .filter(
                            invoice ->
                                    invoice.getBuyer() == buyer
                    )
                    .toList();
        }

        /*
         * El vendedor consulta facturas que contengan
         * por lo menos uno de sus productos.
         */
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

        /*
         * El operador logístico y los demás roles
         * no tienen acceso a la información financiera.
         */
        throw new IllegalStateException(
                "User is not authorized to query invoices."
        );
    }

    /**
     * Comprueba si la factura contiene productos
     * pertenecientes al vendedor.
     *
     * @param invoice factura consultada
     * @param seller vendedor que realiza la consulta
     * @return true si contiene un producto del vendedor
     */
    private boolean containsSellerProduct(
            Invoice invoice,
            Seller seller
    ) {
        for (OrderItem item : invoice.getItems()) {
            if (seller.managesProduct(
                    item.getProduct()
            )) {
                return true;
            }
        }

        return false;
    }

    /**
     * Valida que quien consulta sea un usuario activo.
     *
     * @param requestedBy usuario solicitante
     */
    private void validateActiveRequester(
            User requestedBy
    ) {
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
     * Indica si el usuario tiene acceso global
     * de lectura sobre las facturas.
     *
     * @param user usuario que realiza la consulta
     * @return true si es administrador o supervisor
     */
    private boolean hasGlobalAccess(User user) {
        return user.getRole()
                == SystemRole.ADMINISTRATOR
                || user.getRole()
                == SystemRole.SUPERVISOR;
    }
}