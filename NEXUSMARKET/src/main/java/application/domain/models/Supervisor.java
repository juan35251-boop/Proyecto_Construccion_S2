package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/**
 * Representa a un supervisor dentro del sistema NexusMarket.
 *
 * Un supervisor es un usuario especializado que participa en tareas
 * de seguimiento y supervisión de los procesos del sistema.
 *
 * Hereda de {@link User} la información personal y la gestión de su
 * estado general. Su rol siempre es {@link SystemRole#SUPERVISOR}.
 */
public class Supervisor extends User {

    /**
     * Crea un supervisor con su información personal y su estado.
     *
     * La inicialización y validación de los datos heredados se delegan
     * al constructor de la clase {@link User}.
     *
     * @param identification identificación del supervisor
     * @param fullName nombre completo del supervisor
     * @param email correo electrónico del supervisor
     * @param status estado actual del supervisor dentro del sistema
     */
    public Supervisor(
            String identification,
            String fullName,
            String email,
            UserStatus status
    ) {
        super(identification, fullName, email, status);
    }

    /**
     * Obtiene el rol correspondiente al supervisor.
     *
     * Este método garantiza que todos los objetos de esta clase tengan
     * siempre el rol {@link SystemRole#SUPERVISOR}.
     *
     * @return el rol de supervisor
     */
    @Override
    public SystemRole getRole() {
        return SystemRole.SUPERVISOR;
    }
}