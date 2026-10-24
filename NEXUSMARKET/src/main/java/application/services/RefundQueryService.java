package application.services;

import application.domain.models.Buyer;
import application.domain.models.Refund;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.RefundQueryUseCase;
import application.ports.output.RefundQueryRepository;
import application.ports.output.UserRepository;

import java.util.List;

/**
 * Servicio encargado de consultar reembolsos.
 *
 * Los administradores y supervisores pueden consultar
 * todos los reembolsos.
 *
 * Los compradores solamente pueden consultar los
 * reembolsos asociados con sus propias devoluciones.
 */
public class RefundQueryService
        implements RefundQueryUseCase {

    private final RefundQueryRepository
            refundQueryRepository;

    private final UserRepository userRepository;

    /**
     * Constructor conservado para mantener compatibilidad
     * con las pruebas que proporcionan directamente
     * un usuario.
     *
     * @param refundQueryRepository repositorio de reembolsos
     */
    public RefundQueryService(
            RefundQueryRepository refundQueryRepository
    ) {
        validateRefundQueryRepository(
                refundQueryRepository
        );

        this.refundQueryRepository =
                refundQueryRepository;

        this.userRepository = null;
    }

    /**
     * Construye el servicio completo utilizado por la API.
     *
     * @param refundQueryRepository repositorio de reembolsos
     * @param userRepository repositorio de usuarios
     */
    public RefundQueryService(
            RefundQueryRepository refundQueryRepository,
            UserRepository userRepository
    ) {
        validateRefundQueryRepository(
                refundQueryRepository
        );

        validateUserRepository(userRepository);

        this.refundQueryRepository =
                refundQueryRepository;

        this.userRepository = userRepository;
    }

    /**
     * Obtiene los reembolsos visibles para
     * el usuario recibido.
     *
     * @param requestedBy usuario solicitante
     * @return reembolsos autorizados
     */
    @Override
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
                    .filter(refund ->
                            isAccessibleForBuyer(
                                    refund,
                                    buyer
                            )
                    )
                    .toList();
        }

        throw new IllegalStateException(
                "User is not authorized to query refunds."
        );
    }

    /**
     * Consulta reembolsos mediante la identificación
     * del solicitante.
     */
    @Override
    public List<Refund>
    findAccessibleRefundsByRequesterIdentification(
            String requesterIdentification
    ) {
        User requester =
                findRequester(
                        requesterIdentification
                );

        return findAccessibleRefunds(requester);
    }

    /**
     * Consulta un reembolso específico.
     */
    @Override
    public Refund findAccessibleRefundByIdentifiers(
            String requesterIdentification,
            String refundId
    ) {
        validateText(
                refundId,
                "Refund identifier"
        );

        User requester =
                findRequester(
                        requesterIdentification
                );

        Refund refund =
                refundQueryRepository
                        .findById(refundId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Refund was not found."
                                )
                        );

        validateActiveRequester(requester);

        if (hasGlobalAccess(requester)) {
            return refund;
        }

        if (requester instanceof Buyer buyer
                && isAccessibleForBuyer(
                        refund,
                        buyer
                )) {
            return refund;
        }

        throw new IllegalStateException(
                "User is not authorized to query this refund."
        );
    }

    /**
     * Comprueba si el reembolso pertenece
     * al comprador indicado.
     *
     * La comparación se realiza por identificación
     * y no por referencia de memoria.
     */
    private boolean isAccessibleForBuyer(
            Refund refund,
            Buyer buyer
    ) {
        return refund != null
                && refund.getBuyer() != null
                && buyer.getIdentification().equals(
                        refund
                                .getBuyer()
                                .getIdentification()
                );
    }

    /**
     * Localiza al usuario solicitante.
     */
    private User findRequester(
            String identification
    ) {
        validateText(
                identification,
                "Requester identification"
        );

        validateUserRepositoryAvailable();

        return userRepository
                .findByIdentification(identification)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Requesting user was not found."
                        )
                );
    }

    /**
     * Determina si el usuario tiene acceso global.
     */
    private boolean hasGlobalAccess(
            User user
    ) {
        return user.getRole()
                == SystemRole.ADMINISTRATOR
                || user.getRole()
                == SystemRole.SUPERVISOR;
    }

    /**
     * Valida que el solicitante exista
     * y esté activo.
     */
    private void validateActiveRequester(
            User requester
    ) {
        if (requester == null) {
            throw new IllegalArgumentException(
                    "Requesting user must not be null."
            );
        }

        if (!requester.isActive()) {
            throw new IllegalStateException(
                    "Only active users can query refunds."
            );
        }
    }

    /**
     * Valida un texto obligatorio.
     */
    private void validateText(
            String value,
            String fieldName
    ) {
        if (value == null
                || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }
    }

    /**
     * Comprueba que el repositorio de usuarios
     * esté disponible.
     */
    private void validateUserRepositoryAvailable() {
        if (userRepository == null) {
            throw new IllegalStateException(
                    "User repository is required for identifier-based operations."
            );
        }
    }

    private void validateRefundQueryRepository(
            RefundQueryRepository repository
    ) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Refund query repository must not be null."
            );
        }
    }

    private void validateUserRepository(
            UserRepository repository
    ) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }
    }
}