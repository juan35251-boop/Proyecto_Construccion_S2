package application.services;

import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.BuyerQueryUseCase;
import application.ports.output.UserRepository;

import java.util.List;

public class BuyerQueryService
        implements BuyerQueryUseCase {

    private final UserRepository userRepository;

    public BuyerQueryService(
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
    public Buyer findByIdentification(
            User requestedBy,
            String identification
    ) {
        validateActiveRequester(requestedBy);
        validateIdentification(identification);

        User foundUser = userRepository
                .findByIdentification(identification)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Buyer was not found."
                        )
                );

        if (!(foundUser instanceof Buyer buyer)) {
            throw new IllegalStateException(
                    "The requested user is not a buyer."
            );
        }

        boolean isSameBuyer =
                requestedBy instanceof Buyer
                        && requestedBy
                        .getIdentification()
                        .equals(
                                buyer.getIdentification()
                        );

        if (!isSameBuyer
                && !hasGlobalAccess(requestedBy)) {
            throw new IllegalStateException(
                    "User is not authorized to query this buyer."
            );
        }

        return buyer;
    }

    @Override
    public List<Buyer> findAll(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        if (!hasGlobalAccess(requestedBy)) {
            throw new IllegalStateException(
                    "User is not authorized to query all buyers."
            );
        }

        return userRepository
                .findAll()
                .stream()
                .filter(Buyer.class::isInstance)
                .map(Buyer.class::cast)
                .toList();
    }

    @Override
    public Buyer findByIdentification(
            String requestedByIdentification,
            String identification
    ) {
        User requestedBy = findRequester(
                requestedByIdentification
        );

        return findByIdentification(
                requestedBy,
                identification
        );
    }

    @Override
    public List<Buyer> findAll(
            String requestedByIdentification
    ) {
        User requestedBy = findRequester(
                requestedByIdentification
        );

        return findAll(requestedBy);
    }

    private User findRequester(
            String requestedByIdentification
    ) {
        validateIdentification(
                requestedByIdentification
        );

        return userRepository
                .findByIdentification(
                        requestedByIdentification
                )
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Requesting user was not found."
                        )
                );
    }

    private void validateActiveRequester(
            User requestedBy
    ) {
        if (requestedBy == null) {
            throw new IllegalArgumentException(
                    "Requesting user must not be null."
            );
        }

        if (!requestedBy.isActive()) {
            throw new IllegalStateException(
                    "Only active users can query buyers."
            );
        }
    }

    private void validateIdentification(
            String identification
    ) {
        if (identification == null
                || identification.isBlank()) {
            throw new IllegalArgumentException(
                    "Identification must not be empty."
            );
        }
    }

    private boolean hasGlobalAccess(User user) {
        return user.getRole()
                == SystemRole.ADMINISTRATOR
                || user.getRole()
                == SystemRole.SUPERVISOR;
    }
}