package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/**
 * Representa la información y los comportamientos comunes de los usuarios
 * de NexusMarket.
 *
 * Esta clase es abstracta porque no existe un usuario genérico dentro del
 * sistema. Cada usuario debe pertenecer a una especialización concreta,
 * como administrador, supervisor, operador logístico, vendedor o comprador.
 *
 * Las clases hijas deben implementar {@link #getRole()} para indicar
 * el rol que les corresponde.
 */
public abstract class User {

    /**
     * Identificación del usuario.
     *
     * No puede modificarse después de crear el usuario.
     */
    private final String identification;

    /**
     * Nombre completo actual del usuario.
     */
    private String fullName;

    /**
     * Correo electrónico actual del usuario.
     */
    private String email;

    /**
     * Estado general del usuario dentro del sistema.
     */
    private UserStatus status;

    /**
     * Inicializa la información común de una clase concreta de usuario.
     *
     * El constructor es {@code protected} para que solamente pueda ser
     * utilizado por las clases que heredan de {@code User}.
     *
     * Todos los valores son validados antes de inicializar el usuario.
     *
     * @param identification identificación del usuario
     * @param fullName nombre completo del usuario
     * @param email correo electrónico del usuario
     * @param status estado inicial del usuario
     *
     * @throws IllegalArgumentException si la identificación es nula,
     *                                  vacía o contiene solamente espacios
     * @throws IllegalArgumentException si el nombre es nulo, vacío
     *                                  o contiene solamente espacios
     * @throws IllegalArgumentException si el correo es nulo, vacío
     *                                  o contiene solamente espacios
     * @throws IllegalArgumentException si el estado es nulo
     */
    protected User(
            String identification,
            String fullName,
            String email,
            UserStatus status
    ) {
        validateText(identification, "Identification");
        validateText(fullName, "Full name");
        validateText(email, "Email");
        validateStatus(status);

        this.identification = identification;
        this.fullName = fullName;
        this.email = email;
        this.status = status;
    }

    /**
     * Obtiene el rol específico del usuario.
     *
     * Cada clase hija debe implementar este método y devolver el rol que
     * le corresponde.
     *
     * @return el rol del usuario dentro del sistema
     */
    public abstract SystemRole getRole();

    /**
     * Obtiene la identificación del usuario.
     *
     * @return la identificación registrada
     */
    public String getIdentification() {
        return identification;
    }

    /**
     * Obtiene el nombre completo del usuario.
     *
     * @return el nombre completo actual
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     *
     * @return el correo actual
     */
    public String getEmail() {
        return email;
    }

    /**
     * Obtiene el estado general del usuario.
     *
     * @return el estado actual
     */
    public UserStatus getStatus() {
        return status;
    }

    /**
     * Cambia el nombre completo del usuario.
     *
     * El nuevo nombre es validado antes de reemplazar el valor actual.
     *
     * @param newFullName nuevo nombre completo
     *
     * @throws IllegalArgumentException si el nuevo nombre es nulo,
     *                                  vacío o contiene solamente espacios
     */
    public void changeFullName(String newFullName) {
        validateText(newFullName, "Full name");
        this.fullName = newFullName;
    }

    /**
     * Cambia el correo electrónico del usuario.
     *
     * El nuevo correo es validado antes de reemplazar el valor actual.
     *
     * @param newEmail nuevo correo electrónico
     *
     * @throws IllegalArgumentException si el nuevo correo es nulo,
     *                                  vacío o contiene solamente espacios
     */
    public void changeEmail(String newEmail) {
        validateText(newEmail, "Email");
        this.email = newEmail;
    }

    /**
     * Cambia el estado general del usuario.
     *
     * @param newStatus nuevo estado del usuario
     *
     * @throws IllegalArgumentException si el nuevo estado es nulo
     */
    public void changeStatus(UserStatus newStatus) {
        validateStatus(newStatus);
        this.status = newStatus;
    }

    /**
     * Indica si el usuario se encuentra activo.
     *
     * @return {@code true} si el estado es
     *         {@link UserStatus#ACTIVE}; de lo contrario,
     *         {@code false}
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    /**
     * Indica si el usuario se encuentra bloqueado.
     *
     * @return {@code true} si el estado es
     *         {@link UserStatus#BLOCKED}; de lo contrario,
     *         {@code false}
     */
    public boolean isBlocked() {
        return status == UserStatus.BLOCKED;
    }

    /**
     * Valida que un texto obligatorio contenga información.
     *
     * El nombre del campo se recibe para construir un mensaje de error
     * específico y reutilizar la misma validación con diferentes atributos.
     *
     * @param value texto que se desea validar
     * @param fieldName nombre del campo incluido en el mensaje de error
     *
     * @throws IllegalArgumentException si el texto es nulo, está vacío
     *                                  o solamente contiene espacios
     */
    private void validateText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }
    }

    /**
     * Valida que el estado del usuario no sea nulo.
     *
     * @param status estado que se desea validar
     *
     * @throws IllegalArgumentException si el estado es nulo
     */
    private void validateStatus(UserStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "User status must not be null."
            );
        }
    }
}