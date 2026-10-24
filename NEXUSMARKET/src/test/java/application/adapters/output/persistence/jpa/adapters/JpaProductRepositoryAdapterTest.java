package application.adapters.output.persistence.jpa.adapters;

import application.adapters.output.persistence.jpa.entities.ProductJpaEntity;
import application.adapters.output.persistence.jpa.repositories.SpringDataProductRepository;
import application.domain.models.Product;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas del adaptador JPA para productos.
 */
class JpaProductRepositoryAdapterTest {

    private SpringDataProductRepository
            springDataRepository;

    private JpaProductRepositoryAdapter adapter;

    /**
     * Prepara el repositorio simulado y el adaptador
     * antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        springDataRepository =
                mock(SpringDataProductRepository.class);

        adapter =
                new JpaProductRepositoryAdapter(
                        springDataRepository
                );
    }

    /**
     * Comprueba que el producto sea convertido
     * y enviado al repositorio JPA.
     */
    @Test
    void shouldSaveProduct() {
        Product product =
                new Product(
                        "PRODUCT-001",
                        ProductType.PHYSICAL,
                        ProductStatus.PUBLISHED
                );

        product.addVariant(
                new ProductVariant("Talla M")
        );

        adapter.save(product);

        ArgumentCaptor<ProductJpaEntity> captor =
                ArgumentCaptor.forClass(
                        ProductJpaEntity.class
                );

        verify(springDataRepository)
                .save(captor.capture());

        ProductJpaEntity savedEntity =
                captor.getValue();

        assertEquals(
                "PRODUCT-001",
                savedEntity.getProductId()
        );

        assertEquals(
                ProductType.PHYSICAL,
                savedEntity.getProductType()
        );

        assertEquals(
                ProductStatus.PUBLISHED,
                savedEntity.getStatus()
        );

        assertEquals(
                List.of("Talla M"),
                savedEntity.getVariants()
        );
    }

    /**
     * Comprueba que todas las entidades sean
     * convertidas en productos del dominio.
     */
    @Test
    void shouldFindAllProducts() {
        ProductJpaEntity physicalProduct =
                new ProductJpaEntity(
                        "PRODUCT-001",
                        ProductType.PHYSICAL,
                        ProductStatus.PUBLISHED,
                        List.of("Color negro")
                );

        ProductJpaEntity digitalProduct =
                new ProductJpaEntity(
                        "PRODUCT-002",
                        ProductType.DIGITAL,
                        ProductStatus.SUSPENDED,
                        List.of()
                );

        when(springDataRepository.findAll())
                .thenReturn(
                        List.of(
                                physicalProduct,
                                digitalProduct
                        )
                );

        List<Product> products =
                adapter.findAll();

        assertEquals(2, products.size());

        assertEquals(
                "PRODUCT-001",
                products.get(0).getProductId()
        );

        assertEquals(
                ProductType.PHYSICAL,
                products.get(0).getProductType()
        );

        assertTrue(
                products.get(0).hasVariant(
                        new ProductVariant(
                                "Color negro"
                        )
                )
        );

        assertEquals(
                "PRODUCT-002",
                products.get(1).getProductId()
        );

        assertEquals(
                ProductType.DIGITAL,
                products.get(1).getProductType()
        );

        verify(springDataRepository).findAll();
    }

    /**
     * Comprueba que una consulta sin productos
     * devuelva una lista vacía.
     */
    @Test
    void shouldReturnEmptyListWhenNoProductsExist() {
        when(springDataRepository.findAll())
                .thenReturn(List.of());

        List<Product> products =
                adapter.findAll();

        assertTrue(products.isEmpty());

        verify(springDataRepository).findAll();
    }

    /**
     * Comprueba que no pueda guardarse
     * un producto nulo.
     */
    @Test
    void shouldRejectNullProductWhenSaving() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> adapter.save(null)
                );

        assertEquals(
                "Product must not be null.",
                exception.getMessage()
        );
    }

    /**
     * Comprueba que el adaptador no pueda crearse
     * sin el repositorio Spring Data.
     */
    @Test
    void shouldRejectNullSpringDataRepository() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new JpaProductRepositoryAdapter(
                                        null
                                )
                );

        assertEquals(
                "Product JPA repository must not be null.",
                exception.getMessage()
        );
    }
}