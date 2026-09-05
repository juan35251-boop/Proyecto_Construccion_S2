package application.services;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.ports.output.InvoiceRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del servicio encargado de generar facturas.
 */
class GenerateInvoiceServiceTest {

    /**
     * Verifica que pueda generarse y almacenarse una factura
     * para un pedido pagado.
     */
    @Test
    void shouldGenerateInvoiceForPaidOrder() {
        InMemoryInvoiceRepository repository =
                new InMemoryInvoiceRepository();

        GenerateInvoiceService service =
                new GenerateInvoiceService(repository);

        Order order = createOrder();
        order.markAsPaid();

        Invoice invoice = service.generate(order);

        assertEquals(order, invoice.getOrder());
        assertEquals(order.getBuyer(), invoice.getBuyer());
        assertEquals(order.getItems(), invoice.getItems());
        assertTrue(repository.contains(invoice));
    }

    /**
     * Verifica que el repositorio de facturas sea obligatorio.
     */
    @Test
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new GenerateInvoiceService(null)
        );
    }

    /**
     * Verifica que no pueda generarse una factura sin pedido.
     */
    @Test
    void shouldRejectNullOrder() {
        InMemoryInvoiceRepository repository =
                new InMemoryInvoiceRepository();

        GenerateInvoiceService service =
                new GenerateInvoiceService(repository);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.generate(null)
        );

        assertTrue(repository.getInvoices().isEmpty());
    }

    /**
     * Verifica que no se genere una factura mientras
     * el pedido continúe pendiente de pago.
     */
    @Test
    void shouldRejectOrderPendingPayment() {
        InMemoryInvoiceRepository repository =
                new InMemoryInvoiceRepository();

        GenerateInvoiceService service =
                new GenerateInvoiceService(repository);

        Order order = createOrder();

        assertThrows(
                IllegalStateException.class,
                () -> service.generate(order)
        );

        assertFalse(
                repository.containsOrder(order)
        );
    }

    /**
     * Crea un pedido pendiente de pago para las pruebas.
     */
    private Order createOrder() {
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

        return new Order(cart);
    }

    /**
     * Repositorio de facturas utilizado durante las pruebas.
     */
    private static class InMemoryInvoiceRepository
            implements InvoiceRepository {

        private final List<Invoice> invoices =
                new ArrayList<>();

        @Override
        public void save(Invoice invoice) {
            invoices.add(invoice);
        }

        boolean contains(Invoice invoice) {
            return invoices.contains(invoice);
        }

        boolean containsOrder(Order order) {
            for (Invoice invoice : invoices) {
                if (invoice.belongsTo(order)) {
                    return true;
                }
            }

            return false;
        }

        List<Invoice> getInvoices() {
            return List.copyOf(invoices);
        }
    }
}