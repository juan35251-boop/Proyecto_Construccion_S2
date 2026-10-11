package application.services;

import application.domain.models.Administrator;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.valueobjects.UserStatus;
import application.ports.input.RegisterSellerUseCase;
import application.ports.output.UserRepository;

/**
 * Servicio de aplicación encargado de registrar vendedores
 * dentro de NexusMarket.
 *
 * Implementa el puerto de entrada RegisterSellerUseCase,
 * permitiendo que los futuros controladores utilicen este
 * caso de uso sin depender directamente de su implementación.
 *
 * De acuerdo con la regla del negocio, un vendedor no puede
 * registrarse por sí mismo. El registro debe ser realizado
 * por un administrador activo.
 *
 * El servicio también comprueba que la identificación y el
 * correo electrónico no estén registrados previamente.
 */
public class RegisterSellerService
        implements RegisterSellerUseCase {

    /**
     * Puerto de salida utilizado para consultar
     * y guardar usuarios.
     */
    private final UserRepository userRepository;

    /**
     * Construye el servicio con el repositorio requerido.
     *
     * La dependencia se recibe mediante el constructor para
     * mantener el servicio independiente de MySQL, MongoDB
     * o cualquier tecnología de almacenamiento.
     *
     * @param userRepository repositorio utilizado para
     *                       administrar usuarios
     *
     * @throws IllegalArgumentException si el repositorio es nulo
     */
    public RegisterSellerService(
            UserRepository userRepository
    ) {
        validateRepository(userRepository);
        this.userRepository = userRepository;
    }

    /**
     * Registra un nuevo vendedor.
     *
     * Primero valida que la operación sea realizada por un
     * administrador activo. Después crea el vendedor, comprueba
     * la unicidad de su identificación y correo, y finalmente
     * solicita al repositorio que lo guarde.
     *
     * @param administrator administrador que realiza el registro
     * @param identification identificación del nuevo vendedor
     * @param fullName nombre completo del nuevo vendedor
     * @param email correo electrónico del nuevo vendedor
     * @param status estado inicial del nuevo vendedor
     * @param firstWarehouse primera bodega del vendedor
     * @return vendedor registrado
     *
     * @throws IllegalArgumentException si el administrador
     *         o algún dato obligatorio es inválido
     * @throws IllegalStateException si el administrador no está
     *         activo o los datos ya están registrados
     */
    @Override
    public Seller register(
            Administrator administrator,
            String identification,
            String fullName,
            String email,
            UserStatus status,
            Warehouse firstWarehouse
    ) {
        validateAdministrator(administrator);

        /*
         * Seller valida internamente los datos generales
         * del usuario y que la primera bodega pertenezca
         * a un vendedor.
         */
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

        /*
         * El servicio utiliza el puerto de salida sin conocer
         * la tecnología donde se almacenará el vendedor.
         */
        userRepository.save(seller);

        return seller;
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
     * Valida que la operación sea realizada
     * por un administrador activo.
     *
     * No es necesario comprobar manualmente el rol porque
     * el parámetro solamente admite objetos Administrator.
     *
     * @param administrator administrador que ejecuta la operación
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
     * Valida que la identificación no pertenezca
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
     * Valida que el correo electrónico no pertenezca
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