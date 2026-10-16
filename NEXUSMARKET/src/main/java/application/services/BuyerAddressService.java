package application.services;

import application.domain.models.Buyer;
import application.domain.models.User;
import application.ports.input.BuyerAddressUseCase;
import application.ports.output.UserRepository;

public class BuyerAddressService
        implements BuyerAddressUseCase {

    private final UserRepository userRepository;

    public BuyerAddressService(
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
    public Buyer changePrimaryAddress(
            Buyer buyer,
            String newPrimaryAddress
    ) {
        validateActiveBuyer(buyer);

        buyer.changePrimaryAddress(
                newPrimaryAddress
        );

        userRepository.save(buyer);

        return buyer;
    }

    @Override
    public Buyer addAdditionalAddress(
            Buyer buyer,
            String address
    ) {
        validateActiveBuyer(buyer);

        buyer.addAdditionalAddress(address);

        userRepository.save(buyer);

        return buyer;
    }

    @Override
    public boolean removeAdditionalAddress(
            Buyer buyer,
            String address
    ) {
        validateActiveBuyer(buyer);

        boolean removed =
                buyer.removeAdditionalAddress(address);

        if (removed) {
            userRepository.save(buyer);
        }

        return removed;
    }

    @Override
    public Buyer changePrimaryAddress(
            String buyerIdentification,
            String newPrimaryAddress
    ) {
        Buyer buyer = findBuyer(
                buyerIdentification
        );

        return changePrimaryAddress(
                buyer,
                newPrimaryAddress
        );
    }

    @Override
    public Buyer addAdditionalAddress(
            String buyerIdentification,
            String address
    ) {
        Buyer buyer = findBuyer(
                buyerIdentification
        );

        return addAdditionalAddress(
                buyer,
                address
        );
    }

    @Override
    public boolean removeAdditionalAddress(
            String buyerIdentification,
            String address
    ) {
        Buyer buyer = findBuyer(
                buyerIdentification
        );

        return removeAdditionalAddress(
                buyer,
                address
        );
    }

    private Buyer findBuyer(
            String buyerIdentification
    ) {
        if (buyerIdentification == null
                || buyerIdentification.isBlank()) {
            throw new IllegalArgumentException(
                    "Buyer identification must not be empty."
            );
        }

        User user = userRepository
                .findByIdentification(
                        buyerIdentification
                )
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Buyer was not found."
                        )
                );

        if (!(user instanceof Buyer buyer)) {
            throw new IllegalStateException(
                    "The requested user is not a buyer."
            );
        }

        return buyer;
    }

    private void validateActiveBuyer(Buyer buyer) {
        if (buyer == null) {
            throw new IllegalArgumentException(
                    "Address operation requires a buyer."
            );
        }

        if (!buyer.isActive()) {
            throw new IllegalStateException(
                    "Only an active buyer can manage addresses."
            );
        }
    }
}