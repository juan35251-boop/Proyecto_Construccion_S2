package application.adapters.output.persistence.jpa.entities;

import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entidad JPA utilizada para almacenar usuarios.
 *
 * Cuando el usuario es un vendedor, se conservan los
 * identificadores reales de sus bodegas. Esto permite
 * reconstruirlas sin generar identificadores nuevos.
 */
@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @Column(
            name = "identification",
            nullable = false,
            updatable = false
    )
    private String identification;

    @Column(
            name = "full_name",
            nullable = false
    )
    private String fullName;

    @Column(
            name = "email",
            nullable = false,
            unique = true
    )
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "user_status",
            nullable = false
    )
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "role",
            nullable = false
    )
    private SystemRole role;

    @Column(name = "primary_address")
    private String primaryAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "buyer_status")
    private BuyerStatus buyerStatus;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_additional_addresses",
            joinColumns = @JoinColumn(
                    name = "user_identification"
            )
    )
    @Column(name = "address")
    private List<String> additionalAddresses =
            new ArrayList<>();

    /**
     * Identificadores permanentes de las bodegas
     * administradas por un vendedor.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "seller_warehouse_ids",
            joinColumns = @JoinColumn(
                    name = "seller_identification"
            )
    )
    @Column(name = "warehouse_id")
    private Set<String> warehouseIds =
            new HashSet<>();

    protected UserJpaEntity() {
        // Constructor requerido por JPA.
    }

    /**
     * Constructor para compradores.
     */
    public UserJpaEntity(
            String identification,
            String fullName,
            String email,
            UserStatus status,
            SystemRole role,
            String primaryAddress,
            BuyerStatus buyerStatus,
            List<String> additionalAddresses
    ) {
        this.identification = identification;
        this.fullName = fullName;
        this.email = email;
        this.status = status;
        this.role = role;
        this.primaryAddress = primaryAddress;
        this.buyerStatus = buyerStatus;

        this.additionalAddresses =
                additionalAddresses == null
                        ? new ArrayList<>()
                        : new ArrayList<>(
                                additionalAddresses
                        );

        this.warehouseIds = new HashSet<>();
    }

    /**
     * Constructor para vendedores.
     */
    public UserJpaEntity(
            String identification,
            String fullName,
            String email,
            UserStatus status,
            SystemRole role,
            Set<String> warehouseIds
    ) {
        this.identification = identification;
        this.fullName = fullName;
        this.email = email;
        this.status = status;
        this.role = role;

        this.primaryAddress = null;
        this.buyerStatus = null;
        this.additionalAddresses =
                new ArrayList<>();

        this.warehouseIds =
                warehouseIds == null
                        ? new HashSet<>()
                        : new HashSet<>(warehouseIds);
    }

    /**
     * Constructor para administradores y usuarios
     * que solamente necesitan información general.
     */
    public UserJpaEntity(
            String identification,
            String fullName,
            String email,
            UserStatus status,
            SystemRole role
    ) {
        this.identification = identification;
        this.fullName = fullName;
        this.email = email;
        this.status = status;
        this.role = role;

        this.primaryAddress = null;
        this.buyerStatus = null;
        this.additionalAddresses =
                new ArrayList<>();
        this.warehouseIds =
                new HashSet<>();
    }

    public String getIdentification() {
        return identification;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public UserStatus getStatus() {
        return status;
    }

    public SystemRole getRole() {
        return role;
    }

    public String getPrimaryAddress() {
        return primaryAddress;
    }

    public BuyerStatus getBuyerStatus() {
        return buyerStatus;
    }

    public List<String> getAdditionalAddresses() {
        if (additionalAddresses == null) {
            return List.of();
        }

        return List.copyOf(additionalAddresses);
    }

    public Set<String> getWarehouseIds() {
        if (warehouseIds == null) {
            return Set.of();
        }

        return Set.copyOf(warehouseIds);
    }
}