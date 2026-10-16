package application.services;

import application.domain.models.Buyer;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.UserStatus;
import application.ports.output.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class BuyerAddressServiceTest {

    private UserRepository userRepository;
    private BuyerAddressService buyerAddressService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);

        buyerAddressService =
                new BuyerAddressService(userRepository);
    }

    @Test
    void shouldChangePrimaryAddress() {
        Buyer buyer = createBuyer();

        Buyer updatedBuyer =
                buyerAddressService.changePrimaryAddress(
                        buyer,
                        "Carrera 50 #40-20"
                );

        assertEquals(
                "Carrera 50 #40-20",
                updatedBuyer.getPrimaryAddress()
        );
    }

    @Test
    void shouldAddAdditionalAddress() {
        Buyer buyer = createBuyer();

        Buyer updatedBuyer =
                buyerAddressService.addAdditionalAddress(
                        buyer,
                        "Carrera 40 #50-60"
                );

        assertTrue(
                updatedBuyer
                        .getAdditionalAddresses()
                        .contains("Carrera 40 #50-60")
        );
    }

    @Test
    void shouldRemoveAdditionalAddress() {
        Buyer buyer = createBuyer();

        buyerAddressService.addAdditionalAddress(
                buyer,
                "Carrera 40 #50-60"
        );

        boolean removed =
                buyerAddressService.removeAdditionalAddress(
                        buyer,
                        "Carrera 40 #50-60"
                );

        assertTrue(removed);

        assertFalse(
                buyer.getAdditionalAddresses()
                        .contains("Carrera 40 #50-60")
        );
    }

    @Test
    void shouldReturnFalseWhenAddressDoesNotExist() {
        Buyer buyer = createBuyer();

        boolean removed =
                buyerAddressService.removeAdditionalAddress(
                        buyer,
                        "Dirección inexistente"
                );

        assertFalse(removed);
    }

    @Test
    void shouldRejectEmptyPrimaryAddress() {
        Buyer buyer = createBuyer();

        assertThrows(
                IllegalArgumentException.class,
                () -> buyerAddressService.changePrimaryAddress(
                        buyer,
                        ""
                )
        );
    }

    @Test
    void shouldRejectEmptyAdditionalAddress() {
        Buyer buyer = createBuyer();

        assertThrows(
                IllegalArgumentException.class,
                () -> buyerAddressService.addAdditionalAddress(
                        buyer,
                        ""
                )
        );
    }

    @Test
    void shouldRejectNullBuyerWhenChangingPrimaryAddress() {
        assertThrows(
                IllegalArgumentException.class,
                () -> buyerAddressService.changePrimaryAddress(
                        (Buyer) null,
                        "Carrera 50 #40-20"
                )
        );
    }

    @Test
    void shouldRejectNullBuyerWhenAddingAddress() {
        assertThrows(
                IllegalArgumentException.class,
                () -> buyerAddressService.addAdditionalAddress(
                        (Buyer) null,
                        "Carrera 40 #50-60"
                )
        );
    }

    @Test
    void shouldRejectNullBuyerWhenRemovingAddress() {
        assertThrows(
                IllegalArgumentException.class,
                () -> buyerAddressService.removeAdditionalAddress(
                        (Buyer) null,
                        "Carrera 40 #50-60"
                )
        );
    }

    private Buyer createBuyer() {
        return new Buyer(
                "1001",
                "Juan Perez",
                "juan@email.com",
                UserStatus.ACTIVE,
                "Calle 10 #20-30",
                BuyerStatus.ACTIVE
        );
    }
}