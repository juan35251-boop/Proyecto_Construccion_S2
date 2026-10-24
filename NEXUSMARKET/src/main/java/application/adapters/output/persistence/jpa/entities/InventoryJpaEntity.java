package application.adapters.output.persistence.jpa.entities;

import application.domain.valueobjects.InventoryCondition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entidad JPA utilizada para almacenar el estado
 * actual de un inventario en MySQL.
 *
 * Esta clase no pertenece al dominio. Su única
 * responsabilidad es representar la información
 * en la base de datos relacional.
 *
 * Los movimientos históricos del inventario no se
 * almacenan en esta tabla; serán documentos de MongoDB.
 */
@Entity
@Table(
        name = "inventories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_product_warehouse",
                        columnNames = {
                                "product_id",
                                "warehouse_id"
                        }
                )
        }
)
public class InventoryJpaEntity {

    /**
     * Clave primaria técnica generada por MySQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador permanente utilizado por el dominio.
     */
    @Column(
            name = "inventory_id",
            nullable = false,
            unique = true,
            length = 36
    )
    private String inventoryId;

    /**
     * Identificador permanente del producto relacionado.
     *
     * Se almacena el identificador y no el objeto del dominio,
     * evitando que JPA determine la estructura del dominio.
     */
    @Column(
            name = "product_id",
            nullable = false,
            length = 36
    )
    private String productId;

    /**
     * Identificador permanente de la bodega relacionada.
     */
    @Column(
            name = "warehouse_id",
            nullable = false,
            length = 36
    )
    private String warehouseId;

    /**
     * Cantidad actualmente disponible.
     */
    @Column(
            name = "available_quantity",
            nullable = false
    )
    private int availableQuantity;

    /**
     * Condición actual del inventario.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "inventory_condition",
            nullable = false,
            length = 30
    )
    private InventoryCondition condition;

    /**
     * Constructor requerido por JPA.
     */
    protected InventoryJpaEntity() {
    }

    /**
     * Construye una entidad nueva sin clave técnica.
     *
     * La clave Long será generada posteriormente por MySQL.
     *
     * @param inventoryId identificador del inventario
     * @param productId identificador del producto
     * @param warehouseId identificador de la bodega
     * @param availableQuantity cantidad disponible
     * @param condition condición del inventario
     */
    public InventoryJpaEntity(
            String inventoryId,
            String productId,
            String warehouseId,
            int availableQuantity,
            InventoryCondition condition
    ) {
        this(
                null,
                inventoryId,
                productId,
                warehouseId,
                availableQuantity,
                condition
        );
    }

    /**
     * Reconstruye completamente una entidad almacenada.
     *
     * Este constructor también facilita las pruebas unitarias
     * del mapeador y del adaptador.
     *
     * @param id clave técnica de MySQL
     * @param inventoryId identificador del inventario
     * @param productId identificador del producto
     * @param warehouseId identificador de la bodega
     * @param availableQuantity cantidad disponible
     * @param condition condición actual
     */
    public InventoryJpaEntity(
            Long id,
            String inventoryId,
            String productId,
            String warehouseId,
            int availableQuantity,
            InventoryCondition condition
    ) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.availableQuantity = availableQuantity;
        this.condition = condition;
    }

    /**
     * Obtiene la clave técnica de MySQL.
     *
     * @return clave técnica
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece la clave técnica.
     *
     * @param id clave técnica
     */
    public void setId(Long id) {
        this.id = id;
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
     * Establece el identificador del inventario.
     *
     * @param inventoryId identificador permanente
     */
    public void setInventoryId(
            String inventoryId
    ) {
        this.inventoryId = inventoryId;
    }

    /**
     * Obtiene el identificador del producto.
     *
     * @return identificador del producto
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Establece el identificador del producto.
     *
     * @param productId identificador del producto
     */
    public void setProductId(
            String productId
    ) {
        this.productId = productId;
    }

    /**
     * Obtiene el identificador de la bodega.
     *
     * @return identificador de la bodega
     */
    public String getWarehouseId() {
        return warehouseId;
    }

    /**
     * Establece el identificador de la bodega.
     *
     * @param warehouseId identificador de la bodega
     */
    public void setWarehouseId(
            String warehouseId
    ) {
        this.warehouseId = warehouseId;
    }

    /**
     * Obtiene la cantidad disponible.
     *
     * @return cantidad disponible
     */
    public int getAvailableQuantity() {
        return availableQuantity;
    }

    /**
     * Modifica la cantidad disponible.
     *
     * @param availableQuantity nueva cantidad
     */
    public void setAvailableQuantity(
            int availableQuantity
    ) {
        this.availableQuantity =
                availableQuantity;
    }

    /**
     * Obtiene la condición del inventario.
     *
     * @return condición actual
     */
    public InventoryCondition getCondition() {
        return condition;
    }

    /**
     * Modifica la condición del inventario.
     *
     * @param condition nueva condición
     */
    public void setCondition(
            InventoryCondition condition
    ) {
        this.condition = condition;
    }
}