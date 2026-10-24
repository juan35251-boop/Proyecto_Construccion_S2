package application.domain.models;

import application.domain.valueobjects.WarehouseOwnerType;

import java.util.Objects;
import java.util.UUID;

/**
 * Representa una bodega utilizada para almacenar
 * los productos físicos del Marketplace.
 *
 * Una bodega puede pertenecer directamente al Marketplace
 * o estar asociada a un vendedor.
 *
 * La identidad de la bodega es independiente de su tipo.
 * Por ello, dos bodegas del mismo tipo continúan siendo
 * bodegas diferentes.
 */
public class Warehouse {

    private final String warehouseId;
    private final WarehouseOwnerType ownerType;

    /**
     * Construye una nueva bodega y genera automáticamente
     * un identificador único.
     *
     * Este constructor se utiliza al crear una bodega nueva
     * desde los servicios de aplicación.
     *
     * @param ownerType tipo de propietario de la bodega
     */
    public Warehouse(
            WarehouseOwnerType ownerType
    ) {
        this(
                UUID.randomUUID().toString(),
                ownerType
        );
    }

    /**
     * Reconstruye una bodega utilizando un identificador existente.
     *
     * Este constructor será utilizado principalmente por
     * los adaptadores de persistencia cuando recuperen
     * una bodega almacenada en MySQL.
     *
     * @param warehouseId identificador único de la bodega
     * @param ownerType tipo de propietario de la bodega
     */
    public Warehouse(
            String warehouseId,
            WarehouseOwnerType ownerType
    ) {
        validateWarehouseId(warehouseId);
        validateOwnerType(ownerType);

        this.warehouseId = warehouseId;
        this.ownerType = ownerType;
    }

    /**
     * Obtiene el identificador único de la bodega.
     *
     * @return identificador de la bodega
     */
    public String getWarehouseId() {
        return warehouseId;
    }

    /**
     * Obtiene el tipo de propietario de la bodega.
     *
     * @return tipo de propietario
     */
    public WarehouseOwnerType getOwnerType() {
        return ownerType;
    }

    /**
     * Indica si la bodega pertenece al Marketplace.
     *
     * @return true si pertenece al Marketplace
     */
    public boolean isMarketplaceWarehouse() {
        return ownerType
                == WarehouseOwnerType.MARKETPLACE;
    }

    /**
     * Indica si la bodega pertenece a un vendedor.
     *
     * @return true si pertenece a un vendedor
     */
    public boolean isSellerWarehouse() {
        return ownerType
                == WarehouseOwnerType.SELLER;
    }

    /**
     * Valida que el identificador de la bodega exista
     * y contenga información.
     *
     * @param warehouseId identificador que será validado
     */
    private void validateWarehouseId(
            String warehouseId
    ) {
        if (warehouseId == null
                || warehouseId.isBlank()) {
            throw new IllegalArgumentException(
                    "Warehouse identifier must not be empty."
            );
        }
    }

    /**
     * Valida que se haya indicado el tipo
     * de propietario de la bodega.
     *
     * @param ownerType tipo de propietario
     */
    private void validateOwnerType(
            WarehouseOwnerType ownerType
    ) {
        if (ownerType == null) {
            throw new IllegalArgumentException(
                    "Warehouse owner type must not be null."
            );
        }
    }

    /**
     * Compara bodegas mediante su identificador permanente.
     *
     * Esto permite reconocer la misma bodega después
     * de recuperarla desde la base de datos.
     *
     * @param object objeto que será comparado
     * @return true si representa la misma bodega
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Warehouse warehouse)) {
            return false;
        }

        return warehouseId.equals(
                warehouse.warehouseId
        );
    }

    /**
     * Genera el código hash usando el identificador
     * permanente de la bodega.
     *
     * @return código hash de la bodega
     */
    @Override
    public int hashCode() {
        return Objects.hash(warehouseId);
    }
}