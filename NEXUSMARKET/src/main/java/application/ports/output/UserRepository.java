package application.ports.output;

import application.domain.models.User;

public interface UserRepository {

    boolean existsByIdentification(String identification);

    boolean existsByEmail(String email);

    void save(User user);
}
