package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

public abstract class User {

    private final String identification;
    private String fullName;
    private String email;
    private UserStatus status;

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

    public abstract SystemRole getRole();

    public String getIdentification() {
        return identification;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void changeFullName(String newFullName) {
        validateText(newFullName, "Full name");
        this.fullName = newFullName;
    }

    public void changeEmail(String newEmail) {
        validateText(newEmail, "Email");
        this.email = newEmail;
    }

    public void changeStatus(UserStatus newStatus) {
        validateStatus(newStatus);
        this.status = newStatus;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public boolean isBlocked() {
        return status == UserStatus.BLOCKED;
    }

    private void validateText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }
    }

    private void validateStatus(UserStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "User status must not be null."
            );
        }
    }
}