package application.adapters.output.persistence.jpa.mappers;

import application.adapters.output.persistence.jpa.entities.UserJpaEntity;
import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.LogisticsOperator;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.WarehouseOwnerType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Convierte usuarios entre el modelo del dominio
 * y la entidad utilizada por JPA.
 *
 * El mapper soporta compradores, vendedores,
 * administradores y operadores logísticos.
 */
public final class UserJpaMapper {

    private UserJpaMapper() {
        // Impide crear objetos de esta clase.
    }

    /**
     * Convierte un usuario del dominio
     * en una entidad persistente.
     *
     * @param user usuario del dominio
     * @return entidad persistente
     */
    public static UserJpaEntity toEntity(
            User user
    ) {
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

        if (user instanceof LogisticsOperator
                logisticsOperator) {
            return logisticsOperatorToEntity(
                    logisticsOperator
            );
        }

        throw new IllegalArgumentException(
                "Unsupported user type: "
                        + user.getClass().getSimpleName()
        );
    }

    /**
     * Convierte un comprador en entidad JPA.
     *
     * @param buyer comprador del dominio
     * @return entidad persistente
     */
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

    /**
     * Convierte un vendedor en entidad JPA.
     *
     * Conserva los identificadores permanentes
     * de las bodegas administradas por el vendedor.
     *
     * @param seller vendedor del dominio
     * @return entidad persistente
     */
    private static UserJpaEntity sellerToEntity(
            Seller seller
    ) {
        Set<String> warehouseIds =
                seller.getWarehouses()
                        .stream()
                        .map(Warehouse::getWarehouseId)
                        .collect(Collectors.toSet());

        return new UserJpaEntity(
                seller.getIdentification(),
                seller.getFullName(),
                seller.getEmail(),
                seller.getStatus(),
                seller.getRole(),
                warehouseIds
        );
    }

    /**
     * Convierte un administrador en entidad JPA.
     *
     * @param administrator administrador del dominio
     * @return entidad persistente
     */
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

    /**
     * Convierte un operador logístico en entidad JPA.
     *
     * @param logisticsOperator operador logístico
     * @return entidad persistente
     */
    private static UserJpaEntity
    logisticsOperatorToEntity(
            LogisticsOperator logisticsOperator
    ) {
        return new UserJpaEntity(
                logisticsOperator.getIdentification(),
                logisticsOperator.getFullName(),
                logisticsOperator.getEmail(),
                logisticsOperator.getStatus(),
                logisticsOperator.getRole()
        );
    }

    /**
     * Reconstruye un usuario desde JPA.
     *
     * @param entity entidad persistente
     * @return usuario del dominio
     */
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

        if (entity.getRole()
                == SystemRole.LOGISTICS_OPERATOR) {
            return logisticsOperatorToDomain(entity);
        }

        throw new IllegalArgumentException(
                "Unsupported user role: "
                        + entity.getRole()
        );
    }

    /**
     * Reconstruye un comprador.
     *
     * @param entity entidad persistente
     * @return comprador del dominio
     */
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

    /**
     * Reconstruye un vendedor y sus bodegas,
     * conservando los UUID persistidos.
     *
     * Todas las bodegas asociadas a un vendedor deben
     * pertenecer al tipo SELLER.
     *
     * @param entity entidad persistente
     * @return vendedor del dominio
     */
    private static Seller sellerToDomain(
            UserJpaEntity entity
    ) {
        List<Warehouse> warehouses =
                entity.getWarehouseIds()
                        .stream()
                        .map(warehouseId ->
                                new Warehouse(
                                        warehouseId,
                                        WarehouseOwnerType.SELLER
                                )
                        )
                        .toList();

        if (warehouses.isEmpty()) {
            throw new IllegalStateException(
                    "A persisted seller must have at least one warehouse."
            );
        }

        Seller seller = new Seller(
                entity.getIdentification(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getStatus(),
                warehouses.get(0)
        );

        warehouses.stream()
                .skip(1)
                .forEach(
                        seller::associateWarehouse
                );

        return seller;
    }

    /**
     * Reconstruye un administrador.
     *
     * @param entity entidad persistente
     * @return administrador del dominio
     */
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

    /**
     * Reconstruye un operador logístico.
     *
     * @param entity entidad persistente
     * @return operador logístico del dominio
     */
    private static LogisticsOperator
    logisticsOperatorToDomain(
            UserJpaEntity entity
    ) {
        return new LogisticsOperator(
                entity.getIdentification(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getStatus()
        );
    }
}