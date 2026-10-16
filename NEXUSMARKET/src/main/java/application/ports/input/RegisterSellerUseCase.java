package application.ports.input;

import application.domain.models.Administrator;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.valueobjects.UserStatus;

public interface RegisterSellerUseCase {

    Seller register(
            Administrator administrator,
            String identification,
            String fullName,
            String email,
            UserStatus status,
            Warehouse firstWarehouse
    );

    Seller register(
            String administratorIdentification,
            String identification,
            String fullName,
            String email,
            UserStatus status,
            Warehouse firstWarehouse
    );
}