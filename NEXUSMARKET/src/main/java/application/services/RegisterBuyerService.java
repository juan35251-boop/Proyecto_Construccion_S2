package application.services;

import application.domain.models.Buyer;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.UserStatus;
import application.ports.output.UserRepository;

/**
 * Servicio de aplicación encargado de registrar compradores.
 *
 * Valida que la identificación y el correo electrónico no
 * pertenezcan a otro usuario antes de guardar al comprador.
 */
public class RegisterBuyerService {

    private final UserRepository userRepository;

    /**
     * Construye el servicio con el repositorio de usuarios.
     *
     * @param userRepository repositorio utilizado para validar
     *                       y guardar usuarios
     */
    public RegisterBuyerService(
            UserRepository userRepository
    ) {
        validateRepository(userRepository);
        this.userRepository = userRepository;
    }

    /**
     * Registra un nuevo comprador.
     *
     * Los compradores nuevos comienzan activos tanto en el sistema
     * como comercialmente. No se permite que el propio comprador
     * seleccione estados como bloqueado o suspendido.
     *
     * @param identification identificación del comprador
     * @param fullName nombre completo
     * @param email correo electrónico
     * @param primaryAddress dirección principal
     * @return comprador registrado
     */
    public Buyer register(
            String identification,
            String fullName,
            String email,
            String primaryAddress
    ) {
        /*
         * El constructor de Buyer valida que los datos obligatorios
         * no sean nulos ni estén vacíos.
         */
        Buyer buyer = new Buyer(
                identification,
                fullName,
                email,
                UserStatus.ACTIVE,
                primaryAddress,
                BuyerStatus.ACTIVE
        );

        validateUniqueIdentification(identification);
        validateUniqueEmail(email);

        userRepository.save(buyer);

        return buyer;
    }

    /**
     * Valida que el repositorio exista.
     */
    private void validateRepository(
            UserRepository userRepository
    ) {
        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }
    }

    /**
     * Comprueba que la identificación sea única.
     */
    private void validateUniqueIdentification(
            String identification
    ) {
        if (userRepository.existsByIdentification(
                identification
        )) {
            throw new IllegalStateException(
                    "Identification is already registered."
            );
        }
    }

    /**
     * Comprueba que el correo electrónico sea único.
     */
    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException(
                    "Email is already registered."
            );
        }
    }
}