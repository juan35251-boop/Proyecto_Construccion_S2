package application.adapters.output.persistence.jpa.mappers;

import application.adapters.output.persistence.jpa.entities.ProductJpaEntity;
import application.domain.models.Product;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del convertidor entre Product
 * y ProductJpaEntity.
 */
class ProductJpaMapperTest {

    /**
     * Comprueba que un producto del dominio
     * sea convertido correctamente en entidad JPA.
     */
    @Test
    void shouldConvertProductToEntity() {
        Product product =
                new Product(
                        "PRODUCT-001",
                        ProductType.PHYSICAL,
                        ProductStatus.PUBLISHED
                );

        product.addVariant(
                new ProductVariant("Color negro")
        );

        product.addVariant(
                new ProductVariant("Color blanco")
        );

        ProductJpaEntity entity =
                ProductJpaMapper.toEntity(product);

        assertEquals(
                "PRODUCT-001",
                entity.getProductId()
        );

        assertEquals(
                ProductType.PHYSICAL,
                entity.getProductType()
        );

        assertEquals(
                ProductStatus.PUBLISHED,
                entity.getStatus()
        );

        assertEquals(
                List.of(
                        "Color negro",
                        "Color blanco"
                ),
                entity.getVariants()
        );
    }

    /**
     * Comprueba que una entidad JPA sea convertida
     * correctamente en producto del dominio.
     */
    @Test
    void shouldConvertEntityToProduct() {
        ProductJpaEntity entity =
                new ProductJpaEntity(
                        "PRODUCT-002",
                        ProductType.DIGITAL,
                        ProductStatus.SUSPENDED,
                        List.of(
                                "Licencia personal",
                                "Licencia empresarial"
                        )
                );

        Product product =
                ProductJpaMapper.toDomain(entity);

        assertEquals(
                "PRODUCT-002",
                product.getProductId()
        );

        assertEquals(
                ProductType.DIGITAL,
                product.getProductType()
        );

        assertEquals(
                ProductStatus.SUSPENDED,
                product.getStatus()
        );

        assertEquals(2, product.getVariants().size());

        assertTrue(
                product.hasVariant(
                        new ProductVariant(
                                "Licencia personal"
                        )
                )
        );

        assertTrue(
                product.hasVariant(
                        new ProductVariant(
                                "Licencia empresarial"
                        )
                )
        );
    }

    /**
     * Comprueba la conversión de un producto
     * que no posee variantes.
     */
    @Test
    void shouldConvertProductWithoutVariants() {
        Product product =
                new Product(
                        "PRODUCT-003",
                        ProductType.PHYSICAL,
                        ProductStatus.DISCONTINUED
                );

        ProductJpaEntity entity =
                ProductJpaMapper.toEntity(product);

        assertTrue(entity.getVariants().isEmpty());

        Product restoredProduct =
                ProductJpaMapper.toDomain(entity);

        assertTrue(
                restoredProduct.getVariants().isEmpty()
        );
    }

    /**
     * Comprueba que el identificador permanezca igual
     * después de convertir y reconstruir el producto.
     */
    @Test
    void shouldPreserveProductIdentification() {
        Product originalProduct =
                new Product(
                        "PRODUCT-004",
                        ProductType.DIGITAL,
                        ProductStatus.PUBLISHED
                );

        ProductJpaEntity entity =
                ProductJpaMapper.toEntity(
                        originalProduct
                );

        Product restoredProduct =
                ProductJpaMapper.toDomain(entity);

        assertEquals(
                originalProduct.getProductId(),
                restoredProduct.getProductId()
        );
    }

    /**
     * Comprueba que no pueda convertirse
     * un producto nulo.
     */
    @Test
    void shouldRejectNullProduct() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> ProductJpaMapper
                                .toEntity(null)
                );

        assertEquals(
                "Product must not be null.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba que no pueda convertirse
     * una entidad JPA nula.
     */
    @Test
    void shouldRejectNullProductEntity() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> ProductJpaMapper
                                .toDomain(null)
                );

        assertEquals(
                "Product entity must not be null.",
                exception.getMessage()
        );
    }
}