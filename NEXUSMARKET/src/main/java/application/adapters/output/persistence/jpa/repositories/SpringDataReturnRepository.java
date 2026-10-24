package application.adapters.output.persistence.jpa.repositories;

import application.adapters.output.persistence.jpa.entities.ReturnJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio técnico de Spring Data JPA
 * para solicitudes de devolución.
 */
public interface SpringDataReturnRepository
        extends JpaRepository<
                ReturnJpaEntity,
                Long
        > {

    /**
     * Busca una devolución mediante su identificador.
     *
     * @param returnId identificador funcional
     * @return entidad encontrada, si existe
     */
    Optional<ReturnJpaEntity> findByReturnId(
            String returnId
    );

    /**
     * Busca la devolución asociada a una orden.
     *
     * Order_OrderId indica a Spring Data que debe
     * recorrer la relación order y consultar orderId.
     *
     * @param orderId identificador funcional de la orden
     * @return entidad encontrada, si existe
     */
    Optional<ReturnJpaEntity> findByOrder_OrderId(
            String orderId
    );

    /**
     * Comprueba si una orden ya tiene devolución.
     *
     * @param orderId identificador de la orden
     * @return true si ya existe
     */
    boolean existsByOrder_OrderId(
            String orderId
    );
}