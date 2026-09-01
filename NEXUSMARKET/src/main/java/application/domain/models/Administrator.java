package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/**
 * Representa a un administrador del sistema NexusMarket.
 *
 * Un administrador es un usuario especializado que se encarga de realizar
 * operaciones administrativas dentro del sistema. Hereda la información
 * personal y la gestión del estado definidas en la clase {@link User}.
 *
 * Su rol dentro del sistema siempre es
 * {@link SystemRole#ADMINISTRATOR}.
 */
public class Administrator extends User {

    /**
     * Crea un administrador con su información personal y su estado.
     *
     * La inicialización y validación de los atributos heredados se delegan
     * al constructor de la clase {@link User}.
     *
     * @param identification identificación del administrador
     * @param fullName nombre completo del administrador
     * @param email correo electrónico del administrador
     * @param status estado actual del administrador dentro del sistema
     */
    public Administrator(
            String identification,
            String fullName,
            String email,
            UserStatus status
    ) {
        super(identification, fullName, email, status);
    }

    /**
     * Obtiene el rol correspondiente al administrador.
     *
     * Este método garantiza que todos los objetos de esta clase tengan
     * siempre el rol {@link SystemRole#ADMINISTRATOR}.
     *
     * @return el rol de administrador del sistema
     */
    @Override
    public SystemRole getRole() {
        return SystemRole.ADMINISTRATOR;
    }
}