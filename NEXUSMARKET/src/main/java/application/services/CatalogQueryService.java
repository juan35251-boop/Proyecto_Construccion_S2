package application.services;

import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.CatalogQueryUseCase;
import application.ports.output.ProductQueryRepository;
import application.ports.output.UserRepository;

import java.util.List;

/**
 * Servicio encargado de consultar el catálogo.
 *
 * Permisos:
 *
 * - Comprador y operador logístico: productos publicados.
 * - Vendedor: todos sus propios productos.
 * - Administrador y supervisor: catálogo completo.
 */
public class CatalogQueryService
        implements CatalogQueryUseCase {

    private final UserRepository userRepository;
    private final ProductQueryRepository
            productQueryRepository;

    /**
     * Construye el servicio con los repositorios requeridos.
     *
     * @param userRepository repositorio de usuarios
     * @param productQueryRepository repositorio de productos
     */
    public CatalogQueryService(
            UserRepository userRepository,
            ProductQueryRepository productQueryRepository
    ) {
        validateRepositories(
                userRepository,
                productQueryRepository
        );

        this.userRepository = userRepository;
        this.productQueryRepository =
                productQueryRepository;
    }

    @Override
    public List<Product> findAccessibleProducts(
            String requesterIdentification
    ) {
        User requester =
                findRequester(
                        requesterIdentification
                );

        validateActiveRequester(requester);

        List<Product> products =
                productQueryRepository.findAll();

        if (hasGlobalAccess(requester)) {
            return List.copyOf(products);
        }

        if (requester instanceof Seller seller) {
            return products.stream()
                    .filter(
                            product ->
                                    product.belongsToSeller(
                                            seller.getIdentification()
                                    )
                    )
                    .toList();
        }

        return products.stream()
                .filter(Product::isPublished)
                .toList();
    }

    private User findRequester(
            String identification
    ) {
        validateIdentification(identification);

        return userRepository
                .findByIdentification(identification)
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Requesting user was not found."
                                )
                );
    }

    private void validateActiveRequester(
            User requester
    ) {
        if (!requester.isActive()) {
            throw new IllegalStateException(
                    "Only active users can query the catalog."
            );
        }
    }

    private boolean hasGlobalAccess(User user) {
        return user.getRole()
                == SystemRole.ADMINISTRATOR
                || user.getRole()
                == SystemRole.SUPERVISOR;
    }

    private void validateRepositories(
            UserRepository userRepository,
            ProductQueryRepository productQueryRepository
    ) {
        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }

        if (productQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Product query repository must not be null."
            );
        }
    }

    private void validateIdentification(
            String identification
    ) {
        if (identification == null
                || identification.isBlank()) {
            throw new IllegalArgumentException(
                    "Requester identification must not be empty."
            );
        }
    }
}