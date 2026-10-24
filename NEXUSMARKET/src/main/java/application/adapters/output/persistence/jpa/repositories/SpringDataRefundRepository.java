package application.adapters.output.persistence.jpa.repositories;

import application.adapters.output.persistence.jpa.entities.RefundJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio técnico de Spring Data JPA
 * para reembolsos.
 */
public interface SpringDataRefundRepository
        extends JpaRepository<
                RefundJpaEntity,
                Long
        > {

    /**
     * Busca un reembolso mediante su identificador.
     *
     * @param refundId identificador funcional
     * @return entidad encontrada, si existe
     */
    Optional<RefundJpaEntity> findByRefundId(
            String refundId
    );

    /**
     * Busca el reembolso de una devolución.
     *
     * ReturnProcess_ReturnId indica a Spring Data
     * que debe recorrer la relación returnProcess
     * y consultar su campo returnId.
     *
     * @param returnId identificador de la devolución
     * @return entidad encontrada, si existe
     */
    Optional<RefundJpaEntity>
    findByReturnProcess_ReturnId(
            String returnId
    );

    /**
     * Comprueba si una devolución ya fue reembolsada.
     *
     * @param returnId identificador de la devolución
     * @return true si ya existe el reembolso
     */
    boolean existsByReturnProcess_ReturnId(
            String returnId
    );
}