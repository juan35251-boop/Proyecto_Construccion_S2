package application.services;

import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.CatalogQueryUseCase;
import application.ports.output.ProductQueryRepository;

import java.util.List;

/**
 * Servicio de aplicación encargado de consultar el catálogo.
 *
 * Implementa el puerto de entrada CatalogQueryUseCase,
 * permitiendo consultar productos sin depender directamente
 * de esta implementación.
 *
 * Aplica las siguientes reglas:
 *
 * - El comprador puede consultar productos publicados.
 * - El operador logístico puede consultar productos publicados.
 * - El vendedor puede consultar todos sus productos.
 * - El administrador puede consultar todo el catálogo.
 * - El supervisor puede consultar todo el catálogo.
 */
public class CatalogQueryService
        implements CatalogQueryUseCase {

    private final ProductQueryRepository productQueryRepository;

    /**
     * Construye el servicio con el puerto
     * de consulta requerido.
     *
     * @param productQueryRepository repositorio
     *                               de consulta de productos
     */
    public CatalogQueryService(
            ProductQueryRepository productQueryRepository
    ) {
        if (productQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Product query repository must not be null."
            );
        }

        this.productQueryRepository =
                productQueryRepository;
    }

    /**
     * Obtiene los productos visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return productos que el usuario puede consultar
     */
    @Override
    public List<Product> findAccessibleProducts(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        List<Product> products =
                productQueryRepository.findAll();

        /*
         * Administradores y supervisores pueden
         * consultar todo el catálogo.
         */
        if (hasGlobalAccess(requestedBy)) {
            return List.copyOf(products);
        }

        /*
         * El vendedor puede consultar todos sus productos,
         * incluso si están suspendidos o descontinuados.
         */
        if (requestedBy instanceof Seller seller) {
            return products.stream()
                    .filter(seller::managesProduct)
                    .toList();
        }

        /*
         * Los compradores y operadores logísticos solamente
         * pueden visualizar productos publicados.
         */
        return products.stream()
                .filter(Product::isPublished)
                .toList();
    }

    /**
     * Valida que la consulta sea realizada
     * por un usuario activo.
     *
     * @param requestedBy usuario que realiza la consulta
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
                    "Only active users can query the catalog."
            );
        }
    }

    /**
     * Indica si el usuario tiene acceso global
     * de lectura sobre el catálogo.
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