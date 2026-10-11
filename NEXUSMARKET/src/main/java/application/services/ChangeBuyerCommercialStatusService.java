package application.services;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.valueobjects.BuyerStatus;
import application.ports.input.ChangeBuyerCommercialStatusUseCase;
import application.ports.output.UserRepository;

/**
 * Servicio encargado de cambiar el estado comercial
 * de un comprador.
 *
 * Implementa el puerto de entrada
 * ChangeBuyerCommercialStatusUseCase, permitiendo modificar
 * el estado comercial sin depender directamente
 * de esta implementación.
 *
 * Esta operación solamente puede ser realizada
 * por un administrador activo.
 */
public class ChangeBuyerCommercialStatusService
        implements ChangeBuyerCommercialStatusUseCase {

    private final UserRepository userRepository;

    /**
     * Construye el servicio con el repositorio de usuarios.
     *
     * @param userRepository repositorio utilizado
     *                       para guardar al comprador
     */
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
    @Override
    public Buyer changeStatus(
            Administrator administrator,
            Buyer buyer,
            BuyerStatus newStatus
    ) {
        validateAdministrator(administrator);
        validateBuyer(buyer);
        validateStatus(newStatus);

        /*
         * Buyer cambia su estado comercial sin modificar
         * el estado operativo general de su cuenta.
         */
        buyer.changeCommercialStatus(newStatus);

        /*
         * Se almacena el comprador actualizado mediante
         * el repositorio de usuarios.
         */
        userRepository.save(buyer);

        return buyer;
    }

    /**
     * Valida que la operación sea realizada
     * por un administrador activo.
     *
     * @param administrator administrador responsable
     */
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

    /**
     * Valida que exista un comprador.
     *
     * @param buyer comprador que será modificado
     */
    private void validateBuyer(Buyer buyer) {
        if (buyer == null) {
            throw new IllegalArgumentException(
                    "Buyer must not be null."
            );
        }
    }

    /**
     * Valida que se haya indicado un estado comercial.
     *
     * @param status nuevo estado comercial
     */
    private void validateStatus(BuyerStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Buyer commercial status must not be null."
            );
        }
    }
}