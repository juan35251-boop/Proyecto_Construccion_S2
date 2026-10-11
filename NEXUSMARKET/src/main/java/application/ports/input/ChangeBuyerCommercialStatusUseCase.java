package application.ports.input;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.valueobjects.BuyerStatus;

/**
 * Puerto de entrada que define el caso de uso
 * para cambiar el estado comercial de un comprador.
 *
 * Esta operación solamente puede ser realizada
 * por un administrador activo.
 */
public interface ChangeBuyerCommercialStatusUseCase {

    /**
     * Cambia el estado comercial del comprador.
     *
     * @param administrator administrador responsable
     * @param buyer comprador que será modificado
     * @param newStatus nuevo estado comercial
     * @return comprador actualizado
     */
    Buyer changeStatus(
            Administrator administrator,
            Buyer buyer,
            BuyerStatus newStatus
    );
}