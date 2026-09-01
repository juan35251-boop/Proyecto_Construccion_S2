package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/**
 * Representa a un operador logístico del sistema NexusMarket.
 *
 * Un operador logístico es un usuario especializado que participa en
 * procesos relacionados con inventarios, bodegas y envíos.
 *
 * Hereda de {@link User} la información personal y la administración
 * de su estado general dentro del sistema.
 *
 * Su rol siempre es {@link SystemRole#LOGISTICS_OPERATOR}.
 */
public class LogisticsOperator extends User {

    /**
     * Crea un operador logístico con su información personal y estado.
     *
     * La inicialización y validación de los datos heredados se delegan
     * al constructor de la clase {@link User}.
     *
     * @param identification identificación del operador logístico
     * @param fullName nombre completo del operador logístico
     * @param email correo electrónico del operador logístico
     * @param status estado actual del operador dentro del sistema
     */
    public LogisticsOperator(
            String identification,
            String fullName,
            String email,
            UserStatus status
    ) {
        super(identification, fullName, email, status);
    }

    /**
     * Obtiene el rol correspondiente al operador logístico.
     *
     * Este método garantiza que todos los objetos de esta clase tengan
     * siempre el rol {@link SystemRole#LOGISTICS_OPERATOR}.
     *
     * @return el rol de operador logístico
     */
    @Override
    public SystemRole getRole() {
        return SystemRole.LOGISTICS_OPERATOR;
    }
}