package application.adapters.output.persistence.jpa.adapters;

import application.adapters.output.persistence.jpa.entities.UserJpaEntity;
import application.adapters.output.persistence.jpa.repositories.SpringDataUserRepository;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaUserRepositoryAdapterTest {

    private SpringDataUserRepository springRepository;
    private JpaUserRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        springRepository =
                mock(SpringDataUserRepository.class);

        adapter = new JpaUserRepositoryAdapter(
                springRepository
        );
    }

    @Test
    void shouldCheckIfIdentificationExists() {
        when(springRepository.existsById("1001"))
                .thenReturn(true);

        boolean result =
                adapter.existsByIdentification("1001");

        assertTrue(result);

        verify(springRepository)
                .existsById("1001");
    }

    @Test
    void shouldCheckIfEmailExists() {
        when(springRepository.existsByEmailIgnoreCase(
                "juan@email.com"
        )).thenReturn(true);

        boolean result =
                adapter.existsByEmail("juan@email.com");

        assertTrue(result);

        verify(springRepository)
                .existsByEmailIgnoreCase(
                        "juan@email.com"
                );
    }

    @Test
    void shouldSaveBuyerAsJpaEntity() {
        Buyer buyer = new Buyer(
                "1001",
                "Juan Perez",
                "juan@email.com",
                UserStatus.ACTIVE,
                "Calle 10 #20-30",
                BuyerStatus.ACTIVE
        );

        adapter.save(buyer);

        verify(springRepository)
                .save(any(UserJpaEntity.class));
    }

    @Test
    void shouldFindBuyerByIdentification() {
        UserJpaEntity entity = createEntity();

        when(springRepository.findById("1001"))
                .thenReturn(Optional.of(entity));

        Optional<User> result =
                adapter.findByIdentification("1001");

        assertTrue(result.isPresent());

        Buyer buyer = assertInstanceOf(
                Buyer.class,
                result.get()
        );

        assertEquals(
                "1001",
                buyer.getIdentification()
        );

        assertEquals(
                "Juan Perez",
                buyer.getFullName()
        );
    }

    @Test
    void shouldReturnAllUsers() {
        when(springRepository.findAll())
                .thenReturn(List.of(createEntity()));

        List<User> users = adapter.findAll();

        assertEquals(1, users.size());

        assertInstanceOf(
                Buyer.class,
                users.get(0)
        );
    }

    private UserJpaEntity createEntity() {
        return new UserJpaEntity(
                "1001",
                "Juan Perez",
                "juan@email.com",
                UserStatus.ACTIVE,
                SystemRole.BUYER,
                "Calle 10 #20-30",
                BuyerStatus.ACTIVE,
                List.of()
        );
    }
}