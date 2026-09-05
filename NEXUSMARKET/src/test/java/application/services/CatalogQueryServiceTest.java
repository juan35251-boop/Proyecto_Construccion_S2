package application.services;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.LogisticsOperator;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.Supervisor;
import application.domain.models.Warehouse;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.services.support.InMemoryProductQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del servicio encargado de consultar el catálogo.
 */
class CatalogQueryServiceTest {

    @Test
    @DisplayName("El administrador puede consultar todo el catálogo")
    void administratorShouldFindAllProducts() {
        InMemoryProductQueryRepository repository =
                createRepository();

        CatalogQueryService service =
                new CatalogQueryService(repository);

        List<Product> products =
                service.findAccessibleProducts(
                        createAdministrator()
                );

        assertEquals(3, products.size());
    }

    @Test
    @DisplayName("El supervisor puede consultar todo el catálogo")
    void supervisorShouldFindAllProducts() {
        CatalogQueryService service =
                new CatalogQueryService(
                        createRepository()
                );

        List<Product> products =
                service.findAccessibleProducts(
                        createSupervisor()
                );

        assertEquals(3, products.size());
    }

    @Test
    @DisplayName("El comprador solamente consulta productos publicados")
    void buyerShouldOnlyFindPublishedProducts() {
        CatalogQueryService service =
                new CatalogQueryService(
                        createRepository()
                );

        List<Product> products =
                service.findAccessibleProducts(
                        createBuyer(UserStatus.ACTIVE)
                );

        assertEquals(1, products.size());
        assertTrue(products.get(0).isPublished());
    }

    @Test
    @DisplayName("El operador solamente consulta productos publicados")
    void logisticsOperatorShouldOnlyFindPublishedProducts() {
        CatalogQueryService service =
                new CatalogQueryService(
                        createRepository()
                );

        List<Product> products =
                service.findAccessibleProducts(
                        createLogisticsOperator(
                                UserStatus.ACTIVE
                        )
                );

        assertEquals(1, products.size());
        assertTrue(products.get(0).isPublished());
    }

    @Test
    @DisplayName("El vendedor solamente consulta sus productos")
    void sellerShouldOnlyFindOwnProducts() {
        InMemoryProductQueryRepository repository =
                new InMemoryProductQueryRepository();

        Product ownProduct = new Product(
                ProductType.PHYSICAL,
                ProductStatus.SUSPENDED
        );

        Product otherProduct = new Product(
                ProductType.DIGITAL,
                ProductStatus.PUBLISHED
        );

        repository.add(ownProduct);
        repository.add(otherProduct);

        Seller seller = createSeller(
                UserStatus.ACTIVE
        );

        seller.registerProduct(ownProduct);

        CatalogQueryService service =
                new CatalogQueryService(repository);

        List<Product> products =
                service.findAccessibleProducts(seller);

        assertEquals(1, products.size());
        assertTrue(products.contains(ownProduct));
    }

    @Test
    @DisplayName("Debe rechazar un usuario inactivo")
    void shouldRejectInactiveUser() {
        CatalogQueryService service =
                new CatalogQueryService(
                        createRepository()
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.findAccessibleProducts(
                        createBuyer(UserStatus.INACTIVE)
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario nulo")
    void shouldRejectNullUser() {
        CatalogQueryService service =
                new CatalogQueryService(
                        createRepository()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.findAccessibleProducts(null)
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio nulo")
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CatalogQueryService(null)
        );
    }

    /**
     * Crea un repositorio con productos en distintos estados.
     */
    private InMemoryProductQueryRepository
            createRepository() {
        InMemoryProductQueryRepository repository =
                new InMemoryProductQueryRepository();

        repository.add(
                new Product(
                        ProductType.PHYSICAL,
                        ProductStatus.PUBLISHED
                )
        );

        repository.add(
                new Product(
                        ProductType.DIGITAL,
                        ProductStatus.SUSPENDED
                )
        );

        repository.add(
                new Product(
                        ProductType.PHYSICAL,
                        ProductStatus.DISCONTINUED
                )
        );

        return repository;
    }

    private Administrator createAdministrator() {
        return new Administrator(
                "9001",
                "Administrator",
                "admin@email.com",
                UserStatus.ACTIVE
        );
    }

    private Supervisor createSupervisor() {
        return new Supervisor(
                "8001",
                "Supervisor",
                "supervisor@email.com",
                UserStatus.ACTIVE
        );
    }

    private Buyer createBuyer(UserStatus status) {
        return new Buyer(
                "1001",
                "Buyer",
                "buyer@email.com",
                status,
                "Main Street 10",
                BuyerStatus.ACTIVE
        );
    }

    private LogisticsOperator createLogisticsOperator(
            UserStatus status
    ) {
        return new LogisticsOperator(
                "3001",
                "Logistics Operator",
                "operator@email.com",
                status
        );
    }

    private Seller createSeller(UserStatus status) {
        return new Seller(
                "2001",
                "Seller",
                "seller@email.com",
                status,
                new Warehouse(
                        WarehouseOwnerType.SELLER
                )
        );
    }
}