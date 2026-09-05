package application.services;

import application.domain.models.Buyer;
import application.ports.output.UserRepository;

/**
 * Servicio encargado de administrar las direcciones
 * propias de un comprador.
 *
 * Un comprador nunca puede modificar las direcciones
 * pertenecientes a otro comprador.
 */
public class BuyerAddressService {

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

    /**
     * Cambia la dirección principal del comprador.
     */
    public Buyer changePrimaryAddress(
            Buyer buyer,
            String newPrimaryAddress
    ) {
        validateActiveBuyer(buyer);

        buyer.changePrimaryAddress(newPrimaryAddress);
        userRepository.save(buyer);

        return buyer;
    }

    /**
     * Agrega una dirección adicional.
     */
    public Buyer addAdditionalAddress(
            Buyer buyer,
            String address
    ) {
        validateActiveBuyer(buyer);

        buyer.addAdditionalAddress(address);
        userRepository.save(buyer);

        return buyer;
    }

    /**
     * Elimina una dirección adicional.
     *
     * El comprador se guarda únicamente si la dirección existía.
     *
     * @return true si la dirección fue eliminada
     */
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