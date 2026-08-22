package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

public class LogisticsOperator extends User {

    public LogisticsOperator(
            String identification,
            String fullName,
            String email,
            UserStatus status
    ) {
        super(identification, fullName, email, status);
    }

    @Override
    public SystemRole getRole() {
        return SystemRole.LOGISTICS_OPERATOR;
    }
}
