package application.services;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Invoice;
import application.domain.models.LogisticsOperator;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.Supervisor;
import application.domain.models.Warehouse;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.services.support.InMemoryInvoiceQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del servicio de consulta de facturas.
 */
class InvoiceQueryServiceTest {

    @Test
    @DisplayName("El administrador consulta todas las facturas")
    void administratorShouldFindAllInvoices() {
        InvoiceQueryService service =
                new InvoiceQueryService(createRepository());

        List<Invoice> invoices =
                service.findAccessibleInvoices(
                        createAdministrator()
                );

        assertEquals(2, invoices.size());
    }

    @Test
    @DisplayName("El supervisor consulta todas las facturas")
    void supervisorShouldFindAllInvoices() {
        InvoiceQueryService service =
                new InvoiceQueryService(createRepository());

        List<Invoice> invoices =
                service.findAccessibleInvoices(
                        createSupervisor()
                );

        assertEquals(2, invoices.size());
    }

    @Test
    @DisplayName("El comprador consulta sus propias facturas")
    void buyerShouldFindOwnInvoices() {
        InMemoryInvoiceQueryRepository repository =
                new InMemoryInvoiceQueryRepository();

        Buyer buyer = createBuyer("1001");
        Buyer otherBuyer = createBuyer("1002");

        Invoice ownInvoice = createInvoice(
                buyer,
                createProduct()
        );

        Invoice otherInvoice = createInvoice(
                otherBuyer,
                createProduct()
        );

        repository.add(ownInvoice);
        repository.add(otherInvoice);

        InvoiceQueryService service =
                new InvoiceQueryService(repository);

        List<Invoice> result =
                service.findAccessibleInvoices(buyer);

        assertEquals(1, result.size());
        assertTrue(result.contains(ownInvoice));
    }

    @Test
    @DisplayName("El vendedor consulta facturas con sus productos")
    void sellerShouldFindRelatedInvoices() {
        InMemoryInvoiceQueryRepository repository =
                new InMemoryInvoiceQueryRepository();

        Product ownProduct = createProduct();
        Product otherProduct = createProduct();

        Seller seller = createSeller(UserStatus.ACTIVE);
        seller.registerProduct(ownProduct);

        Invoice relatedInvoice = createInvoice(
                createBuyer("1001"),
                ownProduct
        );

        Invoice unrelatedInvoice = createInvoice(
                createBuyer("1002"),
                otherProduct
        );

        repository.add(relatedInvoice);
        repository.add(unrelatedInvoice);

        InvoiceQueryService service =
                new InvoiceQueryService(repository);

        List<Invoice> result =
                service.findAccessibleInvoices(seller);

        assertEquals(1, result.size());
        assertTrue(result.contains(relatedInvoice));
    }

    @Test
    @DisplayName("El operador logístico no consulta facturas")
    void logisticsOperatorShouldNotQueryInvoices() {
        InvoiceQueryService service =
                new InvoiceQueryService(createRepository());

        assertThrows(
                IllegalStateException.class,
                () -> service.findAccessibleInvoices(
                        createLogisticsOperator()
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario inactivo")
    void shouldRejectInactiveUser() {
        InvoiceQueryService service =
                new InvoiceQueryService(createRepository());

        assertThrows(
                IllegalStateException.class,
                () -> service.findAccessibleInvoices(
                        createSeller(UserStatus.INACTIVE)
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario nulo")
    void shouldRejectNullUser() {
        InvoiceQueryService service =
                new InvoiceQueryService(createRepository());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.findAccessibleInvoices(null)
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio nulo")
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new InvoiceQueryService(null)
        );
    }

    private InMemoryInvoiceQueryRepository
            createRepository() {
        InMemoryInvoiceQueryRepository repository =
                new InMemoryInvoiceQueryRepository();

        repository.add(
                createInvoice(
                        createBuyer("1001"),
                        createProduct()
                )
        );

        repository.add(
                createInvoice(
                        createBuyer("1002"),
                        createProduct()
                )
        );

        return repository;
    }

    private Invoice createInvoice(
            Buyer buyer,
            Product product
    ) {
        Order order = createOrder(buyer, product);
        order.markAsPaid();

        return new Invoice(order);
    }

    private Order createOrder(
            Buyer buyer,
            Product product
    ) {
        Cart cart = new Cart(buyer);
        cart.addProduct(product, 1);

        return new Order(cart);
    }

    private Product createProduct() {
        return new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );
    }

    private Buyer createBuyer(String identification) {
        return new Buyer(
                identification,
                "Buyer " + identification,
                identification + "@email.com",
                UserStatus.ACTIVE,
                "Main Street 10",
                BuyerStatus.ACTIVE
        );
    }

    private Seller createSeller(UserStatus status) {
        return new Seller(
                "2001",
                "Seller",
                "seller@email.com",
                status,
                new Warehouse(WarehouseOwnerType.SELLER)
        );
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

    private LogisticsOperator createLogisticsOperator() {
        return new LogisticsOperator(
                "3001",
                "Logistics Operator",
                "operator@email.com",
                UserStatus.ACTIVE
        );
    }
}