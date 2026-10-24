package application.services;

import application.domain.models.Buyer;
import application.domain.models.Invoice;
import application.domain.models.OrderItem;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.InvoiceQueryUseCase;
import application.ports.output.InvoiceQueryRepository;
import application.ports.output.UserRepository;

import java.util.List;

/**
 * Servicio encargado de consultar facturas.
 *
 * Permisos:
 *
 * - Comprador: consulta sus propias facturas.
 * - Vendedor: consulta facturas relacionadas
 *   con sus productos.
 * - Administrador: consulta todas las facturas.
 * - Supervisor: consulta todas las facturas.
 * - Operador logístico: no tiene acceso
 *   a información financiera.
 */
public class InvoiceQueryService
        implements InvoiceQueryUseCase {

    private final InvoiceQueryRepository
            invoiceQueryRepository;

    private final UserRepository userRepository;

    /**
     * Constructor conservado para las pruebas
     * que proporcionan directamente un usuario.
     *
     * @param invoiceQueryRepository repositorio de facturas
     */
    public InvoiceQueryService(
            InvoiceQueryRepository invoiceQueryRepository
    ) {
        validateInvoiceQueryRepository(
                invoiceQueryRepository
        );

        this.invoiceQueryRepository =
                invoiceQueryRepository;

        this.userRepository = null;
    }

    /**
     * Construye el servicio completo para la API.
     *
     * @param invoiceQueryRepository repositorio de facturas
     * @param userRepository repositorio de usuarios
     */
    public InvoiceQueryService(
            InvoiceQueryRepository invoiceQueryRepository,
            UserRepository userRepository
    ) {
        validateInvoiceQueryRepository(
                invoiceQueryRepository
        );

        validateUserRepository(userRepository);

        this.invoiceQueryRepository =
                invoiceQueryRepository;

        this.userRepository = userRepository;
    }

    /**
     * Obtiene las facturas visibles para el usuario.
     *
     * @param requestedBy usuario solicitante
     * @return facturas autorizadas
     */
    @Override
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
                    .filter(invoice ->
                            belongsToBuyer(
                                    invoice,
                                    buyer
                            )
                    )
                    .toList();
        }

        if (requestedBy instanceof Seller seller) {
            return invoices.stream()
                    .filter(invoice ->
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
     * Obtiene las facturas visibles mediante
     * la identificación del solicitante.
     *
     * @param requesterIdentification identificación del usuario
     * @return facturas autorizadas
     */
    @Override
    public List<Invoice>
    findAccessibleInvoicesByRequesterIdentification(
            String requesterIdentification
    ) {
        User requestedBy =
                findUser(requesterIdentification);

        return findAccessibleInvoices(requestedBy);
    }

    /**
     * Consulta una factura específica respetando
     * los permisos del usuario.
     *
     * @param requesterIdentification identificación del solicitante
     * @param invoiceId identificador de la factura
     * @return factura autorizada
     */
    @Override
    public Invoice findAccessibleInvoiceByIdentifiers(
            String requesterIdentification,
            String invoiceId
    ) {
        User requestedBy =
                findUser(requesterIdentification);

        validateText(
                invoiceId,
                "Invoice identifier"
        );

        Invoice invoice =
                invoiceQueryRepository
                        .findById(invoiceId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Invoice was not found."
                                )
                        );

        if (!canAccessInvoice(
                requestedBy,
                invoice
        )) {
            throw new IllegalStateException(
                    "User is not authorized to query this invoice."
            );
        }

        return invoice;
    }

    /**
     * Determina si el usuario puede consultar
     * una factura específica.
     */
    private boolean canAccessInvoice(
            User user,
            Invoice invoice
    ) {
        validateActiveRequester(user);

        if (hasGlobalAccess(user)) {
            return true;
        }

        if (user instanceof Buyer buyer) {
            return belongsToBuyer(
                    invoice,
                    buyer
            );
        }

        if (user instanceof Seller seller) {
            return containsSellerProduct(
                    invoice,
                    seller
            );
        }

        return false;
    }

    /**
     * Comprueba si la factura pertenece
     * al comprador indicado.
     *
     * La comparación utiliza la identificación,
     * no la referencia de memoria.
     */
    private boolean belongsToBuyer(
            Invoice invoice,
            Buyer buyer
    ) {
        return invoice != null
                && invoice.getBuyer() != null
                && buyer.getIdentification()
                        .equals(
                                invoice.getBuyer()
                                        .getIdentification()
                        );
    }

    /**
     * Comprueba si la factura contiene al menos
     * un producto perteneciente al vendedor.
     */
    private boolean containsSellerProduct(
            Invoice invoice,
            Seller seller
    ) {
        if (invoice == null) {
            return false;
        }

        for (OrderItem item : invoice.getItems()) {
            if (item != null
                    && sellerManagesProduct(
                            seller,
                            item.getProduct()
                    )) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determina si un producto pertenece al vendedor.
     *
     * Para productos persistidos se utiliza
     * sellerIdentification. Para productos antiguos
     * se conserva managesProduct como respaldo.
     */
    private boolean sellerManagesProduct(
            Seller seller,
            Product product
    ) {
        if (product == null) {
            return false;
        }

        if (product.getSellerIdentification() != null) {
            return product.belongsToSeller(
                    seller.getIdentification()
            );
        }

        return seller.managesProduct(product);
    }

    /**
     * Localiza al usuario solicitante.
     */
    private User findUser(
            String identification
    ) {
        validateApiRepository();

        validateText(
                identification,
                "Requester identification"
        );

        return userRepository
                .findByIdentification(identification)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Requesting user was not found."
                        )
                );
    }

    /**
     * Valida que el solicitante exista
     * y se encuentre activo.
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
     * Indica si el usuario tiene acceso global.
     */
    private boolean hasGlobalAccess(
            User user
    ) {
        return user.getRole()
                == SystemRole.ADMINISTRATOR
                || user.getRole()
                == SystemRole.SUPERVISOR;
    }

    /**
     * Valida el repositorio de facturas.
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
     * Valida el repositorio de usuarios.
     */
    private void validateUserRepository(
            UserRepository repository
    ) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }
    }

    /**
     * Comprueba que esté disponible el repositorio
     * utilizado para localizar usuarios.
     */
    private void validateApiRepository() {
        if (userRepository == null) {
            throw new IllegalStateException(
                    "User lookup repository is not configured."
            );
        }
    }

    /**
     * Valida un texto obligatorio.
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