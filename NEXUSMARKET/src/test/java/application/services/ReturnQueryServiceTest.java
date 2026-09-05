package application.services;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Return;
import application.domain.models.Seller;
import application.domain.models.Supervisor;
import application.domain.models.Warehouse;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.services.support.InMemoryReturnQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del servicio de consulta de devoluciones.
 */
class ReturnQueryServiceTest {

    @Test
    @DisplayName("El administrador consulta todas las devoluciones")
    void administratorShouldFindAllReturns() {
        ReturnQueryService service =
                new ReturnQueryService(createRepository());

        List<Return> result =
                service.findAccessibleReturns(
                        createAdministrator()
                );

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("El supervisor consulta todas las devoluciones")
    void supervisorShouldFindAllReturns() {
        ReturnQueryService service =
                new ReturnQueryService(createRepository());

        List<Return> result =
                service.findAccessibleReturns(
                        createSupervisor()
                );

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("El comprador consulta sus devoluciones")
    void buyerShouldFindOwnReturns() {
        InMemoryReturnQueryRepository repository =
                new InMemoryReturnQueryRepository();

        Buyer buyer = createBuyer(
                "1001",
                UserStatus.ACTIVE
        );

        Return ownReturn = createReturn(buyer);

        Return otherReturn = createReturn(
                createBuyer(
                        "1002",
                        UserStatus.ACTIVE
                )
        );

        repository.add(ownReturn);
        repository.add(otherReturn);

        ReturnQueryService service =
                new ReturnQueryService(repository);

        List<Return> result =
                service.findAccessibleReturns(buyer);

        assertEquals(1, result.size());
        assertTrue(result.contains(ownReturn));
    }

    @Test
    @DisplayName("El vendedor no puede consultar devoluciones")
    void sellerShouldNotQueryReturns() {
        ReturnQueryService service =
                new ReturnQueryService(createRepository());

        assertThrows(
                IllegalStateException.class,
                () -> service.findAccessibleReturns(
                        createSeller()
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario inactivo")
    void shouldRejectInactiveUser() {
        ReturnQueryService service =
                new ReturnQueryService(createRepository());

        assertThrows(
                IllegalStateException.class,
                () -> service.findAccessibleReturns(
                        createBuyer(
                                "1001",
                                UserStatus.INACTIVE
                        )
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario nulo")
    void shouldRejectNullUser() {
        ReturnQueryService service =
                new ReturnQueryService(createRepository());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.findAccessibleReturns(null)
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio nulo")
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ReturnQueryService(null)
        );
    }

    private InMemoryReturnQueryRepository
            createRepository() {
        InMemoryReturnQueryRepository repository =
                new InMemoryReturnQueryRepository();

        repository.add(
                createReturn(
                        createBuyer(
                                "1001",
                                UserStatus.ACTIVE
                        )
                )
        );

        repository.add(
                createReturn(
                        createBuyer(
                                "1002",
                                UserStatus.ACTIVE
                        )
                )
        );

        return repository;
    }

    /**
     * Crea una devolución a partir de un pedido entregado.
     */
    private Return createReturn(Buyer buyer) {
        Order order = createOrder(buyer);

        order.markAsPaid();
        order.dispatch();
        order.markAsDelivered();

        return new Return(order, buyer);
    }

    private Order createOrder(Buyer buyer) {
        Product product = new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );

        Cart cart = new Cart(buyer);
        cart.addProduct(product, 1);

        return new Order(cart);
    }

    private Buyer createBuyer(
            String identification,
            UserStatus status
    ) {
        return new Buyer(
                identification,
                "Buyer " + identification,
                identification + "@email.com",
                status,
                "Main Street 10",
                BuyerStatus.ACTIVE
        );
    }

    private Seller createSeller() {
        return new Seller(
                "2001",
                "Seller",
                "seller@email.com",
                UserStatus.ACTIVE,
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
}