package application.adapters.input.rest.dto;

import application.domain.valueobjects.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterSellerRequest(

        @NotBlank(
                message = "Identification must not be empty."
        )
        String identification,

        @NotBlank(
                message = "Full name must not be empty."
        )
        String fullName,

        @NotBlank(
                message = "Email must not be empty."
        )
        @Email(
                message = "Email must have a valid format."
        )
        String email,

        @NotNull(
                message = "Status must not be null."
        )
        UserStatus status
) {
}