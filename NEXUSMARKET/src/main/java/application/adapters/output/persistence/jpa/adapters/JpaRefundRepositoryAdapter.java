package application.adapters.output.persistence.jpa.adapters;

import application.adapters.output.persistence.jpa.entities.RefundJpaEntity;
import application.adapters.output.persistence.jpa.entities.ReturnJpaEntity;
import application.adapters.output.persistence.jpa.mappers.RefundJpaMapper;
import application.adapters.output.persistence.jpa.repositories.SpringDataRefundRepository;
import application.adapters.output.persistence.jpa.repositories.SpringDataReturnRepository;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.ports.output.RefundQueryRepository;
import application.ports.output.RefundRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA encargado de almacenar
 * y consultar reembolsos en MySQL.
 */
@Repository
@Transactional
public class JpaRefundRepositoryAdapter
        implements RefundRepository,
        RefundQueryRepository {

    private final SpringDataRefundRepository
            springDataRefundRepository;

    private final SpringDataReturnRepository
            springDataReturnRepository;

    private final RefundJpaMapper refundJpaMapper;

    /**
     * Construye el adaptador con sus dependencias.
     *
     * @param springDataRefundRepository repositorio JPA de reembolsos
     * @param springDataReturnRepository repositorio JPA de devoluciones
     * @param refundJpaMapper mapper de reembolsos
     */
    public JpaRefundRepositoryAdapter(
            SpringDataRefundRepository
                    springDataRefundRepository,
            SpringDataReturnRepository
                    springDataReturnRepository,
            RefundJpaMapper refundJpaMapper
    ) {
        validateDependencies(
                springDataRefundRepository,
                springDataReturnRepository,
                refundJpaMapper
        );

        this.springDataRefundRepository =
                springDataRefundRepository;

        this.springDataReturnRepository =
                springDataReturnRepository;

        this.refundJpaMapper = refundJpaMapper;
    }

    /**
     * Guarda un reembolso nuevo o actualiza
     * uno existente.
     *
     * La devolución debe existir previamente
     * en MySQL.
     *
     * @param refund reembolso almacenado
     */
    @Override
    public void save(
            Refund refund
    ) {
        validateRefund(refund);

        Return returnProcess =
                refund.getReturnProcess();

        validateReturn(returnProcess);

        String returnId =
                returnProcess.getReturnId();

        validateText(
                returnId,
                "Return identifier"
        );

        ReturnJpaEntity returnEntity =
                springDataReturnRepository
                        .findByReturnId(returnId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Return must be persisted before processing a refund."
                                )
                        );

        Optional<RefundJpaEntity> storedRefund =
                springDataRefundRepository
                        .findByRefundId(
                                refund.getRefundId()
                        );

        RefundJpaEntity refundEntity;

        if (storedRefund.isPresent()) {
            refundEntity = storedRefund.get();

            refundJpaMapper.updateEntity(
                    refund,
                    returnEntity,
                    refundEntity
            );
        } else {
            validateReturnWithoutAnotherRefund(
                    returnId
            );

            refundEntity =
                    refundJpaMapper.toEntity(
                            refund,
                            returnEntity
                    );
        }

        springDataRefundRepository.save(
                refundEntity
        );
    }

    /**
     * Busca un reembolso mediante su identificador.
     *
     * @param refundId identificador del reembolso
     * @return reembolso encontrado o resultado vacío
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Refund> findById(
            String refundId
    ) {
        validateText(
                refundId,
                "Refund identifier"
        );

        return springDataRefundRepository
                .findByRefundId(refundId)
                .map(refundJpaMapper::toDomain);
    }

    /**
     * Busca el reembolso asociado
     * con una devolución.
     *
     * @param returnId identificador de la devolución
     * @return reembolso encontrado o resultado vacío
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Refund> findByReturnId(
            String returnId
    ) {
        validateText(
                returnId,
                "Return identifier"
        );

        return springDataRefundRepository
                .findByReturnProcess_ReturnId(
                        returnId
                )
                .map(refundJpaMapper::toDomain);
    }

    /**
     * Comprueba si una devolución ya
     * tiene un reembolso.
     *
     * @param returnId identificador de la devolución
     * @return true si ya existe
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByReturnId(
            String returnId
    ) {
        validateText(
                returnId,
                "Return identifier"
        );

        return springDataRefundRepository
                .existsByReturnProcess_ReturnId(
                        returnId
                );
    }

    /**
     * Obtiene todos los reembolsos.
     *
     * @return reembolsos registrados
     */
    @Override
    @Transactional(readOnly = true)
    public List<Refund> findAll() {
        return springDataRefundRepository
                .findAll()
                .stream()
                .map(refundJpaMapper::toDomain)
                .toList();
    }

    /**
     * Impide procesar dos reembolsos
     * para la misma devolución.
     */
    private void validateReturnWithoutAnotherRefund(
            String returnId
    ) {
        if (springDataRefundRepository
                .existsByReturnProcess_ReturnId(
                        returnId
                )) {
            throw new IllegalStateException(
                    "Return already has a refund."
            );
        }
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

    private void validateReturn(
            Return returnProcess
    ) {
        if (returnProcess == null) {
            throw new IllegalArgumentException(
                    "Refund return must not be null."
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

    private void validateDependencies(
            SpringDataRefundRepository refundRepository,
            SpringDataReturnRepository returnRepository,
            RefundJpaMapper mapper
    ) {
        if (refundRepository == null) {
            throw new IllegalArgumentException(
                    "Spring Data refund repository must not be null."
            );
        }

        if (returnRepository == null) {
            throw new IllegalArgumentException(
                    "Spring Data return repository must not be null."
            );
        }

        if (mapper == null) {
            throw new IllegalArgumentException(
                    "Refund JPA mapper must not be null."
            );
        }
    }
}