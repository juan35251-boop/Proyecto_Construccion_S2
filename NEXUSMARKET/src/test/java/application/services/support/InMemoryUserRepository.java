package application.services.support;

import application.domain.models.User;
import application.ports.output.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación en memoria del repositorio de usuarios.
 *
 * Se utiliza únicamente en pruebas para comprobar los servicios
 * sin necesitar todavía una base de datos.
 */
public class InMemoryUserRepository
        implements UserRepository {

    private final List<User> users = new ArrayList<>();
    private int saveCount;

    /**
     * Comprueba si existe una identificación.
     */
    @Override
    public boolean existsByIdentification(
            String identification
    ) {
        return users.stream()
                .anyMatch(
                        user ->
                                user.getIdentification()
                                        .equals(identification)
                );
    }

    /**
     * Comprueba si existe un correo electrónico.
     */
    @Override
    public boolean existsByEmail(String email) {
        return users.stream()
                .anyMatch(
                        user ->
                                user.getEmail()
                                        .equalsIgnoreCase(email)
                );
    }

    /**
     * Busca un usuario por identificación.
     */
    @Override
    public Optional<User> findByIdentification(
            String identification
    ) {
        return users.stream()
                .filter(
                        user ->
                                user.getIdentification()
                                        .equals(identification)
                )
                .findFirst();
    }

    /**
     * Devuelve una copia de los usuarios almacenados.
     */
    @Override
    public List<User> findAll() {
        return List.copyOf(users);
    }

    /**
     * Guarda un usuario o actualiza el que tenga
     * la misma identificación.
     */
    @Override
    public void save(User user) {
        users.removeIf(
                storedUser ->
                        storedUser.getIdentification()
                                .equals(user.getIdentification())
        );

        users.add(user);
        saveCount++;
    }

    /**
     * Indica cuántas veces se llamó al método save.
     */
    public int getSaveCount() {
        return saveCount;
    }

    /**
     * Comprueba si una instancia está almacenada.
     */
    public boolean contains(User user) {
        return users.contains(user);
    }
}