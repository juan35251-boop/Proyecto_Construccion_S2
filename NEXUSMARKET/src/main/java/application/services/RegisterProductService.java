package application.services;

import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.ports.input.RegisterProductUseCase;
import application.ports.output.ProductRepository;

/**
 * Servicio de aplicación encargado de registrar productos.
 *
 * Implementa el puerto de entrada RegisterProductUseCase,
 * permitiendo que los futuros controladores REST ejecuten
 * esta operación mediante un contrato independiente.
 *
 * Coordina las reglas necesarias para que un vendedor activo
 * pueda crear un producto, asociarlo con su catálogo y
 * guardarlo mediante el repositorio.
 */
public class RegisterProductService
        implements RegisterProductUseCase {

    private final ProductRepository productRepository;

    /**
     * Construye el servicio con el repositorio que almacenará
     * los productos.
     *
     * La dependencia se recibe mediante el constructor para
     * facilitar las pruebas y evitar que el servicio dependa
     * directamente de una tecnología de base de datos.
     *
     * @param productRepository repositorio utilizado
     *                          para guardar productos
     */
    public RegisterProductService(
            ProductRepository productRepository
    ) {
        validateRepository(productRepository);
        this.productRepository = productRepository;
    }

    /**
     * Registra un producto administrado por un vendedor.
     *
     * Primero valida que el vendedor exista y esté activo.
     * Después crea el producto, lo agrega al catálogo del
     * vendedor y lo guarda mediante el puerto de salida.
     *
     * @param seller vendedor que registra el producto
     * @param productType tipo del producto: físico o digital
     * @param status estado inicial del producto
     * @return producto registrado
     *
     * @throws IllegalArgumentException si el vendedor,
     *         el tipo o el estado son inválidos
     * @throws IllegalStateException si el vendedor no está activo
     */
    @Override
    public Product register(
            Seller seller,
            ProductType productType,
            ProductStatus status
    ) {
        validateSeller(seller);
        validateActiveSeller(seller);

        /*
         * El constructor de Product valida que el tipo
         * y el estado no sean nulos.
         */
        Product product = new Product(
                productType,
                status
        );

        /*
         * La relación entre el vendedor y el producto
         * se mantiene dentro del modelo Seller.
         */
        seller.registerProduct(product);

        /*
         * El servicio utiliza el puerto de salida y desconoce
         * si el producto será almacenado en memoria, MySQL
         * u otra tecnología.
         */
        productRepository.save(product);

        return product;
    }

    /**
     * Valida que el servicio reciba un repositorio.
     *
     * @param productRepository repositorio que se desea validar
     */
    private void validateRepository(
            ProductRepository productRepository
    ) {
        if (productRepository == null) {
            throw new IllegalArgumentException(
                    "Product repository must not be null."
            );
        }
    }

    /**
     * Valida que se haya proporcionado un vendedor.
     *
     * @param seller vendedor que se desea validar
     */
    private void validateSeller(Seller seller) {
        if (seller == null) {
            throw new IllegalArgumentException(
                    "Product must be registered by a seller."
            );
        }
    }

    /**
     * Comprueba que el vendedor tenga un estado activo.
     *
     * @param seller vendedor que se desea validar
     */
    private void validateActiveSeller(Seller seller) {
        if (!seller.isActive()) {
            throw new IllegalStateException(
                    "Only an active seller can register products."
            );
        }
    }
}