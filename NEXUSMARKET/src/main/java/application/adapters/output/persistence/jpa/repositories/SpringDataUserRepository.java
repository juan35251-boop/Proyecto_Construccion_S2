package application.adapters.output.persistence.jpa.repositories;

import application.adapters.output.persistence.jpa.entities.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUserRepository
        extends JpaRepository<UserJpaEntity, String> {

    boolean existsByEmailIgnoreCase(String email);
}