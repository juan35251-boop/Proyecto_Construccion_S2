package application.services;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.ports.output.RefundRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del servicio encargado
 * de procesar reembolsos.
 */
class ProcessRefundServiceTest {

    /**
     * Verifica que un administrador activo pueda procesar
     * y guardar un reembolso.
     */
    @Test
    void shouldProcessRefund() {
        InMemoryRefundRepository repository =
                new InMemoryRefundRepository();

        ProcessRefundService service =
                new ProcessRefundService(repository);

        Return returnProcess = createReturn();

        Administrator administrator =
                createAdministrator(UserStatus.ACTIVE);

        Refund refund = service.process(
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
        assertTrue(repository.contains(refund));
    }

    /**
     * Verifica que el repositorio sea obligatorio.
     */
    @Test
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ProcessRefundService(null)
        );
    }

    /**
     * Verifica que no pueda procesarse un reembolso
     * sin una devolución.
     */
    @Test
    void shouldRejectNullReturn() {
        InMemoryRefundRepository repository =
                new InMemoryRefundRepository();

        ProcessRefundService service =
                new ProcessRefundService(repository);

        Administrator administrator =
                createAdministrator(UserStatus.ACTIVE);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.process(
                        null,
                        administrator
                )
        );

        assertTrue(repository.getRefunds().isEmpty());
    }

    /**
     * Verifica que el reembolso requiera un administrador.
     */
    @Test
    void shouldRejectNullAdministrator() {
        InMemoryRefundRepository repository =
                new InMemoryRefundRepository();

        ProcessRefundService service =
                new ProcessRefundService(repository);

        Return returnProcess = createReturn();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.process(
                        returnProcess,
                        null
                )
        );

        assertFalse(
                repository.containsReturn(returnProcess)
        );
    }

    /**
     * Verifica que un administrador inactivo
     * no pueda procesar reembolsos.
     */
    @Test
    void shouldRejectInactiveAdministrator() {
        InMemoryRefundRepository repository =
                new InMemoryRefundRepository();

        ProcessRefundService service =
                new ProcessRefundService(repository);

        Return returnProcess = createReturn();

        Administrator administrator =
                createAdministrator(UserStatus.INACTIVE);

        assertThrows(
                IllegalStateException.class,
                () -> service.process(
                        returnProcess,
                        administrator
                )
        );

        assertTrue(repository.getRefunds().isEmpty());
    }

    /**
     * Verifica que un administrador bloqueado
     * no pueda procesar reembolsos.
     */
    @Test
    void shouldRejectBlockedAdministrator() {
        InMemoryRefundRepository repository =
                new InMemoryRefundRepository();

        ProcessRefundService service =
                new ProcessRefundService(repository);

        Return returnProcess = createReturn();

        Administrator administrator =
                createAdministrator(UserStatus.BLOCKED);

        assertThrows(
                IllegalStateException.class,
                () -> service.process(
                        returnProcess,
                        administrator
                )
        );

        assertTrue(repository.getRefunds().isEmpty());
    }

    /**
     * Crea una devolución válida a partir
     * de un pedido entregado.
     */
    private Return createReturn() {
        Buyer buyer = new Buyer(
                "1001",
                "Test Buyer",
                "buyer@email.com",
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

    /**
     * Crea un administrador con el estado indicado.
     */
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

    /**
     * Repositorio de reembolsos utilizado durante las pruebas.
     */
    private static class InMemoryRefundRepository
            implements RefundRepository {

        private final List<Refund> refunds =
                new ArrayList<>();

        @Override
        public void save(Refund refund) {
            refunds.add(refund);
        }

        boolean contains(Refund refund) {
            return refunds.contains(refund);
        }

        boolean containsReturn(Return returnProcess) {
            for (Refund refund : refunds) {
                if (refund.belongsTo(returnProcess)) {
                    return true;
                }
            }

            return false;
        }

        List<Refund> getRefunds() {
            return List.copyOf(refunds);
        }
    }
}