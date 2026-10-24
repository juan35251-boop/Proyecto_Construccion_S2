package application.adapters.output.persistence.jpa.adapters;

import application.adapters.output.persistence.jpa.entities.OrderJpaEntity;
import application.adapters.output.persistence.jpa.entities.ReturnJpaEntity;
import application.adapters.output.persistence.jpa.mappers.ReturnJpaMapper;
import application.adapters.output.persistence.jpa.repositories.SpringDataOrderRepository;
import application.adapters.output.persistence.jpa.repositories.SpringDataReturnRepository;
import application.domain.models.Order;
import application.domain.models.Return;
import application.ports.output.ReturnQueryRepository;
import application.ports.output.ReturnRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA encargado de almacenar y consultar
 * solicitudes de devolución en MySQL.
 */
@Repository
@Transactional
public class JpaReturnRepositoryAdapter
        implements ReturnRepository,
        ReturnQueryRepository {

    private final SpringDataReturnRepository
            springDataReturnRepository;

    private final SpringDataOrderRepository
            springDataOrderRepository;

    private final ReturnJpaMapper returnJpaMapper;

    /**
     * Construye el adaptador con sus dependencias.
     *
     * @param springDataReturnRepository repositorio JPA de devoluciones
     * @param springDataOrderRepository repositorio JPA de órdenes
     * @param returnJpaMapper mapper de devoluciones
     */
    public JpaReturnRepositoryAdapter(
            SpringDataReturnRepository
                    springDataReturnRepository,
            SpringDataOrderRepository
                    springDataOrderRepository,
            ReturnJpaMapper returnJpaMapper
    ) {
        validateDependencies(
                springDataReturnRepository,
                springDataOrderRepository,
                returnJpaMapper
        );

        this.springDataReturnRepository =
                springDataReturnRepository;

        this.springDataOrderRepository =
                springDataOrderRepository;

        this.returnJpaMapper = returnJpaMapper;
    }

    /**
     * Guarda una devolución nueva o actualiza
     * una devolución existente.
     *
     * La orden debe estar almacenada previamente.
     *
     * @param returnProcess devolución almacenada
     */
    @Override
    public void save(
            Return returnProcess
    ) {
        validateReturn(returnProcess);

        Order order = returnProcess.getOrder();

        validateOrder(order);

        String orderId = order.getOrderId();

        validateText(
                orderId,
                "Order identifier"
        );

        OrderJpaEntity orderEntity =
                springDataOrderRepository
                        .findByOrderId(orderId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Order must be persisted before requesting a return."
                                )
                        );

        Optional<ReturnJpaEntity> storedReturn =
                springDataReturnRepository
                        .findByReturnId(
                                returnProcess.getReturnId()
                        );

        ReturnJpaEntity returnEntity;

        if (storedReturn.isPresent()) {
            returnEntity = storedReturn.get();

            returnJpaMapper.updateEntity(
                    returnProcess,
                    orderEntity,
                    returnEntity
            );
        } else {
            validateOrderWithoutAnotherReturn(
                    orderId
            );

            returnEntity =
                    returnJpaMapper.toEntity(
                            returnProcess,
                            orderEntity
                    );
        }

        springDataReturnRepository.save(
                returnEntity
        );
    }

    /**
     * Busca una devolución mediante su identificador.
     *
     * @param returnId identificador de la devolución
     * @return devolución encontrada o resultado vacío
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Return> findById(
            String returnId
    ) {
        validateText(
                returnId,
                "Return identifier"
        );

        return springDataReturnRepository
                .findByReturnId(returnId)
                .map(returnJpaMapper::toDomain);
    }

    /**
     * Busca una devolución mediante el identificador
     * de su orden.
     *
     * @param orderId identificador de la orden
     * @return devolución encontrada o resultado vacío
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Return> findByOrderId(
            String orderId
    ) {
        validateText(
                orderId,
                "Order identifier"
        );

        return springDataReturnRepository
                .findByOrder_OrderId(orderId)
                .map(returnJpaMapper::toDomain);
    }

    /**
     * Comprueba si una orden ya tiene una devolución.
     *
     * @param orderId identificador de la orden
     * @return true si la devolución existe
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByOrderId(
            String orderId
    ) {
        validateText(
                orderId,
                "Order identifier"
        );

        return springDataReturnRepository
                .existsByOrder_OrderId(orderId);
    }

    /**
     * Obtiene todas las devoluciones almacenadas.
     *
     * @return devoluciones registradas
     */
    @Override
    @Transactional(readOnly = true)
    public List<Return> findAll() {
        return springDataReturnRepository
                .findAll()
                .stream()
                .map(returnJpaMapper::toDomain)
                .toList();
    }

    /**
     * Impide registrar dos devoluciones
     * para una misma orden.
     */
    private void validateOrderWithoutAnotherReturn(
            String orderId
    ) {
        if (springDataReturnRepository
                .existsByOrder_OrderId(orderId)) {
            throw new IllegalStateException(
                    "Order already has a return request."
            );
        }
    }

    /**
     * Valida la devolución recibida.
     */
    private void validateReturn(
            Return returnProcess
    ) {
        if (returnProcess == null) {
            throw new IllegalArgumentException(
                    "Return must not be null."
            );
        }
    }

    /**
     * Valida la orden relacionada.
     */
    private void validateOrder(
            Order order
    ) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Return order must not be null."
            );
        }
    }

    /**
     * Valida un identificador obligatorio.
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
     * Valida las dependencias del adaptador.
     */
    private void validateDependencies(
            SpringDataReturnRepository returnRepository,
            SpringDataOrderRepository orderRepository,
            ReturnJpaMapper mapper
    ) {
        if (returnRepository == null) {
            throw new IllegalArgumentException(
                    "Spring Data return repository must not be null."
            );
        }

        if (orderRepository == null) {
            throw new IllegalArgumentException(
                    "Spring Data order repository must not be null."
            );
        }

        if (mapper == null) {
            throw new IllegalArgumentException(
                    "Return JPA mapper must not be null."
            );
        }
    }
}