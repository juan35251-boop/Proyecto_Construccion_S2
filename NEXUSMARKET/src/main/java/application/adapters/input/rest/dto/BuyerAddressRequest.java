package application.adapters.input.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record BuyerAddressRequest(

        @NotBlank(
                message = "Address must not be empty."
        )
        String address

) {
}