package application.adapters.output.persistence.jpa.repositories;

import application.adapters.output.persistence.jpa.entities.CartJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio Spring Data encargado de ejecutar
 * las operaciones relacionales de los carritos.
 */
public interface SpringDataCartRepository
        extends JpaRepository<CartJpaEntity, Long> {

    /**
     * Busca un carrito mediante su identificador
     * de dominio.
     *
     * @param cartId identificador del carrito
     * @return entidad encontrada o resultado vacío
     */
    Optional<CartJpaEntity> findByCartId(
            String cartId
    );

    /**
     * Busca el carrito de un comprador.
     *
     * @param buyerIdentification identificación del comprador
     * @return entidad encontrada o resultado vacío
     */
    Optional<CartJpaEntity> findByBuyerIdentification(
            String buyerIdentification
    );

    /**
     * Comprueba si un comprador ya posee carrito.
     *
     * @param buyerIdentification identificación del comprador
     * @return true si existe
     */
    boolean existsByBuyerIdentification(
            String buyerIdentification
    );
}