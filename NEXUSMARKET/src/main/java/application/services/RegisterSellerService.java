package application.services;

import application.domain.models.Administrator;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.UserStatus;
import application.ports.input.RegisterSellerUseCase;
import application.ports.output.UserRepository;

public class RegisterSellerService
        implements RegisterSellerUseCase {

    private final UserRepository userRepository;

    public RegisterSellerService(
            UserRepository userRepository
    ) {
        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }

        this.userRepository = userRepository;
    }

    @Override
    public Seller register(
            Administrator administrator,
            String identification,
            String fullName,
            String email,
            UserStatus status,
            Warehouse firstWarehouse
    ) {
        validateAdministrator(administrator);

        Seller seller = new Seller(
                identification,
                fullName,
                email,
                status,
                firstWarehouse
        );

        validateUniqueIdentification(
                seller.getIdentification()
        );

        validateUniqueEmail(
                seller.getEmail()
        );

        userRepository.save(seller);

        return seller;
    }

    @Override
    public Seller register(
            String administratorIdentification,
            String identification,
            String fullName,
            String email,
            UserStatus status,
            Warehouse firstWarehouse
    ) {
        validateIdentification(
                administratorIdentification
        );

        User requestedBy = userRepository
                .findByIdentification(
                        administratorIdentification
                )
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Administrator was not found."
                        )
                );

        if (!(requestedBy
                instanceof Administrator administrator)) {
            throw new IllegalStateException(
                    "Only an administrator can register sellers."
            );
        }

        return register(
                administrator,
                identification,
                fullName,
                email,
                status,
                firstWarehouse
        );
    }

    private void validateAdministrator(
            Administrator administrator
    ) {
        if (administrator == null) {
            throw new IllegalArgumentException(
                    "Seller registration requires an administrator."
            );
        }

        if (!administrator.isActive()) {
            throw new IllegalStateException(
                    "Only an active administrator can register sellers."
            );
        }
    }

    private void validateIdentification(
            String identification
    ) {
        if (identification == null
                || identification.isBlank()) {
            throw new IllegalArgumentException(
                    "Administrator identification must not be empty."
            );
        }
    }

    private void validateUniqueIdentification(
            String identification
    ) {
        if (userRepository.existsByIdentification(
                identification
        )) {
            throw new IllegalStateException(
                    "Identification is already registered."
            );
        }
    }

    private void validateUniqueEmail(
            String email
    ) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException(
                    "Email is already registered."
            );
        }
    }
}