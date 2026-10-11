package application.ports.input;

import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;

/**
 * Puerto de entrada que define el caso de uso
 * para registrar productos en NexusMarket.
 *
 * Un producto solamente puede ser registrado
 * por un vendedor activo.
 */
public interface RegisterProductUseCase {

    /**
     * Registra un producto y lo asocia al catálogo
     * del vendedor que realiza la operación.
     *
     * @param seller vendedor que registra el producto
     * @param productType tipo del producto: físico o digital
     * @param status estado inicial del producto
     * @return producto registrado
     *
     * @throws IllegalArgumentException si el vendedor,
     *         el tipo o el estado son inválidos
     * @throws IllegalStateException si el vendedor
     *         no se encuentra activo
     */
    Product register(
            Seller seller,
            ProductType productType,
            ProductStatus status
    );
}