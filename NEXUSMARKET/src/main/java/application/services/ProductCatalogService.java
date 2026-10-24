package application.services;

import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductVariant;
import application.ports.input.ProductCatalogUseCase;
import application.ports.output.ProductQueryRepository;
import application.ports.output.ProductRepository;
import application.ports.output.UserRepository;

/**
 * Servicio encargado de administrar
 * los productos del catálogo de un vendedor.
 */
public class ProductCatalogService
        implements ProductCatalogUseCase {

    private final UserRepository userRepository;
    private final ProductQueryRepository
            productQueryRepository;
    private final ProductRepository productRepository;

    /**
     * Construye el servicio con sus repositorios.
     *
     * @param userRepository repositorio de usuarios
     * @param productQueryRepository repositorio de consulta
     * @param productRepository repositorio de escritura
     */
    public ProductCatalogService(
            UserRepository userRepository,
            ProductQueryRepository productQueryRepository,
            ProductRepository productRepository
    ) {
        validateRepositories(
                userRepository,
                productQueryRepository,
                productRepository
        );

        this.userRepository = userRepository;
        this.productQueryRepository =
                productQueryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Product addVariant(
            String sellerIdentification,
            String productId,
            ProductVariant variant
    ) {
        Product product =
                findAuthorizedProduct(
                        sellerIdentification,
                        productId
                );

        product.addVariant(variant);
        productRepository.save(product);

        return product;
    }

    @Override
    public boolean removeVariant(
            String sellerIdentification,
            String productId,
            ProductVariant variant
    ) {
        Product product =
                findAuthorizedProduct(
                        sellerIdentification,
                        productId
                );

        boolean removed =
                product.removeVariant(variant);

        if (removed) {
            productRepository.save(product);
        }

        return removed;
    }

    @Override
    public Product changeStatus(
            String sellerIdentification,
            String productId,
            ProductStatus newStatus
    ) {
        Product product =
                findAuthorizedProduct(
                        sellerIdentification,
                        productId
                );

        product.changeStatus(newStatus);
        productRepository.save(product);

        return product;
    }

    /**
     * Recupera el vendedor y el producto y comprueba
     * que el vendedor sea su propietario.
     */
    private Product findAuthorizedProduct(
            String sellerIdentification,
            String productId
    ) {
        Seller seller =
                findSeller(sellerIdentification);

        validateActiveSeller(seller);
        validateProductId(productId);

        Product product =
                productQueryRepository
                        .findById(productId)
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Product was not found."
                                        )
                        );

        if (!product.belongsToSeller(
                seller.getIdentification()
        )) {
            throw new IllegalStateException(
                    "Seller can only manage their own products."
            );
        }

        return product;
    }

    private Seller findSeller(
            String sellerIdentification
    ) {
        validateSellerIdentification(
                sellerIdentification
        );

        User user =
                userRepository
                        .findByIdentification(
                                sellerIdentification
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Seller was not found."
                                        )
                        );

        if (!(user instanceof Seller seller)) {
            throw new IllegalStateException(
                    "Catalog operation requires a seller."
            );
        }

        return seller;
    }

    private void validateActiveSeller(
            Seller seller
    ) {
        if (!seller.isActive()) {
            throw new IllegalStateException(
                    "Only an active seller can manage products."
            );
        }
    }

    private void validateRepositories(
            UserRepository userRepository,
            ProductQueryRepository productQueryRepository,
            ProductRepository productRepository
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

        if (productRepository == null) {
            throw new IllegalArgumentException(
                    "Product repository must not be null."
            );
        }
    }

    private void validateSellerIdentification(
            String identification
    ) {
        if (identification == null
                || identification.isBlank()) {
            throw new IllegalArgumentException(
                    "Seller identification must not be empty."
            );
        }
    }

    private void validateProductId(
            String productId
    ) {
        if (productId == null
                || productId.isBlank()) {
            throw new IllegalArgumentException(
                    "Product identification must not be empty."
            );
        }
    }
}