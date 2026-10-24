package application.adapters.output.persistence.jpa.repositories;

import application.adapters.output.persistence.jpa.entities.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA para productos.
 */
public interface SpringDataProductRepository
        extends JpaRepository<
                ProductJpaEntity,
                String
        > {
}