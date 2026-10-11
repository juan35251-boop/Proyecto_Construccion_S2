package application.services;

import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductVariant;
import application.ports.input.ProductCatalogUseCase;
import application.ports.output.ProductRepository;

/**
 * Servicio de aplicación encargado de administrar
 * los productos del catálogo de un vendedor.
 *
 * Implementa el puerto de entrada ProductCatalogUseCase,
 * permitiendo administrar el catálogo sin depender
 * directamente de esta implementación.
 *
 * Permite agregar o eliminar variantes y cambiar
 * el estado de un producto.
 */
public class ProductCatalogService
        implements ProductCatalogUseCase {

    private final ProductRepository productRepository;

    /**
     * Construye el servicio con el repositorio de productos.
     *
     * @param productRepository repositorio utilizado
     *                          para actualizar productos
     */
    public ProductCatalogService(
            ProductRepository productRepository
    ) {
        validateRepository(productRepository);
        this.productRepository = productRepository;
    }

    /**
     * Agrega una variante a un producto del vendedor.
     *
     * @param seller vendedor que realiza la operación
     * @param product producto que será modificado
     * @param variant variante que se desea agregar
     * @return producto actualizado
     */
    @Override
    public Product addVariant(
            Seller seller,
            Product product,
            ProductVariant variant
    ) {
        validateProductManagement(
                seller,
                product
        );

        /*
         * Product valida que la variante exista
         * y que no se encuentre repetida.
         */
        product.addVariant(variant);

        productRepository.save(product);

        return product;
    }

    /**
     * Elimina una variante de un producto.
     *
     * @param seller vendedor que realiza la operación
     * @param product producto que será modificado
     * @param variant variante que se desea eliminar
     * @return true si la variante existía y fue eliminada
     */
    @Override
    public boolean removeVariant(
            Seller seller,
            Product product,
            ProductVariant variant
    ) {
        validateProductManagement(
                seller,
                product
        );

        boolean removed =
                product.removeVariant(variant);

        /*
         * Solo se guarda el producto cuando realmente
         * ocurrió una modificación.
         */
        if (removed) {
            productRepository.save(product);
        }

        return removed;
    }

    /**
     * Cambia el estado de un producto.
     *
     * Por ejemplo, permite pasar el producto a publicado,
     * suspendido o descontinuado.
     *
     * @param seller vendedor que realiza la operación
     * @param product producto que será modificado
     * @param newStatus nuevo estado
     * @return producto actualizado
     */
    @Override
    public Product changeStatus(
            Seller seller,
            Product product,
            ProductStatus newStatus
    ) {
        validateProductManagement(
                seller,
                product
        );

        /*
         * Product valida que el nuevo estado
         * no sea nulo.
         */
        product.changeStatus(newStatus);

        productRepository.save(product);

        return product;
    }

    /**
     * Valida que el repositorio exista.
     *
     * @param productRepository repositorio de productos
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
     * Comprueba que el vendedor esté activo y que administre
     * el producto sobre el cual realiza la operación.
     *
     * @param seller vendedor que realiza la operación
     * @param product producto administrado
     */
    private void validateProductManagement(
            Seller seller,
            Product product
    ) {
        if (seller == null) {
            throw new IllegalArgumentException(
                    "Catalog operation requires a seller."
            );
        }

        if (!seller.isActive()) {
            throw new IllegalStateException(
                    "Only an active seller can manage products."
            );
        }

        if (product == null) {
            throw new IllegalArgumentException(
                    "Product must not be null."
            );
        }

        if (!seller.managesProduct(product)) {
            throw new IllegalStateException(
                    "Seller can only manage their own products."
            );
        }
    }
}