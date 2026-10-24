package application.adapters.output.persistence.jpa.entities;

import application.domain.valueobjects.WarehouseOwnerType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Entidad JPA utilizada para almacenar bodegas en MySQL.
 *
 * Esta clase pertenece exclusivamente al adaptador
 * de persistencia y no forma parte del dominio.
 *
 * El campo id es la clave técnica generada por MySQL,
 * mientras que warehouseId representa la identidad
 * permanente utilizada por el dominio y la API.
 */
@Entity
@Table(name = "warehouses")
public class WarehouseJpaEntity {

    /**
     * Clave primaria técnica administrada por MySQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador permanente de la bodega dentro
     * del dominio de NexusMarket.
     */
    @Column(
            name = "warehouse_id",
            nullable = false,
            unique = true,
            length = 36
    )
    private String warehouseId;

    /**
     * Define si la bodega pertenece al Marketplace
     * o a un vendedor.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "owner_type",
            nullable = false,
            length = 30
    )
    private WarehouseOwnerType ownerType;

    /**
     * Constructor requerido por JPA.
     */
    protected WarehouseJpaEntity() {
    }

    /**
     * Construye una entidad nueva generando automáticamente
     * el identificador permanente de la bodega.
     *
     * Este constructor se conserva para mantener
     * compatibilidad con las pruebas existentes.
     *
     * @param ownerType tipo de propietario de la bodega
     */
    public WarehouseJpaEntity(
            WarehouseOwnerType ownerType
    ) {
        this(
                null,
                UUID.randomUUID().toString(),
                ownerType
        );
    }

    /**
     * Construye una entidad nueva utilizando el identificador
     * permanente recibido desde el dominio.
     *
     * @param warehouseId identificador permanente de la bodega
     * @param ownerType tipo de propietario
     */
    public WarehouseJpaEntity(
            String warehouseId,
            WarehouseOwnerType ownerType
    ) {
        this(
                null,
                warehouseId,
                ownerType
        );
    }

    /**
     * Reconstruye completamente una entidad almacenada.
     *
     * Este constructor puede utilizarse en pruebas
     * y procesos de mapeo.
     *
     * @param id clave técnica de MySQL
     * @param warehouseId identificador permanente del dominio
     * @param ownerType tipo de propietario
     */
    public WarehouseJpaEntity(
            Long id,
            String warehouseId,
            WarehouseOwnerType ownerType
    ) {
        this.id = id;
        this.warehouseId = warehouseId;
        this.ownerType = ownerType;
    }

    /**
     * Obtiene la clave técnica de MySQL.
     *
     * @return clave primaria técnica
     */
    public Long getId() {
        return id;
    }

    /**
     * Modifica la clave técnica.
     *
     * Este método es utilizado principalmente
     * por JPA y por las pruebas del adaptador.
     *
     * @param id nueva clave técnica
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el identificador permanente de la bodega.
     *
     * @return identificador de dominio
     */
    public String getWarehouseId() {
        return warehouseId;
    }

    /**
     * Establece el identificador permanente de la bodega.
     *
     * @param warehouseId identificador de dominio
     */
    public void setWarehouseId(
            String warehouseId
    ) {
        this.warehouseId = warehouseId;
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
     * Modifica el tipo de propietario de la bodega.
     *
     * @param ownerType nuevo tipo de propietario
     */
    public void setOwnerType(
            WarehouseOwnerType ownerType
    ) {
        this.ownerType = ownerType;
    }
}