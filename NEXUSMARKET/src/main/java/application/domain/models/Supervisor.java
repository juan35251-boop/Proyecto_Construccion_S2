package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

public class Supervisor extends User {

    public Supervisor(
            String identification,
            String fullName,
            String email,
            UserStatus status
    ) {
        super(identification, fullName, email, status);
    }

    @Override
    public SystemRole getRole() {
        return SystemRole.SUPERVISOR;
    }
}