package application.services;

import application.domain.models.Buyer;
import application.ports.input.BuyerAddressUseCase;
import application.ports.output.UserRepository;

/**
 * Servicio encargado de administrar las direcciones
 * propias de un comprador.
 *
 * Implementa el puerto de entrada BuyerAddressUseCase,
 * permitiendo administrar las direcciones sin depender
 * directamente de esta implementación.
 *
 * Un comprador nunca puede modificar las direcciones
 * pertenecientes a otro comprador.
 */
public class BuyerAddressService
        implements BuyerAddressUseCase {

    private final UserRepository userRepository;

    /**
     * Construye el servicio con el repositorio de usuarios.
     *
     * @param userRepository repositorio utilizado
     *                       para guardar al comprador
     */
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
     *
     * @param buyer comprador que modifica su dirección
     * @param newPrimaryAddress nueva dirección principal
     * @return comprador actualizado
     */
    @Override
    public Buyer changePrimaryAddress(
            Buyer buyer,
            String newPrimaryAddress
    ) {
        validateActiveBuyer(buyer);

        /*
         * Buyer valida que la nueva dirección
         * no sea nula ni esté vacía.
         */
        buyer.changePrimaryAddress(newPrimaryAddress);

        userRepository.save(buyer);

        return buyer;
    }

    /**
     * Agrega una dirección adicional.
     *
     * @param buyer comprador que agrega la dirección
     * @param address dirección adicional
     * @return comprador actualizado
     */
    @Override
    public Buyer addAdditionalAddress(
            Buyer buyer,
            String address
    ) {
        validateActiveBuyer(buyer);

        /*
         * Buyer protege las reglas relacionadas
         * con sus direcciones adicionales.
         */
        buyer.addAdditionalAddress(address);

        userRepository.save(buyer);

        return buyer;
    }

    /**
     * Elimina una dirección adicional.
     *
     * El comprador se guarda únicamente si
     * la dirección realmente existía.
     *
     * @param buyer comprador que elimina la dirección
     * @param address dirección que se desea eliminar
     * @return true si la dirección fue eliminada
     */
    @Override
    public boolean removeAdditionalAddress(
            Buyer buyer,
            String address
    ) {
        validateActiveBuyer(buyer);

        boolean removed =
                buyer.removeAdditionalAddress(address);

        /*
         * Si la dirección no existía, no ocurrió
         * ningún cambio y no es necesario guardar.
         */
        if (removed) {
            userRepository.save(buyer);
        }

        return removed;
    }

    /**
     * Comprueba que la operación sea realizada
     * por un comprador activo.
     *
     * @param buyer comprador que administra sus direcciones
     */
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