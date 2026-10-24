package application.services;

import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.ports.input.RegisterProductUseCase;
import application.ports.output.ProductRepository;
import application.ports.output.UserRepository;

/**
 * Servicio encargado de registrar productos.
 *
 * Localiza al vendedor mediante su identificación,
 * comprueba que esté activo y registra el producto
 * con una relación persistente hacia su propietario.
 */
public class RegisterProductService
        implements RegisterProductUseCase {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * Construye el servicio con sus repositorios.
     *
     * @param userRepository repositorio de usuarios
     * @param productRepository repositorio de productos
     */
    public RegisterProductService(
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        validateRepositories(
                userRepository,
                productRepository
        );

        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    /**
     * Registra un producto perteneciente al vendedor.
     *
     * @param sellerIdentification identificación del vendedor
     * @param productType tipo del producto
     * @param status estado inicial
     * @return producto registrado
     */
    @Override
    public Product register(
            String sellerIdentification,
            ProductType productType,
            ProductStatus status
    ) {
        Seller seller =
                findSeller(sellerIdentification);

        validateActiveSeller(seller);

        Product product =
                Product.createForSeller(
                        seller.getIdentification(),
                        productType,
                        status
                );

        /*
         * Conserva la relación dentro del modelo cargado
         * durante la operación actual.
         */
        seller.registerProduct(product);

        productRepository.save(product);

        return product;
    }

    /**
     * Busca y valida al vendedor solicitante.
     */
    private Seller findSeller(
            String sellerIdentification
    ) {
        validateIdentification(
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
                    "Only a seller can register products."
            );
        }

        return seller;
    }

    private void validateActiveSeller(
            Seller seller
    ) {
        if (!seller.isActive()) {
            throw new IllegalStateException(
                    "Only an active seller can register products."
            );
        }
    }

    private void validateRepositories(
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }

        if (productRepository == null) {
            throw new IllegalArgumentException(
                    "Product repository must not be null."
            );
        }
    }

    private void validateIdentification(
            String identification
    ) {
        if (identification == null
                || identification.isBlank()) {
            throw new IllegalArgumentException(
                    "Seller identification must not be empty."
            );
        }
    }
}