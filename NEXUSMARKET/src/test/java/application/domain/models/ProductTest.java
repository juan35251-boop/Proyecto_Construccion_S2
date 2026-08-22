package application.domain.models;

import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductTest {

    @Test
    void shouldCreatePhysicalPublishedProduct() {
        Product product = new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );

        assertEquals(ProductType.PHYSICAL, product.getProductType());
        assertEquals(ProductStatus.PUBLISHED, product.getStatus());
        assertTrue(product.isPhysical());
        assertTrue(product.isPublished());
        assertFalse(product.hasVariants());
    }

    @Test
    void shouldRejectNullProductType() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Product(
                        null,
                        ProductStatus.PUBLISHED
                )
        );
    }

    @Test
    void shouldRejectNullProductStatus() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Product(
                        ProductType.DIGITAL,
                        null
                )
        );
    }

    @Test
    void shouldChangeProductStatus() {
        Product product = createProduct();

        product.changeStatus(ProductStatus.SUSPENDED);

        assertEquals(ProductStatus.SUSPENDED, product.getStatus());
    }

    @Test
    void shouldRejectNullStatusChange() {
        Product product = createProduct();

        assertThrows(
                IllegalArgumentException.class,
                () -> product.changeStatus(null)
        );
    }

    @Test
    void shouldAddProductVariant() {
        Product product = createProduct();
        ProductVariant variant = new ProductVariant(
                "Color: Black, Size: M"
        );

        product.addVariant(variant);

        assertTrue(product.hasVariants());
        assertTrue(product.hasVariant(variant));
        assertEquals(1, product.getVariants().size());
    }

    @Test
    void shouldRejectDuplicateProductVariant() {
        Product product = createProduct();

        product.addVariant(
                new ProductVariant("Color: Black")
        );

        assertThrows(
                IllegalStateException.class,
                () -> product.addVariant(
                        new ProductVariant("Color: Black")
                )
        );
    }

    @Test
    void shouldRemoveProductVariant() {
        Product product = createProduct();
        ProductVariant variant = new ProductVariant("Size: L");

        product.addVariant(variant);

        assertTrue(product.removeVariant(variant));
        assertFalse(product.hasVariant(variant));
        assertFalse(product.hasVariants());
    }

    @Test
    void shouldRejectNullProductVariant() {
        Product product = createProduct();

        assertThrows(
                IllegalArgumentException.class,
                () -> product.addVariant(null)
        );
    }

    @Test
    void shouldRejectEmptyVariantDescription() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ProductVariant("")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new ProductVariant("   ")
        );
    }

    @Test
    void shouldCompareVariantsByValue() {
        ProductVariant first = new ProductVariant("Model: 2026");
        ProductVariant second = new ProductVariant("Model: 2026");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    private Product createProduct() {
        return new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );
    }
}
