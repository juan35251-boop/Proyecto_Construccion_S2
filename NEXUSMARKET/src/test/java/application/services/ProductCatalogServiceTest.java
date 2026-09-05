package application.services;

import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.ports.output.ProductRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del servicio encargado
 * de gestionar el catálogo de productos.
 */
class ProductCatalogServiceTest {

    /**
     * Verifica que un vendedor pueda agregar una variante
     * a uno de sus productos.
     */
    @Test
    void shouldAddProductVariant() {
        TestContext context = createContext(
                UserStatus.ACTIVE
        );

        ProductVariant variant =
                new ProductVariant("Color: Blue");

        context.service.addVariant(
                context.seller,
                context.product,
                variant
        );

        assertTrue(context.product.hasVariant(variant));
        assertTrue(
                context.repository.contains(context.product)
        );
    }

    /**
     * Verifica que pueda eliminarse una variante existente.
     */
    @Test
    void shouldRemoveProductVariant() {
        TestContext context = createContext(
                UserStatus.ACTIVE
        );

        ProductVariant variant =
                new ProductVariant("Size: Medium");

        context.product.addVariant(variant);

        boolean removed = context.service.removeVariant(
                context.seller,
                context.product,
                variant
        );

        assertTrue(removed);
        assertFalse(context.product.hasVariant(variant));
        assertTrue(
                context.repository.contains(context.product)
        );
    }

    /**
     * Verifica que no se guarde el producto cuando
     * la variante que se desea eliminar no existe.
     */
    @Test
    void shouldNotSaveWhenVariantDoesNotExist() {
        TestContext context = createContext(
                UserStatus.ACTIVE
        );

        boolean removed = context.service.removeVariant(
                context.seller,
                context.product,
                new ProductVariant("Unknown variant")
        );

        assertFalse(removed);
        assertEquals(0, context.repository.getSaveCount());
    }

    /**
     * Verifica que el vendedor pueda cambiar
     * el estado de uno de sus productos.
     */
    @Test
    void shouldChangeProductStatus() {
        TestContext context = createContext(
                UserStatus.ACTIVE
        );

        context.service.changeStatus(
                context.seller,
                context.product,
                ProductStatus.SUSPENDED
        );

        assertEquals(
                ProductStatus.SUSPENDED,
                context.product.getStatus()
        );
        assertTrue(
                context.repository.contains(context.product)
        );
    }

    /**
     * Verifica que el repositorio sea obligatorio.
     */
    @Test
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ProductCatalogService(null)
        );
    }

    /**
     * Verifica que la operación requiera un vendedor.
     */
    @Test
    void shouldRejectNullSeller() {
        TestContext context = createContext(
                UserStatus.ACTIVE
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> context.service.changeStatus(
                        null,
                        context.product,
                        ProductStatus.SUSPENDED
                )
        );
    }

    /**
     * Verifica que un vendedor inactivo
     * no pueda administrar productos.
     */
    @Test
    void shouldRejectInactiveSeller() {
        TestContext context = createContext(
                UserStatus.INACTIVE
        );

        assertThrows(
                IllegalStateException.class,
                () -> context.service.changeStatus(
                        context.seller,
                        context.product,
                        ProductStatus.SUSPENDED
                )
        );
    }

    /**
     * Verifica que la operación requiera un producto.
     */
    @Test
    void shouldRejectNullProduct() {
        TestContext context = createContext(
                UserStatus.ACTIVE
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> context.service.changeStatus(
                        context.seller,
                        null,
                        ProductStatus.SUSPENDED
                )
        );
    }

    /**
     * Verifica que un vendedor no pueda modificar
     * productos que no pertenecen a su catálogo.
     */
    @Test
    void shouldRejectProductFromAnotherSeller() {
        TestContext context = createContext(
                UserStatus.ACTIVE
        );

        Product anotherProduct = new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );

        assertThrows(
                IllegalStateException.class,
                () -> context.service.changeStatus(
                        context.seller,
                        anotherProduct,
                        ProductStatus.SUSPENDED
                )
        );

        assertEquals(
                ProductStatus.PUBLISHED,
                anotherProduct.getStatus()
        );
    }

    /**
     * Verifica que una variante no pueda agregarse
     * dos veces al mismo producto.
     */
    @Test
    void shouldRejectDuplicatedVariant() {
        TestContext context = createContext(
                UserStatus.ACTIVE
        );

        ProductVariant variant =
                new ProductVariant("Color: Black");

        context.service.addVariant(
                context.seller,
                context.product,
                variant
        );

        assertThrows(
                IllegalStateException.class,
                () -> context.service.addVariant(
                        context.seller,
                        context.product,
                        variant
                )
        );

        assertEquals(1, context.product.getVariants().size());
    }

    /**
     * Verifica que el nuevo estado sea obligatorio.
     */
    @Test
    void shouldRejectNullProductStatus() {
        TestContext context = createContext(
                UserStatus.ACTIVE
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> context.service.changeStatus(
                        context.seller,
                        context.product,
                        null
                )
        );
    }

    /**
     * Crea el servicio, el vendedor, el producto
     * y el repositorio utilizados por una prueba.
     */
    private TestContext createContext(
            UserStatus sellerStatus
    ) {
        InMemoryProductRepository repository =
                new InMemoryProductRepository();

        ProductCatalogService service =
                new ProductCatalogService(repository);

        Warehouse warehouse = new Warehouse(
                WarehouseOwnerType.SELLER
        );

        Seller seller = new Seller(
                "2001",
                "Test Seller",
                "seller@email.com",
                sellerStatus,
                warehouse
        );

        Product product = new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );

        seller.registerProduct(product);

        return new TestContext(
                service,
                repository,
                seller,
                product
        );
    }

    /**
     * Agrupa los objetos utilizados durante cada prueba.
     */
    private record TestContext(
            ProductCatalogService service,
            InMemoryProductRepository repository,
            Seller seller,
            Product product
    ) {
    }

    /**
     * Repositorio de productos utilizado durante las pruebas.
     */
    private static class InMemoryProductRepository
            implements ProductRepository {

        private final List<Product> products =
                new ArrayList<>();

        @Override
        public void save(Product product) {
            products.add(product);
        }

        boolean contains(Product product) {
            return products.contains(product);
        }

        int getSaveCount() {
            return products.size();
        }
    }
}