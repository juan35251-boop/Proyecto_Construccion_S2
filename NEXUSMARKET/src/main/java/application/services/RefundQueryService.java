package application.services;

import application.domain.models.Buyer;
import application.domain.models.Refund;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.RefundQueryUseCase;
import application.ports.output.RefundQueryRepository;

import java.util.List;

/**
 * Servicio encargado de consultar reembolsos.
 *
 * Implementa el puerto de entrada RefundQueryUseCase,
 * permitiendo consultar reembolsos sin depender
 * directamente de esta implementación.
 *
 * Permisos:
 *
 * - El comprador consulta sus propios reembolsos.
 * - El administrador consulta todos para gestionarlos.
 * - El supervisor consulta todos para monitoreo.
 * - Los demás roles no tienen acceso financiero.
 */
public class RefundQueryService
        implements RefundQueryUseCase {

    private final RefundQueryRepository
            refundQueryRepository;

    /**
     * Construye el servicio con el repositorio requerido.
     *
     * @param refundQueryRepository repositorio
     *                              de reembolsos
     */
    public RefundQueryService(
            RefundQueryRepository refundQueryRepository
    ) {
        if (refundQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Refund query repository must not be null."
            );
        }

        this.refundQueryRepository =
                refundQueryRepository;
    }

    /**
     * Obtiene los reembolsos visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return reembolsos autorizados
     */
    @Override
    public List<Refund> findAccessibleRefunds(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        List<Refund> refunds =
                refundQueryRepository.findAll();

        /*
         * Administradores y supervisores tienen
         * acceso global a los reembolsos.
         */
        if (hasGlobalAccess(requestedBy)) {
            return List.copyOf(refunds);
        }

        /*
         * El comprador solamente puede consultar
         * los reembolsos que le pertenecen.
         */
        if (requestedBy instanceof Buyer buyer) {
            return refunds.stream()
                    .filter(
                            refund ->
                                    refund.getBuyer() == buyer
                    )
                    .toList();
        }

        /*
         * Los vendedores, operadores logísticos
         * y demás roles no tienen acceso financiero.
         */
        throw new IllegalStateException(
                "User is not authorized to query refunds."
        );
    }

    /**
     * Valida que quien consulta sea un usuario activo.
     *
     * @param requestedBy usuario solicitante
     */
    private void validateActiveRequester(
            User requestedBy
    ) {
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
     * Indica si el usuario posee acceso global
     * de lectura sobre los reembolsos.
     *
     * @param user usuario que realiza la consulta
     * @return true si es administrador o supervisor
     */
    private boolean hasGlobalAccess(User user) {
        return user.getRole()
                == SystemRole.ADMINISTRATOR
                || user.getRole()
                == SystemRole.SUPERVISOR;
    }
}