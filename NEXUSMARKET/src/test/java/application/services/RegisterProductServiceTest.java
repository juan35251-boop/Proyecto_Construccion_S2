package application.services;

import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.ports.output.ProductRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del servicio encargado de registrar productos.
 *
 * Se utiliza un repositorio en memoria para verificar el comportamiento
 * del servicio sin conectarlo todavía con una base de datos.
 */
class RegisterProductServiceTest {

    /**
     * Verifica que un vendedor activo pueda registrar un producto.
     */
    @Test
    void shouldRegisterProductByActiveSeller() {
        InMemoryProductRepository repository =
                new InMemoryProductRepository();

        RegisterProductService service =
                new RegisterProductService(repository);

        Seller seller = createSeller(UserStatus.ACTIVE);

        Product product = service.register(
                seller,
                ProductType.PHYSICAL,
                ProductStatus.PUBLISHED
        );

        assertEquals(
                ProductType.PHYSICAL,
                product.getProductType()
        );

        assertEquals(
                ProductStatus.PUBLISHED,
                product.getStatus()
        );

        assertTrue(seller.managesProduct(product));
        assertTrue(repository.contains(product));
        assertEquals(1, repository.getProducts().size());
    }

    /**
     * Verifica que el servicio no pueda crearse sin repositorio.
     */
    @Test
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegisterProductService(null)
        );
    }

    /**
     * Verifica que no se pueda registrar un producto sin vendedor.
     */
    @Test
    void shouldRejectNullSeller() {
        RegisterProductService service =
                createService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(
                        null,
                        ProductType.PHYSICAL,
                        ProductStatus.PUBLISHED
                )
        );
    }

    /**
     * Verifica que un vendedor inactivo no pueda registrar productos.
     */
    @Test
    void shouldRejectInactiveSeller() {
        RegisterProductService service =
                createService();

        Seller seller = createSeller(UserStatus.INACTIVE);

        assertThrows(
                IllegalStateException.class,
                () -> service.register(
                        seller,
                        ProductType.PHYSICAL,
                        ProductStatus.PUBLISHED
                )
        );
    }

    /**
     * Verifica que un vendedor bloqueado no pueda registrar productos.
     */
    @Test
    void shouldRejectBlockedSeller() {
        RegisterProductService service =
                createService();

        Seller seller = createSeller(UserStatus.BLOCKED);

        assertThrows(
                IllegalStateException.class,
                () -> service.register(
                        seller,
                        ProductType.DIGITAL,
                        ProductStatus.PUBLISHED
                )
        );
    }

    /**
     * Verifica que el tipo del producto sea obligatorio.
     */
    @Test
    void shouldRejectNullProductType() {
        RegisterProductService service =
                createService();

        Seller seller = createSeller(UserStatus.ACTIVE);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(
                        seller,
                        null,
                        ProductStatus.PUBLISHED
                )
        );
    }

    /**
     * Verifica que el estado inicial del producto sea obligatorio.
     */
    @Test
    void shouldRejectNullProductStatus() {
        RegisterProductService service =
                createService();

        Seller seller = createSeller(UserStatus.ACTIVE);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(
                        seller,
                        ProductType.PHYSICAL,
                        null
                )
        );
    }

    /**
     * Crea un servicio con un repositorio en memoria.
     *
     * @return servicio preparado para las pruebas
     */
    private RegisterProductService createService() {
        return new RegisterProductService(
                new InMemoryProductRepository()
        );
    }

    /**
     * Crea un vendedor con el estado requerido por cada prueba.
     *
     * @param status estado que tendrá el vendedor
     * @return vendedor preparado para la prueba
     */
    private Seller createSeller(UserStatus status) {
        Warehouse warehouse = new Warehouse(
                WarehouseOwnerType.SELLER
        );

        return new Seller(
                "2001",
                "Test Seller",
                "seller@email.com",
                status,
                warehouse
        );
    }

    /**
     * Implementación sencilla del repositorio que guarda los productos
     * en una lista durante las pruebas.
     */
    private static class InMemoryProductRepository
            implements ProductRepository {

        private final List<Product> products =
                new ArrayList<>();

        /**
         * Guarda el producto en la lista en memoria.
         *
         * @param product producto que se desea guardar
         */
        @Override
        public void save(Product product) {
            products.add(product);
        }

        /**
         * Permite comprobar si un producto fue guardado.
         *
         * @param product producto buscado
         * @return true si el producto está almacenado
         */
        boolean contains(Product product) {
            return products.contains(product);
        }

        /**
         * Devuelve una copia inmodificable de los productos guardados.
         *
         * @return productos almacenados durante la prueba
         */
        List<Product> getProducts() {
            return List.copyOf(products);
        }
    }
}