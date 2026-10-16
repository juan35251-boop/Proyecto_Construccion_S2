package application.adapters.output.persistence.jpa.mappers;

import application.adapters.output.persistence.jpa.entities.UserJpaEntity;
import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.WarehouseOwnerType;

import java.util.List;

public final class UserJpaMapper {

    private UserJpaMapper() {
        // Impide crear objetos de esta clase.
    }

    public static UserJpaEntity toEntity(User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "User must not be null."
            );
        }

        if (user instanceof Buyer buyer) {
            return buyerToEntity(buyer);
        }

        if (user instanceof Seller seller) {
            return sellerToEntity(seller);
        }

        if (user instanceof Administrator administrator) {
            return administratorToEntity(
                    administrator
            );
        }

        throw new IllegalArgumentException(
                "Unsupported user type: "
                        + user.getClass().getSimpleName()
        );
    }

    private static UserJpaEntity buyerToEntity(
            Buyer buyer
    ) {
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

    private static UserJpaEntity sellerToEntity(
            Seller seller
    ) {
        List<WarehouseOwnerType> warehouseOwnerTypes =
                seller.getWarehouses()
                        .stream()
                        .map(Warehouse::getOwnerType)
                        .toList();

        return new UserJpaEntity(
                seller.getIdentification(),
                seller.getFullName(),
                seller.getEmail(),
                seller.getStatus(),
                seller.getRole(),
                warehouseOwnerTypes
        );
    }

    private static UserJpaEntity administratorToEntity(
            Administrator administrator
    ) {
        return new UserJpaEntity(
                administrator.getIdentification(),
                administrator.getFullName(),
                administrator.getEmail(),
                administrator.getStatus(),
                administrator.getRole()
        );
    }

    public static User toDomain(
            UserJpaEntity entity
    ) {
        if (entity == null) {
            throw new IllegalArgumentException(
                    "User entity must not be null."
            );
        }

        if (entity.getRole() == SystemRole.BUYER) {
            return buyerToDomain(entity);
        }

        if (entity.getRole() == SystemRole.SELLER) {
            return sellerToDomain(entity);
        }

        if (entity.getRole()
                == SystemRole.ADMINISTRATOR) {
            return administratorToDomain(entity);
        }

        throw new IllegalArgumentException(
                "Unsupported user role: "
                        + entity.getRole()
        );
    }

    private static Buyer buyerToDomain(
            UserJpaEntity entity
    ) {
        Buyer buyer = new Buyer(
                entity.getIdentification(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getStatus(),
                entity.getPrimaryAddress(),
                entity.getBuyerStatus()
        );

        entity.getAdditionalAddresses()
                .forEach(
                        buyer::addAdditionalAddress
                );

        return buyer;
    }

    private static Seller sellerToDomain(
            UserJpaEntity entity
    ) {
        List<WarehouseOwnerType> ownerTypes =
                entity.getWarehouseOwnerTypes();

        if (ownerTypes.isEmpty()) {
            throw new IllegalStateException(
                    "A persisted seller must have at least one warehouse."
            );
        }

        Warehouse firstWarehouse =
                new Warehouse(ownerTypes.get(0));

        Seller seller = new Seller(
                entity.getIdentification(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getStatus(),
                firstWarehouse
        );

        ownerTypes.stream()
                .skip(1)
                .map(Warehouse::new)
                .forEach(
                        seller::associateWarehouse
                );

        return seller;
    }

    private static Administrator administratorToDomain(
            UserJpaEntity entity
    ) {
        return new Administrator(
                entity.getIdentification(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getStatus()
        );
    }
}