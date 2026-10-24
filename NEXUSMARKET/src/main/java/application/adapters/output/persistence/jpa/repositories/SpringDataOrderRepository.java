package application.adapters.output.persistence.jpa.repositories;

import application.adapters.output.persistence.jpa.entities.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data utilizado para ejecutar
 * operaciones relacionales sobre pedidos.
 */
public interface SpringDataOrderRepository
        extends JpaRepository<OrderJpaEntity, Long> {

    /**
     * Busca un pedido por su identificador de dominio.
     *
     * @param orderId identificador del pedido
     * @return entidad encontrada o resultado vacío
     */
    Optional<OrderJpaEntity> findByOrderId(
            String orderId
    );

    /**
     * Obtiene los pedidos pertenecientes a un comprador.
     *
     * @param buyerIdentification identificación del comprador
     * @return entidades encontradas
     */
    List<OrderJpaEntity> findAllByBuyerIdentification(
            String buyerIdentification
    );
}