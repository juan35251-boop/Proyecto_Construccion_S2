package application.domain.models;

import application.domain.valueobjects.InventoryCondition;

/**
 * Representa las existencias de un producto dentro de una bodega.
 *
 * Cada inventario relaciona un {@link Product} con un {@link Warehouse}
 * y controla su cantidad disponible y su condición actual.
 *
 * El producto y la bodega no pueden reemplazarse después de crear el
 * inventario, pero la cantidad disponible y la condición sí pueden cambiar.
 */
public class Inventory {

    /**
     * Producto cuyas existencias se administran.
     */
    private final Product product;

    /**
     * Bodega en la que se encuentra almacenado el producto.
     */
    private final Warehouse warehouse;

    /**
     * Cantidad del producto que se encuentra disponible.
     *
     * Su valor nunca puede ser negativo.
     */
    private int availableQuantity;

    /**
     * Condición actual del inventario.
     */
    private InventoryCondition condition;

    /**
     * Crea un inventario asociado a un producto y una bodega.
     *
     * Todos los datos son validados antes de inicializar el inventario.
     * La cantidad inicial puede ser cero, pero no puede ser negativa.
     *
     * @param product producto asociado al inventario
     * @param warehouse bodega donde se almacena el producto
     * @param availableQuantity cantidad disponible inicialmente
     * @param condition condición inicial del inventario
     *
     * @throws IllegalArgumentException si el producto es nulo
     * @throws IllegalArgumentException si la bodega es nula
     * @throws IllegalArgumentException si la cantidad inicial es negativa
     * @throws IllegalArgumentException si la condición es nula
     */
    public Inventory(
            Product product,
            Warehouse warehouse,
            int availableQuantity,
            InventoryCondition condition
    ) {
        validateProduct(product);
        validateWarehouse(warehouse);
        validateNonNegativeQuantity(availableQuantity);
        validateCondition(condition);

        this.product = product;
        this.warehouse = warehouse;
        this.availableQuantity = availableQuantity;
        this.condition = condition;
    }

    /**
     * Obtiene el producto asociado al inventario.
     *
     * @return el producto almacenado
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Obtiene la bodega asociada al inventario.
     *
     * @return la bodega donde se encuentra el producto
     */
    public Warehouse getWarehouse() {
        return warehouse;
    }

    /**
     * Obtiene la cantidad actualmente disponible.
     *
     * @return la cantidad disponible del producto
     */
    public int getAvailableQuantity() {
        return availableQuantity;
    }

    /**
     * Obtiene la condición actual del inventario.
     *
     * @return la condición del inventario
     */
    public InventoryCondition getCondition() {
        return condition;
    }

    /**
     * Aumenta la cantidad disponible del producto.
     *
     * La cantidad recibida se suma a las existencias actuales.
     *
     * @param quantity cantidad que se desea agregar
     *
     * @throws IllegalArgumentException si la cantidad es menor
     *                                  o igual a cero
     */
    public void addQuantity(int quantity) {
        validatePositiveQuantity(quantity);
        availableQuantity += quantity;
    }

    /**
     * Reserva una cantidad del producto.
     *
     * Una reserva reduce la cantidad disponible. No se permite reservar
     * inventario dañado ni solicitar una cantidad superior a las
     * existencias disponibles.
     *
     * @param quantity cantidad que se desea reservar
     *
     * @throws IllegalArgumentException si la cantidad es menor
     *                                  o igual a cero
     * @throws IllegalStateException si el inventario está dañado
     * @throws IllegalStateException si no existe cantidad suficiente
     */
    public void reserveQuantity(int quantity) {
        validatePositiveQuantity(quantity);

        if (condition == InventoryCondition.DAMAGED) {
            throw new IllegalStateException(
                    "Damaged inventory cannot be reserved."
            );
        }

        if (quantity > availableQuantity) {
            throw new IllegalStateException(
                    "Insufficient available inventory."
            );
        }

        availableQuantity -= quantity;
    }

    /**
     * Reemplaza la cantidad disponible por un nuevo valor.
     *
     * Este método puede utilizarse para corregir o ajustar las existencias
     * después de una verificación del inventario.
     *
     * @param newQuantity nueva cantidad disponible
     *
     * @throws IllegalArgumentException si la nueva cantidad es negativa
     */
    public void adjustQuantity(int newQuantity) {
        validateNonNegativeQuantity(newQuantity);
        availableQuantity = newQuantity;
    }

    /**
     * Cambia la condición actual del inventario.
     *
     * @param newCondition nueva condición del inventario
     *
     * @throws IllegalArgumentException si la nueva condición es nula
     */
    public void changeCondition(InventoryCondition newCondition) {
        validateCondition(newCondition);
        this.condition = newCondition;
    }

    /**
     * Valida que el inventario esté relacionado con un producto.
     *
     * @param product producto que se desea validar
     *
     * @throws IllegalArgumentException si el producto es nulo
     */
    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Inventory must be associated with a product."
            );
        }
    }

    /**
     * Valida que el inventario esté relacionado con una bodega.
     *
     * @param warehouse bodega que se desea validar
     *
     * @throws IllegalArgumentException si la bodega es nula
     */
    private void validateWarehouse(Warehouse warehouse) {
        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "Inventory must be associated with a warehouse."
            );
        }
    }

    /**
     * Valida que una cantidad sea mayor que cero.
     *
     * Esta validación se utiliza en operaciones que aumentan o reservan
     * existencias.
     *
     * @param quantity cantidad que se desea validar
     *
     * @throws IllegalArgumentException si la cantidad es menor
     *                                  o igual a cero
     */
    private void validatePositiveQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }
    }

    /**
     * Valida que una cantidad no sea negativa.
     *
     * El valor cero es válido porque un producto puede quedar sin
     * existencias disponibles.
     *
     * @param quantity cantidad que se desea validar
     *
     * @throws IllegalArgumentException si la cantidad es negativa
     */
    private void validateNonNegativeQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException(
                    "Inventory quantity must not be negative."
            );
        }
    }

    /**
     * Valida que la condición del inventario no sea nula.
     *
     * @param condition condición que se desea validar
     *
     * @throws IllegalArgumentException si la condición es nula
     */
    private void validateCondition(InventoryCondition condition) {
        if (condition == null) {
            throw new IllegalArgumentException(
                    "Inventory condition must not be null."
            );
        }
    }
}