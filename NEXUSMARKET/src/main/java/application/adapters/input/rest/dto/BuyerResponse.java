package application.adapters.input.rest.dto;

import application.domain.models.Buyer;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

import java.util.List;

/**
 * Información de un comprador que puede devolverse
 * mediante la API REST.
 *
 * Evita exponer directamente la entidad del dominio.
 */
public record BuyerResponse(
        String identification,
        String fullName,
        String email,
        SystemRole role,
        UserStatus status,
        String primaryAddress,
        List<String> additionalAddresses,
        BuyerStatus commercialStatus
) {

    /**
     * Convierte un comprador del dominio
     * en una respuesta de la API.
     *
     * @param buyer comprador registrado
     * @return respuesta REST
     */
    public static BuyerResponse from(Buyer buyer) {
        if (buyer == null) {
            throw new IllegalArgumentException(
                    "Buyer must not be null."
            );
        }

        return new BuyerResponse(
                buyer.getIdentification(),
                buyer.getFullName(),
                buyer.getEmail(),
                buyer.getRole(),
                buyer.getStatus(),
                buyer.getPrimaryAddress(),
                buyer.getAdditionalAddresses(),
                buyer.getCommercialStatus()
        );
    }
}