package application.adapters.input.rest.dto;

import application.domain.models.Product;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;

import java.util.List;

/**
 * Información de producto devuelta por la API.
 *
 * @param productId identificador del producto
 * @param sellerIdentification vendedor propietario
 * @param productType tipo físico o digital
 * @param status estado actual
 * @param variants variantes registradas
 */
public record ProductResponse(
        String productId,
        String sellerIdentification,
        ProductType productType,
        ProductStatus status,
        List<String> variants
) {

    /**
     * Convierte un producto del dominio
     * en una respuesta de la API.
     *
     * @param product producto recibido
     * @return respuesta correspondiente
     */
    public static ProductResponse from(
            Product product
    ) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Product must not be null."
            );
        }

        List<String> variants =
                product.getVariants()
                        .stream()
                        .map(ProductVariant::getDescription)
                        .toList();

        return new ProductResponse(
                product.getProductId(),
                product.getSellerIdentification(),
                product.getProductType(),
                product.getStatus(),
                variants
        );
    }
}