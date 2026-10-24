package application.services;

import application.domain.models.Administrator;
import application.domain.models.LogisticsOperator;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.Supervisor;
import application.domain.models.Warehouse;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.services.support.InMemoryProductQueryRepository;
import application.services.support.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogQueryServiceTest {

    private InMemoryUserRepository userRepository;
    private InMemoryProductQueryRepository
            productRepository;
    private CatalogQueryService service;

    @BeforeEach
    void setUp() {
        userRepository =
                new InMemoryUserRepository();

        productRepository =
                new InMemoryProductQueryRepository();

        service =
                new CatalogQueryService(
                        userRepository,
                        productRepository
                );
    }

    @Test
    void shouldAllowAdministratorToSeeAllProducts() {
        Administrator administrator =
                new Administrator(
                        "ADMIN-001",
                        "Administrator",
                        "admin@nexusmarket.com",
                        UserStatus.ACTIVE
                );

        userRepository.save(administrator);
        addCatalogProducts();

        List<Product> result =
                service.findAccessibleProducts(
                        "ADMIN-001"
                );

        assertEquals(3, result.size());
    }

    @Test
    void shouldAllowSupervisorToSeeAllProducts() {
        Supervisor supervisor =
                new Supervisor(
                        "SUPERVISOR-001",
                        "Supervisor",
                        "supervisor@nexusmarket.com",
                        UserStatus.ACTIVE
                );

        userRepository.save(supervisor);
        addCatalogProducts();

        List<Product> result =
                service.findAccessibleProducts(
                        "SUPERVISOR-001"
                );

        assertEquals(3, result.size());
    }

    @Test
    void shouldAllowSellerToSeeOnlyOwnProducts() {
        Seller seller =
                createSeller(
                        "SELLER-001",
                        UserStatus.ACTIVE
                );

        userRepository.save(seller);
        addCatalogProducts();

        List<Product> result =
                service.findAccessibleProducts(
                        "SELLER-001"
                );

        assertEquals(2, result.size());

        assertTrue(
                result.stream().allMatch(
                        product ->
                                product.belongsToSeller(
                                        "SELLER-001"
                                )
                )
        );
    }

    @Test
    void shouldAllowLogisticsOperatorToSeePublishedProducts() {
        LogisticsOperator operator =
                new LogisticsOperator(
                        "OPERATOR-001",
                        "Logistics Operator",
                        "operator@nexusmarket.com",
                        UserStatus.ACTIVE
                );

        userRepository.save(operator);
        addCatalogProducts();

        List<Product> result =
                service.findAccessibleProducts(
                        "OPERATOR-001"
                );

        assertEquals(2, result.size());

        assertTrue(
                result.stream()
                        .allMatch(Product::isPublished)
        );
    }

    @Test
    void shouldRejectInactiveRequester() {
        Supervisor supervisor =
                new Supervisor(
                        "SUPERVISOR-001",
                        "Supervisor",
                        "supervisor@nexusmarket.com",
                        UserStatus.INACTIVE
                );

        userRepository.save(supervisor);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                service.findAccessibleProducts(
                                        "SUPERVISOR-001"
                                )
                );

        assertEquals(
                "Only active users can query the catalog.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectUnknownRequester() {
        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                service.findAccessibleProducts(
                                        "UNKNOWN"
                                )
                );

        assertEquals(
                "Requesting user was not found.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectBlankRequesterIdentification() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                service.findAccessibleProducts(
                                        " "
                                )
                );

        assertEquals(
                "Requester identification must not be empty.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNullUserRepository() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new CatalogQueryService(
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
    void shouldRejectNullProductQueryRepository() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new CatalogQueryService(
                                        userRepository,
                                        null
                                )
                );

        assertEquals(
                "Product query repository must not be null.",
                exception.getMessage()
        );
    }

    private void addCatalogProducts() {
        productRepository.add(
                new Product(
                        "PRODUCT-001",
                        "SELLER-001",
                        ProductType.PHYSICAL,
                        ProductStatus.PUBLISHED
                )
        );

        productRepository.add(
                new Product(
                        "PRODUCT-002",
                        "SELLER-001",
                        ProductType.DIGITAL,
                        ProductStatus.SUSPENDED
                )
        );

        productRepository.add(
                new Product(
                        "PRODUCT-003",
                        "SELLER-002",
                        ProductType.DIGITAL,
                        ProductStatus.PUBLISHED
                )
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
}