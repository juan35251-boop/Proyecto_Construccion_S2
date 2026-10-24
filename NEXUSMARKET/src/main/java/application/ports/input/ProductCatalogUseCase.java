package application.ports.input;

import application.domain.models.Product;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductVariant;

/**
 * Puerto de entrada para administrar
 * los productos de un vendedor.
 */
public interface ProductCatalogUseCase {

    /**
     * Agrega una variante a un producto.
     *
     * @param sellerIdentification identificación del vendedor
     * @param productId identificador del producto
     * @param variant variante que será agregada
     * @return producto actualizado
     */
    Product addVariant(
            String sellerIdentification,
            String productId,
            ProductVariant variant
    );

    /**
     * Elimina una variante de un producto.
     *
     * @param sellerIdentification identificación del vendedor
     * @param productId identificador del producto
     * @param variant variante que será eliminada
     * @return true si la variante fue eliminada
     */
    boolean removeVariant(
            String sellerIdentification,
            String productId,
            ProductVariant variant
    );

    /**
     * Cambia el estado de un producto.
     *
     * @param sellerIdentification identificación del vendedor
     * @param productId identificador del producto
     * @param newStatus nuevo estado
     * @return producto actualizado
     */
    Product changeStatus(
            String sellerIdentification,
            String productId,
            ProductStatus newStatus
    );
}