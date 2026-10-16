package application.adapters.output.persistence.jpa.entities;

import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private List<String> additionalAddresses =
            new ArrayList<>();

    /*
     * Se utiliza Set para evitar que Hibernate intente
     * recuperar dos colecciones tipo List simultáneamente.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<WarehouseOwnerType> warehouseOwnerTypes =
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

        this.warehouseOwnerTypes =
                new HashSet<>();
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
            List<WarehouseOwnerType> warehouseOwnerTypes
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

        this.warehouseOwnerTypes =
                warehouseOwnerTypes == null
                        ? new HashSet<>()
                        : new HashSet<>(
                                warehouseOwnerTypes
                        );
    }

    /**
     * Constructor para administradores y otros usuarios
     * que solamente necesitan los datos generales.
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
        this.warehouseOwnerTypes =
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

    public List<WarehouseOwnerType> getWarehouseOwnerTypes() {
        if (warehouseOwnerTypes == null) {
            return List.of();
        }

        return List.copyOf(warehouseOwnerTypes);
    }
}