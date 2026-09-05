package application.services;

import application.domain.models.Buyer;
import application.domain.models.Refund;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.output.RefundQueryRepository;

import java.util.List;

/**
 * Servicio encargado de consultar reembolsos.
 *
 * Permisos:
 *
 * - El comprador consulta sus propios reembolsos.
 * - El administrador consulta todos para gestionarlos.
 * - El supervisor consulta todos para monitoreo.
 * - Los demás roles no tienen acceso financiero.
 */
public class RefundQueryService {

    private final RefundQueryRepository refundQueryRepository;

    /**
     * Construye el servicio con el repositorio requerido.
     *
     * @param refundQueryRepository repositorio de reembolsos
     */
    public RefundQueryService(
            RefundQueryRepository refundQueryRepository
    ) {
        if (refundQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Refund query repository must not be null."
            );
        }

        this.refundQueryRepository = refundQueryRepository;
    }

    /**
     * Obtiene los reembolsos visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return reembolsos autorizados
     */
    public List<Refund> findAccessibleRefunds(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        List<Refund> refunds =
                refundQueryRepository.findAll();

        if (hasGlobalAccess(requestedBy)) {
            return List.copyOf(refunds);
        }

        if (requestedBy instanceof Buyer buyer) {
            return refunds.stream()
                    .filter(refund -> refund.getBuyer() == buyer)
                    .toList();
        }

        throw new IllegalStateException(
                "User is not authorized to query refunds."
        );
    }

    /**
     * Valida que quien consulta sea un usuario activo.
     */
    private void validateActiveRequester(User requestedBy) {
        if (requestedBy == null) {
            throw new IllegalArgumentException(
                    "Requesting user must not be null."
            );
        }

        if (!requestedBy.isActive()) {
            throw new IllegalStateException(
                    "Only active users can query refunds."
            );
        }
    }

    /**
     * Administradores y supervisores tienen acceso global de lectura.
     */
    private boolean hasGlobalAccess(User user) {
        return user.getRole() == SystemRole.ADMINISTRATOR
                || user.getRole() == SystemRole.SUPERVISOR;
    }
}