package application.domain.models;

import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefundTest {

    @Test
    void shouldCreateRefundProcessedByAdministrator() {
        Return returnProcess = createReturn();
        Administrator administrator = createAdministrator(
                UserStatus.ACTIVE
        );

        Refund refund = new Refund(
                returnProcess,
                administrator
        );

        assertEquals(
                returnProcess,
                refund.getReturnProcess()
        );

        assertEquals(
                administrator,
                refund.getProcessedBy()
        );

        assertEquals(
                returnProcess.getBuyer(),
                refund.getBuyer()
        );

        assertEquals(
                returnProcess.getOrder(),
                refund.getOrder()
        );

        assertTrue(refund.belongsTo(returnProcess));
    }

    @Test
    void shouldRejectNullReturn() {
        Administrator administrator = createAdministrator(
                UserStatus.ACTIVE
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Refund(null, administrator)
        );
    }

    @Test
    void shouldRejectNullAdministrator() {
        Return returnProcess = createReturn();

        assertThrows(
                IllegalArgumentException.class,
                () -> new Refund(returnProcess, null)
        );
    }

    @Test
    void shouldRejectInactiveAdministrator() {
        Return returnProcess = createReturn();

        Administrator administrator = createAdministrator(
                UserStatus.INACTIVE
        );

        assertThrows(
                IllegalStateException.class,
                () -> new Refund(
                        returnProcess,
                        administrator
                )
        );
    }

    @Test
    void shouldRejectBlockedAdministrator() {
        Return returnProcess = createReturn();

        Administrator administrator = createAdministrator(
                UserStatus.BLOCKED
        );

        assertThrows(
                IllegalStateException.class,
                () -> new Refund(
                        returnProcess,
                        administrator
                )
        );
    }

    private Return createReturn() {
        Buyer buyer = new Buyer(
                "1001",
                "Juan Perez",
                "juan@email.com",
                UserStatus.ACTIVE,
                "Main Street 10",
                BuyerStatus.ACTIVE
        );

        Product product = new Product(
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );

        Cart cart = new Cart(buyer);
        cart.addProduct(product, 1);

        Order order = new Order(cart);
        order.markAsPaid();
        order.dispatch();
        order.markAsDelivered();

        return new Return(order, buyer);
    }

    private Administrator createAdministrator(
            UserStatus status
    ) {
        return new Administrator(
                "4001",
                "Administrator",
                "admin@email.com",
                status
        );
    }
}
