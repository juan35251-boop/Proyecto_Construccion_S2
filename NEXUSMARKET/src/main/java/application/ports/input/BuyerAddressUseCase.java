package application.ports.input;

import application.domain.models.Buyer;

public interface BuyerAddressUseCase {

    Buyer changePrimaryAddress(
            Buyer buyer,
            String newPrimaryAddress
    );

    Buyer addAdditionalAddress(
            Buyer buyer,
            String address
    );

    boolean removeAdditionalAddress(
            Buyer buyer,
            String address
    );

    Buyer changePrimaryAddress(
            String buyerIdentification,
            String newPrimaryAddress
    );

    Buyer addAdditionalAddress(
            String buyerIdentification,
            String address
    );

    boolean removeAdditionalAddress(
            String buyerIdentification,
            String address
    );
}