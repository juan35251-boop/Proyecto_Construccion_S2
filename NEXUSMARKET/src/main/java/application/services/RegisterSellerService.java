package application.services;

import application.domain.models.Administrator;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.valueobjects.UserStatus;
import application.ports.output.UserRepository;

/**
 * Servicio de aplicación encargado de registrar vendedores
 * dentro de NexusMarket.
 *
 * De acuerdo con la regla del negocio, un vendedor no puede
 * registrarse por sí mismo. El registro debe ser realizado por
 * un administrador activo.
 *
 * El servicio también comprueba que la identificación y el correo
 * electrónico no estén registrados previamente.
 */
public class RegisterSellerService {

    /**
     * Puerto utilizado para consultar y guardar usuarios.
     */
    private final UserRepository userRepository;

    /**
     * Crea el servicio con el repositorio de usuarios requerido.
     *
     * La dependencia se recibe mediante el constructor para mantener
     * el servicio desacoplado de la base de datos.
     *
     * @param userRepository repositorio utilizado para administrar usuarios
     *
     * @throws IllegalArgumentException si el repositorio es nulo
     */
    public RegisterSellerService(UserRepository userRepository) {
        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }

        this.userRepository = userRepository;
    }

    /**
     * Registra un nuevo vendedor.
     *
     * Primero valida que la operación sea realizada por un administrador
     * activo. Después crea el vendedor, comprueba la unicidad de su
     * identificación y correo, y finalmente solicita al repositorio
     * que lo guarde.
     *
     * @param administrator administrador que realiza el registro
     * @param identification identificación del nuevo vendedor
     * @param fullName nombre completo del nuevo vendedor
     * @param email correo electrónico del nuevo vendedor
     * @param status estado inicial del nuevo vendedor
     * @param firstWarehouse primera bodega del vendedor
     * @return el vendedor registrado
     *
     * @throws IllegalArgumentException si el administrador es nulo
     * @throws IllegalStateException si el administrador no está activo
     * @throws IllegalArgumentException si algún dato del vendedor es inválido
     * @throws IllegalStateException si la identificación ya está registrada
     * @throws IllegalStateException si el correo ya está registrado
     */
    public Seller register(
            Administrator administrator,
            String identification,
            String fullName,
            String email,
            UserStatus status,
            Warehouse firstWarehouse
    ) {
        validateAdministrator(administrator);

        Seller seller = new Seller(
                identification,
                fullName,
                email,
                status,
                firstWarehouse
        );

        validateUniqueIdentification(
                seller.getIdentification()
        );

        validateUniqueEmail(
                seller.getEmail()
        );

        userRepository.save(seller);

        return seller;
    }

    /**
     * Valida que la operación sea realizada por un administrador activo.
     *
     * No es necesario comprobar el rol manualmente porque el parámetro
     * solamente acepta objetos de tipo {@link Administrator}.
     *
     * @param administrator administrador que ejecuta la operación
     *
     * @throws IllegalArgumentException si el administrador es nulo
     * @throws IllegalStateException si el administrador no está activo
     */
    private void validateAdministrator(
            Administrator administrator
    ) {
        if (administrator == null) {
            throw new IllegalArgumentException(
                    "Seller registration requires an administrator."
            );
        }

        if (!administrator.isActive()) {
            throw new IllegalStateException(
                    "Only an active administrator can register sellers."
            );
        }
    }

    /**
     * Valida que la identificación no pertenezca a otro usuario.
     *
     * @param identification identificación que se desea validar
     *
     * @throws IllegalStateException si la identificación ya está registrada
     */
    private void validateUniqueIdentification(
            String identification
    ) {
        if (userRepository.existsByIdentification(identification)) {
            throw new IllegalStateException(
                    "Identification is already registered."
            );
        }
    }

    /**
     * Valida que el correo electrónico no pertenezca a otro usuario.
     *
     * @param email correo electrónico que se desea validar
     *
     * @throws IllegalStateException si el correo ya está registrado
     */
    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException(
                    "Email is already registered."
            );
        }
    }
}
