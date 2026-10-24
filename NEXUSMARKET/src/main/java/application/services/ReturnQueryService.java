package application.services;

import application.domain.models.Buyer;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.ReturnQueryUseCase;
import application.ports.output.ReturnQueryRepository;
import application.ports.output.UserRepository;

import java.util.List;

/**
 * Servicio encargado de consultar solicitudes
 * de devolución.
 */
public class ReturnQueryService
        implements ReturnQueryUseCase {

    private final ReturnQueryRepository
            returnQueryRepository;

    private final UserRepository userRepository;

    /**
     * Constructor conservado para las pruebas
     * que proporcionan directamente un usuario.
     *
     * @param returnQueryRepository repositorio de devoluciones
     */
    public ReturnQueryService(
            ReturnQueryRepository returnQueryRepository
    ) {
        validateReturnQueryRepository(
                returnQueryRepository
        );

        this.returnQueryRepository =
                returnQueryRepository;
        this.userRepository = null;
    }

    /**
     * Construye el servicio completo utilizado por la API.
     *
     * @param returnQueryRepository repositorio de devoluciones
     * @param userRepository repositorio de usuarios
     */
    public ReturnQueryService(
            ReturnQueryRepository returnQueryRepository,
            UserRepository userRepository
    ) {
        validateReturnQueryRepository(
                returnQueryRepository
        );

        validateUserRepository(userRepository);

        this.returnQueryRepository =
                returnQueryRepository;
        this.userRepository = userRepository;
    }

    /**
     * Obtiene las devoluciones visibles
     * para el usuario recibido.
     *
     * Los administradores y supervisores pueden consultar
     * todas las devoluciones. Los compradores solamente
     * pueden consultar las propias.
     */
    @Override
    public List<Return> findAccessibleReturns(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        List<Return> returns =
                returnQueryRepository.findAll();

        if (hasGlobalAccess(requestedBy)) {
            return List.copyOf(returns);
        }

        if (requestedBy instanceof Buyer buyer) {
            return returns.stream()
                    .filter(returnProcess ->
                            returnProcess
                                    .wasRequestedBy(buyer)
                    )
                    .toList();
        }

        throw new IllegalStateException(
                "User is not authorized to query returns."
        );
    }

    /**
     * Consulta devoluciones usando la identificación
     * del usuario solicitante.
     */
    @Override
    public List<Return>
    findAccessibleReturnsByRequesterIdentification(
            String requesterIdentification
    ) {
        User requester =
                findRequester(
                        requesterIdentification
                );

        return findAccessibleReturns(requester);
    }

    /**
     * Consulta una devolución específica.
     */
    @Override
    public Return findAccessibleReturnByIdentifiers(
            String requesterIdentification,
            String returnId
    ) {
        validateText(
                returnId,
                "Return identifier"
        );

        User requester =
                findRequester(
                        requesterIdentification
                );

        Return returnProcess =
                returnQueryRepository
                        .findById(returnId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Return was not found."
                                )
                        );

        validateActiveRequester(requester);

        if (hasGlobalAccess(requester)) {
            return returnProcess;
        }

        if (requester instanceof Buyer buyer
                && returnProcess.wasRequestedBy(buyer)) {
            return returnProcess;
        }

        throw new IllegalStateException(
                "User is not authorized to query this return."
        );
    }

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

    private boolean hasGlobalAccess(
            User user
    ) {
        return user.getRole()
                == SystemRole.ADMINISTRATOR
                || user.getRole()
                == SystemRole.SUPERVISOR;
    }

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
                    "Only active users can query returns."
            );
        }
    }

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

    private void validateUserRepositoryAvailable() {
        if (userRepository == null) {
            throw new IllegalStateException(
                    "User repository is required for identifier-based operations."
            );
        }
    }

    private void validateReturnQueryRepository(
            ReturnQueryRepository repository
    ) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Return query repository must not be null."
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