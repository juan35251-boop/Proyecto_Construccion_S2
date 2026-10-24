package application.adapters.input.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Datos recibidos para generar una factura.
 *
 * @param orderId identificador del pedido facturado
 */
public record CreateInvoiceRequest(

        @NotBlank(
                message = "Order identifier must not be empty."
        )
        String orderId
) {
}