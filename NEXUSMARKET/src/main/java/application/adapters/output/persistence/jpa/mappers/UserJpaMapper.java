package application.adapters.output.persistence.jpa.mappers;

import application.adapters.output.persistence.jpa.entities.UserJpaEntity;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;

public final class UserJpaMapper {

    private UserJpaMapper() {
        // Impide crear objetos de esta clase.
    }

    public static UserJpaEntity toEntity(User user) {
        if (!(user instanceof Buyer buyer)) {
            throw new IllegalArgumentException(
                    "Unsupported user type: "
                            + user.getClass().getSimpleName()
            );
        }

        return new UserJpaEntity(
                buyer.getIdentification(),
                buyer.getFullName(),
                buyer.getEmail(),
                buyer.getStatus(),
                buyer.getRole(),
                buyer.getPrimaryAddress(),
                buyer.getCommercialStatus(),
                buyer.getAdditionalAddresses()
        );
    }

    public static User toDomain(UserJpaEntity entity) {
        if (entity.getRole() != SystemRole.BUYER) {
            throw new IllegalArgumentException(
                    "Unsupported user role: "
                            + entity.getRole()
            );
        }

        Buyer buyer = new Buyer(
                entity.getIdentification(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getStatus(),
                entity.getPrimaryAddress(),
                entity.getBuyerStatus()
        );

        entity.getAdditionalAddresses()
                .forEach(buyer::addAdditionalAddress);

        return buyer;
    }
}