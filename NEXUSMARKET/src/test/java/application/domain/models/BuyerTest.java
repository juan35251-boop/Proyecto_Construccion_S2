package application.domain.models;

import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuyerTest {

    @Test
    void shouldCreateActiveBuyer() {
        Buyer buyer = createActiveBuyer();

        assertEquals("1001", buyer.getIdentification());
        assertEquals("Juan Perez", buyer.getFullName());
        assertEquals("juan@email.com", buyer.getEmail());
        assertEquals(UserStatus.ACTIVE, buyer.getStatus());
        assertEquals(SystemRole.BUYER, buyer.getRole());
        assertEquals("Main Street 10", buyer.getPrimaryAddress());
        assertEquals(BuyerStatus.ACTIVE, buyer.getCommercialStatus());
        assertTrue(buyer.canPurchase());
    }

    @Test
    void shouldNotPurchaseWhenCommerciallySuspended() {
        Buyer buyer = createActiveBuyer();

        buyer.changeCommercialStatus(BuyerStatus.SUSPENDED);

        assertFalse(buyer.canPurchase());
    }

    @Test
    void shouldNotPurchaseWhenUserIsBlocked() {
        Buyer buyer = createActiveBuyer();

        buyer.changeStatus(UserStatus.BLOCKED);

        assertFalse(buyer.canPurchase());
        assertTrue(buyer.isBlocked());
    }

    @Test
    void shouldAddAndRemoveAdditionalAddress() {
        Buyer buyer = createActiveBuyer();

        buyer.addAdditionalAddress("Secondary Street 20");

        assertEquals(1, buyer.getAdditionalAddresses().size());
        assertTrue(
                buyer.getAdditionalAddresses()
                        .contains("Secondary Street 20")
        );

        assertTrue(
                buyer.removeAdditionalAddress("Secondary Street 20")
        );

        assertTrue(buyer.getAdditionalAddresses().isEmpty());
    }

    @Test
    void shouldRejectEmptyPrimaryAddress() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Buyer(
                        "1001",
                        "Juan Perez",
                        "juan@email.com",
                        UserStatus.ACTIVE,
                        "",
                        BuyerStatus.ACTIVE
                )
        );
    }

    @Test
    void shouldRejectNullCommercialStatus() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Buyer(
                        "1001",
                        "Juan Perez",
                        "juan@email.com",
                        UserStatus.ACTIVE,
                        "Main Street 10",
                        null
                )
        );
    }

    @Test
    void shouldRejectEmptyInheritedUserData() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Buyer(
                        "",
                        "Juan Perez",
                        "juan@email.com",
                        UserStatus.ACTIVE,
                        "Main Street 10",
                        BuyerStatus.ACTIVE
                )
        );
    }

    private Buyer createActiveBuyer() {
        return new Buyer(
                "1001",
                "Juan Perez",
                "juan@email.com",
                UserStatus.ACTIVE,
                "Main Street 10",
                BuyerStatus.ACTIVE
        );
    }
}