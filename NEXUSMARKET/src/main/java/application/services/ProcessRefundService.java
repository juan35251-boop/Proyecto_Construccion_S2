package application.services;

import application.domain.models.Administrator;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.User;
import application.ports.input.ProcessRefundUseCase;
import application.ports.output.RefundQueryRepository;
import application.ports.output.RefundRepository;
import application.ports.output.ReturnQueryRepository;
import application.ports.output.UserRepository;

/**
 * Servicio encargado de procesar reembolsos
 * asociados con devoluciones.
 */
public class ProcessRefundService
        implements ProcessRefundUseCase {

    private final RefundRepository refundRepository;

    private final RefundQueryRepository
            refundQueryRepository;

    private final ReturnQueryRepository
            returnQueryRepository;

    private final UserRepository userRepository;

    /**
     * Constructor conservado para mantener compatibilidad
     * con las pruebas unitarias existentes.
     *
     * @param refundRepository repositorio de reembolsos
     */
    public ProcessRefundService(
            RefundRepository refundRepository
    ) {
        validateRefundRepository(refundRepository);

        this.refundRepository = refundRepository;
        this.refundQueryRepository = null;
        this.returnQueryRepository = null;
        this.userRepository = null;
    }

    /**
     * Construye el servicio completo utilizado por la API.
     *
     * @param refundRepository repositorio de escritura
     * @param refundQueryRepository repositorio de consulta
     * @param returnQueryRepository repositorio de devoluciones
     * @param userRepository repositorio de usuarios
     */
    public ProcessRefundService(
            RefundRepository refundRepository,
            RefundQueryRepository refundQueryRepository,
            ReturnQueryRepository returnQueryRepository,
            UserRepository userRepository
    ) {
        validateRefundRepository(refundRepository);

        validateRefundQueryRepository(
                refundQueryRepository
        );

        validateReturnQueryRepository(
                returnQueryRepository
        );

        validateUserRepository(userRepository);

        this.refundRepository = refundRepository;

        this.refundQueryRepository =
                refundQueryRepository;

        this.returnQueryRepository =
                returnQueryRepository;

        this.userRepository = userRepository;
    }

    /**
     * Procesa un reembolso utilizando
     * los objetos del dominio.
     *
     * El modelo Refund comprueba que el administrador
     * exista y se encuentre activo.
     *
     * @param returnProcess devolución relacionada
     * @param administrator administrador responsable
     * @return reembolso procesado
     */
    @Override
    public Refund process(
            Return returnProcess,
            Administrator administrator
    ) {
        validateReturn(returnProcess);
        validateAdministrator(administrator);

        Refund refund =
                new Refund(
                        returnProcess,
                        administrator
                );

        refundRepository.save(refund);

        return refund;
    }

    /**
     * Procesa un reembolso utilizando identificadores.
     *
     * @param administratorIdentification administrador solicitante
     * @param returnId identificador de la devolución
     * @return reembolso procesado
     */
    @Override
    public Refund processByIdentifiers(
            String administratorIdentification,
            String returnId
    ) {
        validateText(
                administratorIdentification,
                "Administrator identification"
        );

        validateText(
                returnId,
                "Return identifier"
        );

        validateIdentifierRepositories();

        User user =
                userRepository
                        .findByIdentification(
                                administratorIdentification
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Administrator was not found."
                                )
                        );

        if (!(user
                instanceof Administrator administrator)) {
            throw new IllegalStateException(
                    "Only an administrator can process refunds."
            );
        }

        Return returnProcess =
                returnQueryRepository
                        .findById(returnId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Return was not found."
                                )
                        );

        if (refundQueryRepository
                .existsByReturnId(returnId)) {
            throw new IllegalStateException(
                    "Return already has a refund."
            );
        }

        return process(
                returnProcess,
                administrator
        );
    }

    /**
     * Valida que exista una devolución.
     */
    private void validateReturn(
            Return returnProcess
    ) {
        if (returnProcess == null) {
            throw new IllegalArgumentException(
                    "Refund must be associated with a return."
            );
        }
    }

    /**
     * Valida que exista un administrador.
     *
     * La validación de su estado también se ejecuta
     * dentro del modelo Refund.
     */
    private void validateAdministrator(
            Administrator administrator
    ) {
        if (administrator == null) {
            throw new IllegalArgumentException(
                    "Refund must be processed by an administrator."
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
     * Comprueba que estén disponibles los repositorios
     * requeridos por las operaciones con identificadores.
     */
    private void validateIdentifierRepositories() {
        if (refundQueryRepository == null
                || returnQueryRepository == null
                || userRepository == null) {
            throw new IllegalStateException(
                    "Repositories required for identifier-based refund processing are not configured."
            );
        }
    }

    private void validateRefundRepository(
            RefundRepository repository
    ) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Refund repository must not be null."
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