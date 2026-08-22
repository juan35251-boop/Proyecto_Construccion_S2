package application.domain.models;

import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    }

    @Test
    void shouldRejectNullProductType() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Product(null, ProductStatus.PUBLISHED)
        );
    }

    @Test
    void shouldRejectNullProductStatus() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Product(ProductType.DIGITAL, null)
        );
    }

    @Test
    void shouldChangeProductStatus() {
        Product product = new Product(
                ProductType.DIGITAL,
                ProductStatus.PUBLISHED
        );

        product.changeStatus(ProductStatus.SUSPENDED);

        assertEquals(ProductStatus.SUSPENDED, product.getStatus());
    }

    @Test
    void shouldRejectNullStatusChange() {
        Product product = new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> product.changeStatus(null)
        );
    }
}
