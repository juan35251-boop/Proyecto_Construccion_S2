package application.services;

import application.domain.models.Administrator;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.ports.output.ProductQueryRepository;
import application.ports.output.ProductRepository;
import application.services.support.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductCatalogServiceTest {

    private InMemoryUserRepository userRepository;
    private InMemoryProductRepository productRepository;
    private ProductCatalogService service;
    private Seller seller;
    private Product product;

    @BeforeEach
    void setUp() {
        userRepository =
                new InMemoryUserRepository();

        productRepository =
                new InMemoryProductRepository();

        service =
                new ProductCatalogService(
                        userRepository,
                        productRepository,
                        productRepository
                );

        seller =
                createSeller(
                        "SELLER-001",
                        UserStatus.ACTIVE
                );

        product =
                Product.createForSeller(
                        "SELLER-001",
                        ProductType.PHYSICAL,
                        ProductStatus.PUBLISHED
                );

        userRepository.save(seller);
        productRepository.add(product);
    }

    @Test
    void shouldAddVariant() {
        ProductVariant variant =
                new ProductVariant("Color negro");

        Product updatedProduct =
                service.addVariant(
                        "SELLER-001",
                        product.getProductId(),
                        variant
                );

        assertTrue(
                updatedProduct.hasVariant(variant)
        );

        assertEquals(
                1,
                productRepository.getSaveCount()
        );
    }

    @Test
    void shouldRemoveExistingVariant() {
        ProductVariant variant =
                new ProductVariant("Talla M");

        product.addVariant(variant);

        boolean removed =
                service.removeVariant(
                        "SELLER-001",
                        product.getProductId(),
                        variant
                );

        assertTrue(removed);
        assertFalse(product.hasVariant(variant));

        assertEquals(
                1,
                productRepository.getSaveCount()
        );
    }

    @Test
    void shouldNotSaveWhenVariantDoesNotExist() {
        boolean removed =
                service.removeVariant(
                        "SELLER-001",
                        product.getProductId(),
                        new ProductVariant("Talla XL")
                );

        assertFalse(removed);

        assertEquals(
                0,
                productRepository.getSaveCount()
        );
    }

    @Test
    void shouldChangeProductStatus() {
        Product updatedProduct =
                service.changeStatus(
                        "SELLER-001",
                        product.getProductId(),
                        ProductStatus.SUSPENDED
                );

        assertEquals(
                ProductStatus.SUSPENDED,
                updatedProduct.getStatus()
        );

        assertEquals(
                1,
                productRepository.getSaveCount()
        );
    }

    @Test
    void shouldRejectProductOwnedByAnotherSeller() {
        Seller anotherSeller =
                createSeller(
                        "SELLER-002",
                        UserStatus.ACTIVE
                );

        userRepository.save(anotherSeller);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> service.changeStatus(
                                "SELLER-002",
                                product.getProductId(),
                                ProductStatus.SUSPENDED
                        )
                );

        assertEquals(
                "Seller can only manage their own products.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectInactiveSeller() {
        Seller inactiveSeller =
                createSeller(
                        "SELLER-002",
                        UserStatus.INACTIVE
                );

        userRepository.save(inactiveSeller);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> service.addVariant(
                                "SELLER-002",
                                product.getProductId(),
                                new ProductVariant("Nueva")
                        )
                );

        assertEquals(
                "Only an active seller can manage products.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectUnknownProduct() {
        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> service.changeStatus(
                                "SELLER-001",
                                "UNKNOWN-PRODUCT",
                                ProductStatus.SUSPENDED
                        )
                );

        assertEquals(
                "Product was not found.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectUnknownSeller() {
        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> service.changeStatus(
                                "UNKNOWN-SELLER",
                                product.getProductId(),
                                ProductStatus.SUSPENDED
                        )
                );

        assertEquals(
                "Seller was not found.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNonSellerUser() {
        Administrator administrator =
                new Administrator(
                        "ADMIN-001",
                        "Administrator",
                        "admin@nexusmarket.com",
                        UserStatus.ACTIVE
                );

        userRepository.save(administrator);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> service.changeStatus(
                                "ADMIN-001",
                                product.getProductId(),
                                ProductStatus.SUSPENDED
                        )
                );

        assertEquals(
                "Catalog operation requires a seller.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNullUserRepository() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new ProductCatalogService(
                                        null,
                                        productRepository,
                                        productRepository
                                )
                );

        assertEquals(
                "User repository must not be null.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNullQueryRepository() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new ProductCatalogService(
                                        userRepository,
                                        null,
                                        productRepository
                                )
                );

        assertEquals(
                "Product query repository must not be null.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNullProductRepository() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new ProductCatalogService(
                                        userRepository,
                                        productRepository,
                                        null
                                )
                );

        assertEquals(
                "Product repository must not be null.",
                exception.getMessage()
        );
    }

    private Seller createSeller(
            String identification,
            UserStatus status
    ) {
        return new Seller(
                identification,
                "Nexus Seller",
                identification.toLowerCase()
                        + "@nexusmarket.com",
                status,
                new Warehouse(
                        WarehouseOwnerType.SELLER
                )
        );
    }

    private static class InMemoryProductRepository
            implements ProductRepository,
            ProductQueryRepository {

        private final List<Product> products =
                new ArrayList<>();

        private int saveCount;

        public void add(Product product) {
            products.add(product);
        }

        @Override
        public void save(Product product) {
            products.removeIf(
                    storedProduct ->
                            storedProduct
                                    .getProductId()
                                    .equals(
                                            product.getProductId()
                                    )
            );

            products.add(product);
            saveCount++;
        }

        @Override
        public Optional<Product> findById(
                String productId
        ) {
            return products.stream()
                    .filter(
                            product ->
                                    product.getProductId()
                                            .equals(productId)
                    )
                    .findFirst();
        }

        @Override
        public List<Product> findAll() {
            return List.copyOf(products);
        }

        public int getSaveCount() {
            return saveCount;
        }
    }
}