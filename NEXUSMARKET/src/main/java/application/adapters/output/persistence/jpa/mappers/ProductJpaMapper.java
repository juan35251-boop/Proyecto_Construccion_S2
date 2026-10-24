package application.adapters.output.persistence.jpa.mappers;

import application.adapters.output.persistence.jpa.entities.ProductJpaEntity;
import application.domain.models.Product;
import application.domain.valueobjects.ProductVariant;

import java.util.List;

/**
 * Convierte productos del dominio en entidades JPA
 * y entidades JPA en productos del dominio.
 */
public final class ProductJpaMapper {

    private ProductJpaMapper() {
    }

    public static ProductJpaEntity toEntity(
            Product product
    ) {
        validateProduct(product);

        List<String> variants =
                product.getVariants()
                        .stream()
                        .map(ProductVariant::getDescription)
                        .toList();

        return new ProductJpaEntity(
                product.getProductId(),
                product.getSellerIdentification(),
                product.getProductType(),
                product.getStatus(),
                variants
        );
    }

    public static Product toDomain(
            ProductJpaEntity entity
    ) {
        validateEntity(entity);

        Product product =
                new Product(
                        entity.getProductId(),
                        entity.getSellerIdentification(),
                        entity.getProductType(),
                        entity.getStatus()
                );

        for (String description
                : entity.getVariants()) {
            product.addVariant(
                    new ProductVariant(description)
            );
        }

        return product;
    }

    private static void validateProduct(
            Product product
    ) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Product must not be null."
            );
        }
    }

    private static void validateEntity(
            ProductJpaEntity entity
    ) {
        if (entity == null) {
            throw new IllegalArgumentException(
                    "Product entity must not be null."
            );
        }
    }
}