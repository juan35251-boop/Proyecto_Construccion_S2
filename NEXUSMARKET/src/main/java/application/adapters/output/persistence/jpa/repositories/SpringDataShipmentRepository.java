package application.adapters.output.persistence.jpa.repositories;

import application.adapters.output.persistence.jpa.entities.ShipmentJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data utilizado para acceder
 * a los envíos almacenados en MySQL.
 */
public interface SpringDataShipmentRepository
        extends JpaRepository<ShipmentJpaEntity, Long> {

    /**
     * Busca un envío por su identificador funcional.
     *
     * EntityGraph solicita cargar también el pedido asociado,
     * necesario para reconstruir el dominio.
     *
     * @param shipmentId identificador del envío
     * @return entidad encontrada o vacío
     */
    @EntityGraph(attributePaths = "order")
    Optional<ShipmentJpaEntity> findByShipmentId(
            String shipmentId
    );

    /**
     * Busca un envío utilizando el identificador
     * funcional del pedido asociado.
     *
     * @param orderId identificador del pedido
     * @return entidad encontrada o vacío
     */
    @EntityGraph(attributePaths = "order")
    Optional<ShipmentJpaEntity> findByOrder_OrderId(
            String orderId
    );

    /**
     * Comprueba si existe un envío para un pedido.
     *
     * @param orderId identificador del pedido
     * @return true si ya existe un envío
     */
    boolean existsByOrder_OrderId(
            String orderId
    );

    /**
     * Obtiene los envíos junto con sus pedidos.
     *
     * @return entidades de envío
     */
    @Override
    @EntityGraph(attributePaths = "order")
    List<ShipmentJpaEntity> findAll();
}