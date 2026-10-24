package application.domain.models;

import application.domain.valueobjects.InventoryCondition;

import java.util.Objects;
import java.util.UUID;

/**
 * Representa las existencias de un producto dentro de una bodega.
 *
 * Cada inventario relaciona un {@link Product} con un {@link Warehouse}
 * y controla su cantidad disponible y su condición actual.
 *
 * El producto y la bodega no pueden reemplazarse después de crear el
 * inventario, pero la cantidad disponible y la condición sí pueden cambiar.
 *
 * Cada inventario posee un identificador permanente que permite
 * almacenarlo en MySQL y relacionarlo con su historial de movimientos
 * guardado en MongoDB.
 */
public class Inventory {

    /**
     * Identificador único y permanente del inventario.
     */
    private final String inventoryId;

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
     * Crea un inventario nuevo y genera automáticamente
     * su identificador permanente.
     *
     * Este constructor se conserva para mantener compatibilidad
     * con los servicios y las pruebas existentes.
     *
     * @param product producto asociado al inventario
     * @param warehouse bodega donde se almacena el producto
     * @param availableQuantity cantidad disponible inicialmente
     * @param condition condición inicial del inventario
     */
    public Inventory(
            Product product,
            Warehouse warehouse,
            int availableQuantity,
            InventoryCondition condition
    ) {
        this(
                UUID.randomUUID().toString(),
                product,
                warehouse,
                availableQuantity,
                condition
        );
    }

    /**
     * Reconstruye un inventario utilizando un identificador existente.
     *
     * Este constructor será utilizado por el adaptador JPA cuando
     * recupere un inventario almacenado en MySQL.
     *
     * @param inventoryId identificador permanente del inventario
     * @param product producto asociado al inventario
     * @param warehouse bodega donde se almacena el producto
     * @param availableQuantity cantidad disponible
     * @param condition condición del inventario
     */
    public Inventory(
            String inventoryId,
            Product product,
            Warehouse warehouse,
            int availableQuantity,
            InventoryCondition condition
    ) {
        validateInventoryId(inventoryId);
        validateProduct(product);
        validateWarehouse(warehouse);
        validateNonNegativeQuantity(availableQuantity);
        validateCondition(condition);

        this.inventoryId = inventoryId;
        this.product = product;
        this.warehouse = warehouse;
        this.availableQuantity = availableQuantity;
        this.condition = condition;
    }

    /**
     * Obtiene el identificador permanente del inventario.
     *
     * @return identificador del inventario
     */
    public String getInventoryId() {
        return inventoryId;
    }

    /**
     * Obtiene el producto asociado al inventario.
     *
     * @return producto almacenado
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Obtiene la bodega asociada al inventario.
     *
     * @return bodega donde se encuentra el producto
     */
    public Warehouse getWarehouse() {
        return warehouse;
    }

    /**
     * Obtiene la cantidad actualmente disponible.
     *
     * @return cantidad disponible del producto
     */
    public int getAvailableQuantity() {
        return availableQuantity;
    }

    /**
     * Obtiene la condición actual del inventario.
     *
     * @return condición del inventario
     */
    public InventoryCondition getCondition() {
        return condition;
    }

    /**
     * Aumenta la cantidad disponible del producto.
     *
     * @param quantity cantidad que se desea agregar
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
     * @param newQuantity nueva cantidad disponible
     */
    public void adjustQuantity(int newQuantity) {
        validateNonNegativeQuantity(newQuantity);
        availableQuantity = newQuantity;
    }

    /**
     * Cambia la condición actual del inventario.
     *
     * @param newCondition nueva condición del inventario
     */
    public void changeCondition(
            InventoryCondition newCondition
    ) {
        validateCondition(newCondition);
        this.condition = newCondition;
    }

    /**
     * Comprueba si este inventario representa
     * el inventario indicado.
     *
     * @param inventory inventario que será comparado
     * @return true si ambos tienen el mismo identificador
     */
    public boolean isSameInventory(
            Inventory inventory
    ) {
        return inventory != null
                && inventoryId.equals(
                        inventory.inventoryId
                );
    }

    /**
     * Valida el identificador permanente.
     *
     * @param inventoryId identificador que será validado
     */
    private void validateInventoryId(
            String inventoryId
    ) {
        if (inventoryId == null
                || inventoryId.isBlank()) {
            throw new IllegalArgumentException(
                    "Inventory identifier must not be empty."
            );
        }
    }

    /**
     * Valida que el inventario esté relacionado
     * con un producto.
     *
     * @param product producto que se desea validar
     */
    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Inventory must be associated with a product."
            );
        }
    }

    /**
     * Valida que el inventario esté relacionado
     * con una bodega.
     *
     * @param warehouse bodega que se desea validar
     */
    private void validateWarehouse(
            Warehouse warehouse
    ) {
        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "Inventory must be associated with a warehouse."
            );
        }
    }

    /**
     * Valida que una cantidad sea mayor que cero.
     *
     * @param quantity cantidad que se desea validar
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
     * @param quantity cantidad que se desea validar
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
     */
    private void validateCondition(
            InventoryCondition condition
    ) {
        if (condition == null) {
            throw new IllegalArgumentException(
                    "Inventory condition must not be null."
            );
        }
    }

    /**
     * Compara inventarios mediante su identificador permanente.
     *
     * @param object objeto que será comparado
     * @return true si representa el mismo inventario
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Inventory inventory)) {
            return false;
        }

        return inventoryId.equals(
                inventory.inventoryId
        );
    }

    /**
     * Genera el código hash utilizando inventoryId.
     *
     * @return código hash del inventario
     */
    @Override
    public int hashCode() {
        return Objects.hash(inventoryId);
    }
}