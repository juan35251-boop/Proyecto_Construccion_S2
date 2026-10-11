package application.ports.input;

import application.domain.models.Buyer;

/**
 * Puerto de entrada que define el caso de uso
 * para administrar las direcciones de un comprador.
 *
 * Un comprador activo solamente puede modificar
 * sus propias direcciones.
 */
public interface BuyerAddressUseCase {

    /**
     * Cambia la dirección principal del comprador.
     *
     * @param buyer comprador que modifica su dirección
     * @param newPrimaryAddress nueva dirección principal
     * @return comprador actualizado
     */
    Buyer changePrimaryAddress(
            Buyer buyer,
            String newPrimaryAddress
    );

    /**
     * Agrega una dirección adicional.
     *
     * @param buyer comprador que agrega la dirección
     * @param address dirección adicional
     * @return comprador actualizado
     */
    Buyer addAdditionalAddress(
            Buyer buyer,
            String address
    );

    /**
     * Elimina una dirección adicional.
     *
     * @param buyer comprador que elimina la dirección
     * @param address dirección que se desea eliminar
     * @return true si la dirección existía y fue eliminada
     */
    boolean removeAdditionalAddress(
            Buyer buyer,
            String address
    );
}