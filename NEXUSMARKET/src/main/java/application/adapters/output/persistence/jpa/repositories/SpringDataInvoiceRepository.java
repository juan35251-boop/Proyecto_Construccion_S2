package application.adapters.output.persistence.jpa.repositories;

import application.adapters.output.persistence.jpa.entities.InvoiceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio técnico de Spring Data JPA
 * utilizado para almacenar facturas.
 */
public interface SpringDataInvoiceRepository
        extends JpaRepository<
                InvoiceJpaEntity,
                Long
        > {

    /**
     * Busca una factura mediante su identificador.
     *
     * @param invoiceId identificador de la factura
     * @return entidad encontrada o resultado vacío
     */
    Optional<InvoiceJpaEntity> findByInvoiceId(
            String invoiceId
    );

    /**
     * Busca la factura perteneciente a un pedido.
     *
     * @param orderId identificador funcional del pedido
     * @return entidad encontrada o resultado vacío
     */
    Optional<InvoiceJpaEntity> findByOrder_OrderId(
            String orderId
    );

    /**
     * Comprueba si un pedido ya tiene factura.
     *
     * @param orderId identificador funcional del pedido
     * @return true si existe una factura
     */
    boolean existsByOrder_OrderId(
            String orderId
    );
}