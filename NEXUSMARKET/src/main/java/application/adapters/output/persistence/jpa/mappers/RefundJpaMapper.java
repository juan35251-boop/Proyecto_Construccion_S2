package application.adapters.output.persistence.jpa.mappers;

import application.adapters.output.persistence.jpa.entities.RefundJpaEntity;
import application.adapters.output.persistence.jpa.entities.ReturnJpaEntity;
import application.domain.models.Administrator;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.User;
import application.ports.output.UserRepository;
import org.springframework.stereotype.Component;

/**
 * Convierte reembolsos entre el dominio
 * y las entidades utilizadas por JPA.
 */
@Component
public class RefundJpaMapper {

    private final ReturnJpaMapper returnJpaMapper;

    private final UserRepository userRepository;

    /**
     * Construye el mapper con sus dependencias.
     *
     * @param returnJpaMapper mapper de devoluciones
     * @param userRepository repositorio utilizado para
     *                       recuperar al administrador
     */
    public RefundJpaMapper(
            ReturnJpaMapper returnJpaMapper,
            UserRepository userRepository
    ) {
        validateReturnJpaMapper(returnJpaMapper);
        validateUserRepository(userRepository);

        this.returnJpaMapper = returnJpaMapper;
        this.userRepository = userRepository;
    }

    /**
     * Convierte un reembolso del dominio
     * en una entidad JPA.
     *
     * @param refund reembolso convertido
     * @param returnEntity devolución persistida
     * @return entidad persistente
     */
    public RefundJpaEntity toEntity(
            Refund refund,
            ReturnJpaEntity returnEntity
    ) {
        validateRefund(refund);
        validateReturnEntity(returnEntity);

        return new RefundJpaEntity(
                refund.getRefundId(),
                returnEntity,
                refund
                        .getProcessedBy()
                        .getIdentification()
        );
    }

    /**
     * Actualiza una entidad persistente.
     *
     * @param refund reembolso actualizado
     * @param returnEntity devolución persistida
     * @param entity entidad actual
     */
    public void updateEntity(
            Refund refund,
            ReturnJpaEntity returnEntity,
            RefundJpaEntity entity
    ) {
        validateRefund(refund);
        validateReturnEntity(returnEntity);
        validateRefundEntity(entity);

        entity.changeRefundId(
                refund.getRefundId()
        );

        entity.changeReturnProcess(
                returnEntity
        );

        entity.changeProcessedByIdentification(
                refund
                        .getProcessedBy()
                        .getIdentification()
        );
    }

    /**
     * Reconstruye un reembolso del dominio.
     *
     * @param entity entidad persistida
     * @return reembolso reconstruido
     */
    public Refund toDomain(
            RefundJpaEntity entity
    ) {
        validateRefundEntity(entity);
        validateReturnEntity(
                entity.getReturnProcess()
        );

        Return returnProcess =
                returnJpaMapper.toDomain(
                        entity.getReturnProcess()
                );

        Administrator administrator =
                findAdministrator(
                        entity.getProcessedByIdentification()
                );

        return new Refund(
                entity.getRefundId(),
                returnProcess,
                administrator
        );
    }

    /**
     * Recupera y valida al administrador responsable.
     */
    private Administrator findAdministrator(
            String identification
    ) {
        User user =
                userRepository
                        .findByIdentification(
                                identification
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Refund administrator was not found."
                                )
                        );

        if (!(user
                instanceof Administrator administrator)) {
            throw new IllegalStateException(
                    "Refund must reference an administrator."
            );
        }

        return administrator;
    }

    private void validateRefund(
            Refund refund
    ) {
        if (refund == null) {
            throw new IllegalArgumentException(
                    "Refund must not be null."
            );
        }
    }

    private void validateRefundEntity(
            RefundJpaEntity entity
    ) {
        if (entity == null) {
            throw new IllegalArgumentException(
                    "Refund JPA entity must not be null."
            );
        }
    }

    private void validateReturnEntity(
            ReturnJpaEntity entity
    ) {
        if (entity == null) {
            throw new IllegalArgumentException(
                    "Refund return JPA entity must not be null."
            );
        }
    }

    private void validateReturnJpaMapper(
            ReturnJpaMapper mapper
    ) {
        if (mapper == null) {
            throw new IllegalArgumentException(
                    "Return JPA mapper must not be null."
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