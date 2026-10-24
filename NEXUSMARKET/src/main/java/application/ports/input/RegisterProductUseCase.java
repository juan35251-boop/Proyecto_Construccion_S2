package application.ports.input;

import application.domain.models.Product;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;

/**
 * Puerto de entrada para registrar productos.
 */
public interface RegisterProductUseCase {

    /**
     * Registra un producto perteneciente a un vendedor.
     *
     * @param sellerIdentification identificación del vendedor
     * @param productType tipo físico o digital
     * @param status estado inicial
     * @return producto registrado
     */
    Product register(
            String sellerIdentification,
            ProductType productType,
            ProductStatus status
    );
}