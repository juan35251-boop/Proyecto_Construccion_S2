package application.services;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.Cart;
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
import application.services.support.InMemoryOrderQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del servicio encargado de consultar pedidos.
 */
class OrderQueryServiceTest {

    @Test
    @DisplayName("El administrador puede consultar todos los pedidos")
    void administratorShouldFindAllOrders() {
        OrderQueryService service =
                new OrderQueryService(createRepository());

        List<Order> orders =
                service.findAccessibleOrders(
                        createAdministrator()
                );

        assertEquals(2, orders.size());
    }

    @Test
    @DisplayName("El supervisor puede consultar todos los pedidos")
    void supervisorShouldFindAllOrders() {
        OrderQueryService service =
                new OrderQueryService(createRepository());

        List<Order> orders =
                service.findAccessibleOrders(
                        createSupervisor()
                );

        assertEquals(2, orders.size());
    }

    @Test
    @DisplayName("El comprador consulta únicamente sus pedidos")
    void buyerShouldFindOwnOrders() {
        InMemoryOrderQueryRepository repository =
                new InMemoryOrderQueryRepository();

        Buyer requestingBuyer = createBuyer("1001");
        Buyer otherBuyer = createBuyer("1002");

        Order ownOrder = createOrder(
                requestingBuyer,
                createProduct(ProductType.PHYSICAL)
        );

        Order otherOrder = createOrder(
                otherBuyer,
                createProduct(ProductType.PHYSICAL)
        );

        repository.add(ownOrder);
        repository.add(otherOrder);

        OrderQueryService service =
                new OrderQueryService(repository);

        List<Order> result =
                service.findAccessibleOrders(
                        requestingBuyer
                );

        assertEquals(1, result.size());
        assertTrue(result.contains(ownOrder));
    }

    @Test
    @DisplayName("El vendedor consulta pedidos con sus productos")
    void sellerShouldFindOrdersWithOwnProducts() {
        InMemoryOrderQueryRepository repository =
                new InMemoryOrderQueryRepository();

        Product sellerProduct =
                createProduct(ProductType.PHYSICAL);

        Product otherProduct =
                createProduct(ProductType.PHYSICAL);

        Seller seller = createSeller(
                UserStatus.ACTIVE
        );

        seller.registerProduct(sellerProduct);

        Order relatedOrder = createOrder(
                createBuyer("1001"),
                sellerProduct
        );

        Order unrelatedOrder = createOrder(
                createBuyer("1002"),
                otherProduct
        );

        repository.add(relatedOrder);
        repository.add(unrelatedOrder);

        OrderQueryService service =
                new OrderQueryService(repository);

        List<Order> result =
                service.findAccessibleOrders(seller);

        assertEquals(1, result.size());
        assertTrue(result.contains(relatedOrder));
    }

    @Test
    @DisplayName("El operador consulta pedidos con productos físicos")
    void logisticsOperatorShouldFindPhysicalOrders() {
        InMemoryOrderQueryRepository repository =
                createRepository();

        OrderQueryService service =
                new OrderQueryService(repository);

        List<Order> result =
                service.findAccessibleOrders(
                        createLogisticsOperator(
                                UserStatus.ACTIVE
                        )
                );

        assertEquals(1, result.size());
        assertTrue(
                result.get(0)
                        .containsPhysicalProducts()
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario inactivo")
    void shouldRejectInactiveUser() {
        OrderQueryService service =
                new OrderQueryService(createRepository());

        assertThrows(
                IllegalStateException.class,
                () -> service.findAccessibleOrders(
                        createSeller(
                                UserStatus.INACTIVE
                        )
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario nulo")
    void shouldRejectNullUser() {
        OrderQueryService service =
                new OrderQueryService(createRepository());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.findAccessibleOrders(null)
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio nulo")
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new OrderQueryService(null)
        );
    }

    /**
     * Crea un repositorio con un pedido físico
     * y un pedido digital.
     */
    private InMemoryOrderQueryRepository
            createRepository() {
        InMemoryOrderQueryRepository repository =
                new InMemoryOrderQueryRepository();

        repository.add(
                createOrder(
                        createBuyer("1001"),
                        createProduct(ProductType.PHYSICAL)
                )
        );

        repository.add(
                createOrder(
                        createBuyer("1002"),
                        createProduct(ProductType.DIGITAL)
                )
        );

        return repository;
    }

    /**
     * Crea un pedido desde un carrito válido.
     */
    private Order createOrder(
            Buyer buyer,
            Product product
    ) {
        Cart cart = new Cart(buyer);
        cart.addProduct(product, 1);

        return new Order(cart);
    }

    private Product createProduct(
            ProductType productType
    ) {
        return new Product(
                productType,
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
                new Warehouse(
                        WarehouseOwnerType.SELLER
                )
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
}