package application.adapters.output.persistence.jpa.entities;

import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @Column(
            name = "identification",
            nullable = false,
            length = 30
    )
    private String identification;

    @Column(
            name = "full_name",
            nullable = false,
            length = 150
    )
    private String fullName;

    @Column(
            name = "email",
            nullable = false,
            unique = true,
            length = 150
    )
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "user_status",
            nullable = false,
            length = 30
    )
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "role",
            nullable = false,
            length = 40
    )
    private SystemRole role;

    @Column(
            name = "primary_address",
            length = 200
    )
    private String primaryAddress;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "buyer_status",
            length = 30
    )
    private BuyerStatus buyerStatus;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> additionalAddresses =
            new ArrayList<>();

    protected UserJpaEntity() {
        // Constructor requerido por JPA.
    }

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
                new ArrayList<>(additionalAddresses);
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
        return List.copyOf(additionalAddresses);
    }
}