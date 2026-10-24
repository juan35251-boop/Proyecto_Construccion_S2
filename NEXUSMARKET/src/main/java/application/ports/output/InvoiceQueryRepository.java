package application.ports.output;

import application.domain.models.Invoice;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida utilizado para consultar facturas.
 *
 * Las implementaciones pueden utilizar JPA, memoria
 * u otra tecnología de persistencia.
 */
public interface InvoiceQueryRepository {

    /**
     * Busca una factura mediante su identificador.
     *
     * La implementación predeterminada conserva
     * compatibles los repositorios en memoria.
     *
     * @param invoiceId identificador de la factura
     * @return factura encontrada o resultado vacío
     */
    default Optional<Invoice> findById(
            String invoiceId
    ) {
        if (invoiceId == null
                || invoiceId.isBlank()) {
            return Optional.empty();
        }

        return findAll()
                .stream()
                .filter(invoice ->
                        invoiceId.equals(
                                invoice.getInvoiceId()
                        )
                )
                .findFirst();
    }

    /**
     * Busca la factura perteneciente a un pedido.
     *
     * @param orderId identificador del pedido
     * @return factura encontrada o resultado vacío
     */
    default Optional<Invoice> findByOrderId(
            String orderId
    ) {
        if (orderId == null
                || orderId.isBlank()) {
            return Optional.empty();
        }

        return findAll()
                .stream()
                .filter(invoice ->
                        invoice.getOrder() != null
                                && orderId.equals(
                                        invoice.getOrder()
                                                .getOrderId()
                                )
                )
                .findFirst();
    }

    /**
     * Comprueba si un pedido ya tiene factura.
     *
     * @param orderId identificador del pedido
     * @return true si existe una factura
     */
    default boolean existsByOrderId(
            String orderId
    ) {
        return findByOrderId(orderId)
                .isPresent();
    }

    /**
     * Obtiene todas las facturas registradas.
     *
     * @return lista de facturas
     */
    List<Invoice> findAll();
}