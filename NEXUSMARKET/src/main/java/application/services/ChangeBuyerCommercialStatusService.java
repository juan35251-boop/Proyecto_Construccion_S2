package application.services;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.valueobjects.BuyerStatus;
import application.ports.output.UserRepository;

/**
 * Servicio encargado de cambiar el estado comercial
 * de un comprador.
 *
 * Esta operación solamente puede ser realizada
 * por un administrador activo.
 */
public class ChangeBuyerCommercialStatusService {

    private final UserRepository userRepository;

    public ChangeBuyerCommercialStatusService(
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
     * Cambia el estado comercial del comprador.
     *
     * @param administrator administrador responsable
     * @param buyer comprador que será modificado
     * @param newStatus nuevo estado comercial
     * @return comprador actualizado
     */
    public Buyer changeStatus(
            Administrator administrator,
            Buyer buyer,
            BuyerStatus newStatus
    ) {
        validateAdministrator(administrator);
        validateBuyer(buyer);
        validateStatus(newStatus);

        buyer.changeCommercialStatus(newStatus);
        userRepository.save(buyer);

        return buyer;
    }

    private void validateAdministrator(
            Administrator administrator
    ) {
        if (administrator == null) {
            throw new IllegalArgumentException(
                    "Commercial status change requires an administrator."
            );
        }

        if (!administrator.isActive()) {
            throw new IllegalStateException(
                    "Only an active administrator can change commercial status."
            );
        }
    }

    private void validateBuyer(Buyer buyer) {
        if (buyer == null) {
            throw new IllegalArgumentException(
                    "Buyer must not be null."
            );
        }
    }

    private void validateStatus(BuyerStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Buyer commercial status must not be null."
            );
        }
    }
}