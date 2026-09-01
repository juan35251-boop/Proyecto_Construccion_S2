package application.domain.valueobjects;

/**
 * Representa los estados generales que puede tener un usuario
 * dentro de NexusMarket.
 *
 * Este estado determina si el usuario está habilitado para participar
 * en las operaciones del sistema. Es utilizado por todas las clases
 * que heredan de {@link application.domain.models.User}.
 */
public enum UserStatus {

    /**
     * Indica que el usuario está habilitado para utilizar las
     * funcionalidades correspondientes a su rol.
     */
    ACTIVE,

    /**
     * Indica que el usuario está inactivo temporalmente.
     *
     * Mientras permanezca en este estado, no se considera habilitado
     * para ejecutar operaciones que requieran un usuario activo.
     */
    INACTIVE,

    /**
     * Indica que el usuario fue bloqueado dentro del sistema.
     *
     * Un usuario bloqueado tampoco puede ejecutar operaciones que
     * requieran un estado activo.
     */
    BLOCKED
}