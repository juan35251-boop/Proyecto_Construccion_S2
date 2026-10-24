package application.adapters.output.persistence.mongodb.documents;

import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.SystemRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Documento utilizado para almacenar en MongoDB
 * el historial de movimientos de inventario.
 *
 * Esta clase pertenece exclusivamente al adaptador
 * de persistencia MongoDB y no forma parte del dominio.
 *
 * Se almacenan identificadores en lugar de objetos JPA
 * para evitar dependencias entre tecnologías.
 */
@Document(collection = "inventory_movements")
public class InventoryMovementMongoDocument {

    /**
     * Identificador permanente del movimiento.
     *
     * También funciona como identificador del documento MongoDB.
     */
    @Id
    private String movementId;

    /**
     * Identificador del inventario afectado.
     */
    @Indexed
    private String inventoryId;

    /**
     * Identificador del producto afectado.
     *
     * Se guarda como dato de trazabilidad histórica.
     */
    @Indexed
    private String productId;

    /**
     * Identificador de la bodega afectada.
     *
     * Se guarda como dato de trazabilidad histórica.
     */
    @Indexed
    private String warehouseId;

    /**
     * Tipo de operación realizada.
     */
    @Indexed
    private InventoryMovementType movementType;

    /**
     * Cantidad de unidades involucradas.
     */
    private int quantity;

    /**
     * Identificación del usuario responsable.
     */
    @Indexed
    private String performedByIdentification;

    /**
     * Rol que tenía el usuario al realizar la operación.
     *
     * Este valor se conserva como evidencia histórica,
     * aunque posteriormente el usuario cambie de estado.
     */
    private SystemRole performedByRole;

    /**
     * Fecha y hora exactas del movimiento.
     */
    @Indexed
    private Instant occurredAt;

    /**
     * Constructor vacío requerido por Spring Data MongoDB.
     */
    protected InventoryMovementMongoDocument() {
    }

    /**
     * Construye un documento completo de movimiento.
     *
     * @param movementId identificador del movimiento
     * @param inventoryId identificador del inventario
     * @param productId identificador del producto
     * @param warehouseId identificador de la bodega
     * @param movementType tipo de movimiento
     * @param quantity cantidad involucrada
     * @param performedByIdentification identificación del usuario
     * @param performedByRole rol del usuario
     * @param occurredAt momento de la operación
     */
    public InventoryMovementMongoDocument(
            String movementId,
            String inventoryId,
            String productId,
            String warehouseId,
            InventoryMovementType movementType,
            int quantity,
            String performedByIdentification,
            SystemRole performedByRole,
            Instant occurredAt
    ) {
        this.movementId = movementId;
        this.inventoryId = inventoryId;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.performedByIdentification =
                performedByIdentification;
        this.performedByRole = performedByRole;
        this.occurredAt = occurredAt;
    }

    public String getMovementId() {
        return movementId;
    }

    public void setMovementId(
            String movementId
    ) {
        this.movementId = movementId;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(
            String inventoryId
    ) {
        this.inventoryId = inventoryId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(
            String productId
    ) {
        this.productId = productId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(
            String warehouseId
    ) {
        this.warehouseId = warehouseId;
    }

    public InventoryMovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(
            InventoryMovementType movementType
    ) {
        this.movementType = movementType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPerformedByIdentification() {
        return performedByIdentification;
    }

    public void setPerformedByIdentification(
            String performedByIdentification
    ) {
        this.performedByIdentification =
                performedByIdentification;
    }

    public SystemRole getPerformedByRole() {
        return performedByRole;
    }

    public void setPerformedByRole(
            SystemRole performedByRole
    ) {
        this.performedByRole = performedByRole;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(
            Instant occurredAt
    ) {
        this.occurredAt = occurredAt;
    }
}