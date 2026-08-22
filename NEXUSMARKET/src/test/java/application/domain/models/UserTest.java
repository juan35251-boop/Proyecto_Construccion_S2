package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    @Test
    void shouldAssignExpectedParticipantRoles() {
        LogisticsOperator operator = new LogisticsOperator(
                "3001",
                "Logistics Operator",
                "operator@email.com",
                UserStatus.ACTIVE
        );

        Administrator administrator = createAdministrator();

        Supervisor supervisor = new Supervisor(
                "5001",
                "Marketplace Supervisor",
                "supervisor@email.com",
                UserStatus.ACTIVE
        );

        assertEquals(
                SystemRole.LOGISTICS_OPERATOR,
                operator.getRole()
        );

        assertEquals(
                SystemRole.ADMINISTRATOR,
                administrator.getRole()
        );

        assertEquals(
                SystemRole.SUPERVISOR,
                supervisor.getRole()
        );
    }

    @Test
    void shouldChangeUserInformation() {
        Administrator administrator = createAdministrator();

        administrator.changeFullName("New Administrator Name");
        administrator.changeEmail("newadmin@email.com");

        assertEquals(
                "New Administrator Name",
                administrator.getFullName()
        );

        assertEquals(
                "newadmin@email.com",
                administrator.getEmail()
        );
    }

    @Test
    void shouldChangeAndIdentifyBlockedStatus() {
        Administrator administrator = createAdministrator();

        assertTrue(administrator.isActive());
        assertFalse(administrator.isBlocked());

        administrator.changeStatus(UserStatus.BLOCKED);

        assertFalse(administrator.isActive());
        assertTrue(administrator.isBlocked());
    }

    @Test
    void shouldRejectEmptyIdentification() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Administrator(
                        "",
                        "Administrator",
                        "admin@email.com",
                        UserStatus.ACTIVE
                )
        );
    }

    @Test
    void shouldRejectEmptyFullName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Administrator(
                        "4001",
                        "",
                        "admin@email.com",
                        UserStatus.ACTIVE
                )
        );
    }

    @Test
    void shouldRejectEmptyEmail() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Administrator(
                        "4001",
                        "Administrator",
                        "",
                        UserStatus.ACTIVE
                )
        );
    }

    @Test
    void shouldRejectNullUserStatus() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Administrator(
                        "4001",
                        "Administrator",
                        "admin@email.com",
                        null
                )
        );
    }

    private Administrator createAdministrator() {
        return new Administrator(
                "4001",
                "Marketplace Administrator",
                "admin@email.com",
                UserStatus.ACTIVE
        );
    }
}