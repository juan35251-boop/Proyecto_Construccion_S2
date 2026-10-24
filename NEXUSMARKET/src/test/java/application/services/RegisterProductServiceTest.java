package application.services;

import application.domain.models.Administrator;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.ports.output.ProductRepository;
import application.services.support.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterProductServiceTest {

    private InMemoryUserRepository userRepository;
    private RecordingProductRepository productRepository;
    private RegisterProductService service;

    @BeforeEach
    void setUp() {
        userRepository =
                new InMemoryUserRepository();

        productRepository =
                new RecordingProductRepository();

        service =
                new RegisterProductService(
                        userRepository,
                        productRepository
                );
    }

    @Test
    void shouldRegisterProductForActiveSeller() {
        Seller seller =
                createSeller(
                        "SELLER-001",
                        UserStatus.ACTIVE
                );

        userRepository.save(seller);

        Product product =
                service.register(
                        "SELLER-001",
                        ProductType.PHYSICAL,
                        ProductStatus.PUBLISHED
                );

        assertEquals(
                ProductType.PHYSICAL,
                product.getProductType()
        );

        assertEquals(
                ProductStatus.PUBLISHED,
                product.getStatus()
        );

        assertEquals(
                "SELLER-001",
                product.getSellerIdentification()
        );

        assertTrue(
                product.belongsToSeller("SELLER-001")
        );

        assertTrue(seller.managesProduct(product));

        assertSame(
                product,
                productRepository.savedProduct
        );

        assertEquals(
                1,
                productRepository.saveCount
        );
    }

    @Test
    void shouldRegisterDigitalProduct() {
        Seller seller =
                createSeller(
                        "SELLER-001",
                        UserStatus.ACTIVE
                );

        userRepository.save(seller);

        Product product =
                service.register(
                        "SELLER-001",
                        ProductType.DIGITAL,
                        ProductStatus.SUSPENDED
                );

        assertTrue(product.isDigital());

        assertEquals(
                ProductStatus.SUSPENDED,
                product.getStatus()
        );
    }

    @Test
    void shouldRejectInactiveSeller() {
        Seller seller =
                createSeller(
                        "SELLER-001",
                        UserStatus.INACTIVE
                );

        userRepository.save(seller);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> service.register(
                                "SELLER-001",
                                ProductType.PHYSICAL,
                                ProductStatus.PUBLISHED
                        )
                );

        assertEquals(
                "Only an active seller can register products.",
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
                        () -> service.register(
                                "ADMIN-001",
                                ProductType.PHYSICAL,
                                ProductStatus.PUBLISHED
                        )
                );

        assertEquals(
                "Only a seller can register products.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectUnknownSeller() {
        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> service.register(
                                "UNKNOWN",
                                ProductType.PHYSICAL,
                                ProductStatus.PUBLISHED
                        )
                );

        assertEquals(
                "Seller was not found.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectBlankSellerIdentification() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.register(
                                " ",
                                ProductType.PHYSICAL,
                                ProductStatus.PUBLISHED
                        )
                );

        assertEquals(
                "Seller identification must not be empty.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNullUserRepository() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new RegisterProductService(
                                        null,
                                        productRepository
                                )
                );

        assertEquals(
                "User repository must not be null.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNullProductRepository() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new RegisterProductService(
                                        userRepository,
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

    private static class RecordingProductRepository
            implements ProductRepository {

        private Product savedProduct;
        private int saveCount;

        @Override
        public void save(Product product) {
            this.savedProduct = product;
            this.saveCount++;
        }
    }
}