package application.ports.input;

import application.domain.models.Buyer;
import application.domain.models.User;

import java.util.List;

public interface BuyerQueryUseCase {

    Buyer findByIdentification(
            User requestedBy,
            String identification
    );

    List<Buyer> findAll(User requestedBy);

    Buyer findByIdentification(
            String requestedByIdentification,
            String identification
    );

    List<Buyer> findAll(
            String requestedByIdentification
    );
}