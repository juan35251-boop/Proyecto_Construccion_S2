package application.adapters.output.persistence.jpa.mappers;

import application.adapters.output.persistence.jpa.entities.UserJpaEntity;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class UserJpaMapperTest {

    @Test
    void shouldConvertBuyerToJpaEntity() {
        Buyer buyer = new Buyer(
                "1001",
                "Juan Perez",
                "juan@email.com",
                UserStatus.ACTIVE,
                "Calle 10 #20-30",
                BuyerStatus.ACTIVE
        );

        buyer.addAdditionalAddress(
                "Carrera 40 #50-60"
        );

        UserJpaEntity entity =
                UserJpaMapper.toEntity(buyer);

        assertEquals(
                "1001",
                entity.getIdentification()
        );
        assertEquals(
                "Juan Perez",
                entity.getFullName()
        );
        assertEquals(
                "juan@email.com",
                entity.getEmail()
        );
        assertEquals(
                UserStatus.ACTIVE,
                entity.getStatus()
        );
        assertEquals(
                SystemRole.BUYER,
                entity.getRole()
        );
        assertEquals(
                "Calle 10 #20-30",
                entity.getPrimaryAddress()
        );
        assertEquals(
                BuyerStatus.ACTIVE,
                entity.getBuyerStatus()
        );
        assertEquals(
                1,
                entity.getAdditionalAddresses().size()
        );
    }

    @Test
    void shouldConvertJpaEntityToBuyer() {
        UserJpaEntity entity = new UserJpaEntity(
                "1001",
                "Juan Perez",
                "juan@email.com",
                UserStatus.ACTIVE,
                SystemRole.BUYER,
                "Calle 10 #20-30",
                BuyerStatus.ACTIVE,
                java.util.List.of(
                        "Carrera 40 #50-60"
                )
        );

        User result =
                UserJpaMapper.toDomain(entity);

        Buyer buyer = assertInstanceOf(
                Buyer.class,
                result
        );

        assertEquals(
                "1001",
                buyer.getIdentification()
        );
        assertEquals(
                "Juan Perez",
                buyer.getFullName()
        );
        assertEquals(
                "Calle 10 #20-30",
                buyer.getPrimaryAddress()
        );
        assertEquals(
                1,
                buyer.getAdditionalAddresses().size()
        );
        assertEquals(
                "Carrera 40 #50-60",
                buyer.getAdditionalAddresses().get(0)
        );
    }
}