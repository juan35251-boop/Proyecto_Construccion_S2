package application.services;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.Seller;
import application.domain.models.Supervisor;
import application.domain.models.Warehouse;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.services.support.InMemoryRefundQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del servicio de consulta de reembolsos.
 */
class RefundQueryServiceTest {

    @Test
    @DisplayName("El administrador consulta todos los reembolsos")
    void administratorShouldFindAllRefunds() {
        RefundQueryService service =
                new RefundQueryService(createRepository());

        List<Refund> result =
                service.findAccessibleRefunds(
                        createAdministrator("9001")
                );

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("El supervisor consulta todos los reembolsos")
    void supervisorShouldFindAllRefunds() {
        RefundQueryService service =
                new RefundQueryService(createRepository());

        List<Refund> result =
                service.findAccessibleRefunds(
                        createSupervisor()
                );

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("El comprador consulta sus reembolsos")
    void buyerShouldFindOwnRefunds() {
        InMemoryRefundQueryRepository repository =
                new InMemoryRefundQueryRepository();

        Buyer buyer = createBuyer(
                "1001",
                UserStatus.ACTIVE
        );

        Refund ownRefund = createRefund(buyer, "9001");

        Refund otherRefund = createRefund(
                createBuyer(
                        "1002",
                        UserStatus.ACTIVE
                ),
                "9002"
        );

        repository.add(ownRefund);
        repository.add(otherRefund);

        RefundQueryService service =
                new RefundQueryService(repository);

        List<Refund> result =
                service.findAccessibleRefunds(buyer);

        assertEquals(1, result.size());
        assertTrue(result.contains(ownRefund));
    }

    @Test
    @DisplayName("El vendedor no puede consultar reembolsos")
    void sellerShouldNotQueryRefunds() {
        RefundQueryService service =
                new RefundQueryService(createRepository());

        assertThrows(
                IllegalStateException.class,
                () -> service.findAccessibleRefunds(
                        createSeller()
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario inactivo")
    void shouldRejectInactiveUser() {
        RefundQueryService service =
                new RefundQueryService(createRepository());

        assertThrows(
                IllegalStateException.class,
                () -> service.findAccessibleRefunds(
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
        RefundQueryService service =
                new RefundQueryService(createRepository());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.findAccessibleRefunds(null)
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio nulo")
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RefundQueryService(null)
        );
    }

    private InMemoryRefundQueryRepository
            createRepository() {
        InMemoryRefundQueryRepository repository =
                new InMemoryRefundQueryRepository();

        repository.add(
                createRefund(
                        createBuyer(
                                "1001",
                                UserStatus.ACTIVE
                        ),
                        "9001"
                )
        );

        repository.add(
                createRefund(
                        createBuyer(
                                "1002",
                                UserStatus.ACTIVE
                        ),
                        "9002"
                )
        );

        return repository;
    }

    /**
     * Crea un reembolso asociado a una devolución válida.
     */
    private Refund createRefund(
            Buyer buyer,
            String administratorIdentification
    ) {
        Return returnProcess = createReturn(buyer);

        Administrator administrator =
                createAdministrator(
                        administratorIdentification
                );

        return new Refund(
                returnProcess,
                administrator
        );
    }

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

    private Administrator createAdministrator(
            String identification
    ) {
        return new Administrator(
                identification,
                "Administrator",
                identification + "@email.com",
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

    private Seller createSeller() {
        return new Seller(
                "2001",
                "Seller",
                "seller@email.com",
                UserStatus.ACTIVE,
                new Warehouse(WarehouseOwnerType.SELLER)
        );
    }
}