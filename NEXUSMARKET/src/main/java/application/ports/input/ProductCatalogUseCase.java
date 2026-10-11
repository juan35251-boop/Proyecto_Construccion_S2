package application.ports.input;

import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductVariant;

/**
 * Puerto de entrada que define el caso de uso
 * para administrar el catálogo de productos.
 *
 * Un vendedor activo solamente puede modificar
 * productos pertenecientes a su propio catálogo.
 */
public interface ProductCatalogUseCase {

    /**
     * Agrega una variante a un producto del vendedor.
     *
     * @param seller vendedor que realiza la operación
     * @param product producto que será modificado
     * @param variant variante que se desea agregar
     * @return producto actualizado
     */
    Product addVariant(
            Seller seller,
            Product product,
            ProductVariant variant
    );

    /**
     * Elimina una variante de un producto.
     *
     * @param seller vendedor que realiza la operación
     * @param product producto que será modificado
     * @param variant variante que se desea eliminar
     * @return true si la variante existía y fue eliminada
     */
    boolean removeVariant(
            Seller seller,
            Product product,
            ProductVariant variant
    );

    /**
     * Cambia el estado de un producto.
     *
     * @param seller vendedor que realiza la operación
     * @param product producto que será modificado
     * @param newStatus nuevo estado del producto
     * @return producto actualizado
     */
    Product changeStatus(
            Seller seller,
            Product product,
            ProductStatus newStatus
    );
}