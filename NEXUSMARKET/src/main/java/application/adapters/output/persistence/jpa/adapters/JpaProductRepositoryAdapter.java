package application.adapters.output.persistence.jpa.adapters;

import application.adapters.output.persistence.jpa.entities.ProductJpaEntity;
import application.adapters.output.persistence.jpa.mappers.ProductJpaMapper;
import application.adapters.output.persistence.jpa.repositories.SpringDataProductRepository;
import application.domain.models.Product;
import application.ports.output.ProductQueryRepository;
import application.ports.output.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA encargado de guardar y consultar productos.
 */
@Repository
public class JpaProductRepositoryAdapter
        implements ProductRepository,
        ProductQueryRepository {

    private final SpringDataProductRepository repository;

    public JpaProductRepositoryAdapter(
            SpringDataProductRepository repository
    ) {
        validateRepository(repository);
        this.repository = repository;
    }

    @Override
    public void save(Product product) {
        validateProduct(product);

        ProductJpaEntity entity =
                ProductJpaMapper.toEntity(product);

        repository.save(entity);
    }

    @Override
    public Optional<Product> findById(
            String productId
    ) {
        validateProductId(productId);

        return repository.findById(productId)
                .map(ProductJpaMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return repository.findAll()
                .stream()
                .map(ProductJpaMapper::toDomain)
                .toList();
    }

    private void validateRepository(
            SpringDataProductRepository repository
    ) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Product JPA repository must not be null."
            );
        }
    }

    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Product must not be null."
            );
        }
    }

    private void validateProductId(
            String productId
    ) {
        if (productId == null
                || productId.isBlank()) {
            throw new IllegalArgumentException(
                    "Product identification must not be empty."
            );
        }
    }
}