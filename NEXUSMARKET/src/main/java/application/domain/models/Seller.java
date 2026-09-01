package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a un vendedor dentro de NexusMarket.
 *
 * Un vendedor es un usuario que puede registrar productos y administrar
 * bodegas pertenecientes a vendedores.
 *
 * Todo vendedor debe crearse con al menos una bodega válida. Hereda de
 * {@link User} su información personal y su estado general en el sistema.
 */
public class Seller extends User {

    /**
     * Productos registrados y administrados por el vendedor.
     */
    private final List<Product> products;

    /**
     * Bodegas asociadas al vendedor.
     *
     * La lista contiene al menos una bodega desde el momento en que se
     * crea el vendedor.
     */
    private final List<Warehouse> warehouses;

    /**
     * Crea un vendedor con su información personal y su primera bodega.
     *
     * Los datos comunes se inicializan mediante el constructor de
     * {@link User}. La primera bodega debe existir y estar clasificada
     * como una bodega perteneciente a un vendedor.
     *
     * @param identification identificación del vendedor
     * @param fullName nombre completo del vendedor
     * @param email correo electrónico del vendedor
     * @param status estado actual del vendedor dentro del sistema
     * @param firstWarehouse primera bodega asociada al vendedor
     *
     * @throws IllegalArgumentException si la primera bodega es nula
     * @throws IllegalArgumentException si la bodega no pertenece
     *                                  al tipo de bodegas de vendedor
     */
    public Seller(
            String identification,
            String fullName,
            String email,
            UserStatus status,
            Warehouse firstWarehouse
    ) {
        super(identification, fullName, email, status);

        validateSellerWarehouse(firstWarehouse);

        this.products = new ArrayList<>();
        this.warehouses = new ArrayList<>();
        this.warehouses.add(firstWarehouse);
    }

    /**
     * Obtiene el rol correspondiente al vendedor.
     *
     * Este método garantiza que todos los objetos de esta clase tengan
     * siempre el rol {@link SystemRole#SELLER}.
     *
     * @return el rol de vendedor
     */
    @Override
    public SystemRole getRole() {
        return SystemRole.SELLER;
    }

    /**
     * Obtiene una copia no modificable de los productos administrados.
     *
     * @return una copia de los productos del vendedor
     */
    public List<Product> getProducts() {
        return List.copyOf(products);
    }

    /**
     * Obtiene una copia no modificable de las bodegas asociadas.
     *
     * @return una copia de las bodegas del vendedor
     */
    public List<Warehouse> getWarehouses() {
        return List.copyOf(warehouses);
    }

    /**
     * Registra un producto para que sea administrado por el vendedor.
     *
     * @param product producto que se desea registrar
     *
     * @throws IllegalArgumentException si el producto es nulo
     */
    public void registerProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Product must not be null."
            );
        }

        products.add(product);
    }

    /**
     * Asocia una nueva bodega al vendedor.
     *
     * La bodega debe estar clasificada como una bodega perteneciente
     * a un vendedor.
     *
     * @param warehouse bodega que se desea asociar
     *
     * @throws IllegalArgumentException si la bodega es nula
     * @throws IllegalArgumentException si no es una bodega de vendedor
     */
    public void associateWarehouse(Warehouse warehouse) {
        validateSellerWarehouse(warehouse);
        warehouses.add(warehouse);
    }

    /**
     * Determina si el vendedor administra un producto.
     *
     * @param product producto que se desea consultar
     * @return {@code true} si el producto está registrado en la lista
     *         del vendedor; de lo contrario, {@code false}
     */
    public boolean managesProduct(Product product) {
        return products.contains(product);
    }

    /**
     * Determina si el vendedor administra una bodega.
     *
     * @param warehouse bodega que se desea consultar
     * @return {@code true} si la bodega está asociada al vendedor;
     *         de lo contrario, {@code false}
     */
    public boolean managesWarehouse(Warehouse warehouse) {
        return warehouses.contains(warehouse);
    }

    /**
     * Valida que una bodega pueda asociarse a un vendedor.
     *
     * La bodega debe existir y su tipo de propietario debe corresponder
     * a un vendedor.
     *
     * @param warehouse bodega que se desea validar
     *
     * @throws IllegalArgumentException si la bodega es nula
     * @throws IllegalArgumentException si no es una bodega de vendedor
     */
    private void validateSellerWarehouse(Warehouse warehouse) {
        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "Seller warehouse must not be null."
            );
        }

        if (!warehouse.isSellerWarehouse()) {
            throw new IllegalArgumentException(
                    "A seller can only be associated with seller warehouses."
            );
        }
    }
}