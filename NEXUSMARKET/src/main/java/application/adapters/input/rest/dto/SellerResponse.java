package application.adapters.input.rest.dto;

import application.domain.models.Seller;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

import java.util.List;

public record SellerResponse(
        String identification,
        String fullName,
        String email,
        SystemRole role,
        UserStatus status,
        List<WarehouseResponse> warehouses,
        int productCount
) {

    public static SellerResponse from(
            Seller seller
    ) {
        if (seller == null) {
            throw new IllegalArgumentException(
                    "Seller must not be null."
            );
        }

        List<WarehouseResponse> warehouses =
                seller.getWarehouses()
                        .stream()
                        .map(WarehouseResponse::from)
                        .toList();

        return new SellerResponse(
                seller.getIdentification(),
                seller.getFullName(),
                seller.getEmail(),
                seller.getRole(),
                seller.getStatus(),
                warehouses,
                seller.getProducts().size()
        );
    }
}