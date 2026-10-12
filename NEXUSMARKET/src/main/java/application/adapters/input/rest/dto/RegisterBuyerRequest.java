package application.adapters.input.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Datos recibidos por la API para registrar
 * un nuevo comprador.
 */
public record RegisterBuyerRequest(

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

        @NotBlank(
                message = "Primary address must not be empty."
        )
        String primaryAddress
) {
}