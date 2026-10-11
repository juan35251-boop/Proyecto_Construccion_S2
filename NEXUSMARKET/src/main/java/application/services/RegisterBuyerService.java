package application.services;

import application.domain.models.Buyer;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.UserStatus;
import application.ports.input.RegisterBuyerUseCase;
import application.ports.output.UserRepository;

/**
 * Servicio de aplicación encargado de registrar compradores.
 *
 * Implementa el puerto de entrada RegisterBuyerUseCase,
 * por lo que puede ser utilizado posteriormente por un
 * controlador REST sin que el controlador dependa
 * directamente de detalles internos del servicio.
 *
 * Valida que la identificación y el correo electrónico
 * no pertenezcan a otro usuario antes de guardar
 * al comprador.
 */
public class RegisterBuyerService
        implements RegisterBuyerUseCase {

    private final UserRepository userRepository;

    /**
     * Construye el servicio con el repositorio de usuarios.
     *
     * El servicio depende del puerto de salida UserRepository
     * y no de una base de datos específica.
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
     * Los compradores nuevos comienzan activos tanto en el
     * sistema como comercialmente. El comprador no puede
     * seleccionar estados como bloqueado o suspendido
     * durante su registro.
     *
     * @param identification identificación del comprador
     * @param fullName nombre completo del comprador
     * @param email correo electrónico del comprador
     * @param primaryAddress dirección principal de entrega
     * @return comprador registrado
     */
    @Override
    public Buyer register(
            String identification,
            String fullName,
            String email,
            String primaryAddress
    ) {
        /*
         * El constructor de Buyer valida primero que todos
         * los datos obligatorios tengan contenido.
         */
        Buyer buyer = new Buyer(
                identification,
                fullName,
                email,
                UserStatus.ACTIVE,
                primaryAddress,
                BuyerStatus.ACTIVE
        );

        /*
         * Estas validaciones requieren consultar todos los
         * usuarios existentes mediante el puerto de salida.
         */
        validateUniqueIdentification(identification);
        validateUniqueEmail(email);

        /*
         * El servicio solicita guardar al comprador, pero no
         * conoce si se utilizará MySQL, memoria u otra tecnología.
         */
        userRepository.save(buyer);

        return buyer;
    }

    /**
     * Valida que el repositorio requerido exista.
     *
     * @param userRepository repositorio de usuarios
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
     * Comprueba que la identificación no pertenezca
     * a otro usuario.
     *
     * @param identification identificación que se desea validar
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
     * Comprueba que el correo electrónico no pertenezca
     * a otro usuario.
     *
     * @param email correo electrónico que se desea validar
     */
    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException(
                    "Email is already registered."
            );
        }
    }
}