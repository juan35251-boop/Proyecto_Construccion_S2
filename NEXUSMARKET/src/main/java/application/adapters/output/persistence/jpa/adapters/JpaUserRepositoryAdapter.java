package application.adapters.output.persistence.jpa.adapters;

import application.adapters.output.persistence.jpa.entities.UserJpaEntity;
import application.adapters.output.persistence.jpa.mappers.UserJpaMapper;
import application.adapters.output.persistence.jpa.repositories.SpringDataUserRepository;
import application.domain.models.User;
import application.ports.output.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaUserRepositoryAdapter
        implements UserRepository {

    private final SpringDataUserRepository repository;

    public JpaUserRepositoryAdapter(
            SpringDataUserRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public boolean existsByIdentification(
            String identification
    ) {
        return repository.existsById(identification);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmailIgnoreCase(email);
    }

    @Override
    public Optional<User> findByIdentification(
            String identification
    ) {
        return repository
                .findById(identification)
                .map(UserJpaMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return repository
                .findAll()
                .stream()
                .map(UserJpaMapper::toDomain)
                .toList();
    }

    @Override
    public void save(User user) {
        UserJpaEntity entity =
                UserJpaMapper.toEntity(user);

        repository.save(entity);
    }
}