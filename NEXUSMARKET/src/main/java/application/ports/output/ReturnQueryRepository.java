package application.ports.output;

import application.domain.models.Return;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para consultar devoluciones.
 *
 * No depende de JPA, MySQL ni otra tecnología
 * específica de persistencia.
 */
public interface ReturnQueryRepository {

    /**
     * Busca una devolución mediante su identificador.
     *
     * La implementación predeterminada mantiene compatibles
     * los repositorios en memoria utilizados en las pruebas.
     *
     * @param returnId identificador de la devolución
     * @return devolución encontrada, si existe
     */
    default Optional<Return> findById(
            String returnId
    ) {
        if (returnId == null
                || returnId.isBlank()) {
            return Optional.empty();
        }

        return findAll()
                .stream()
                .filter(returnProcess ->
                        returnId.equals(
                                returnProcess.getReturnId()
                        )
                )
                .findFirst();
    }

    /**
     * Busca la devolución asociada a una orden.
     *
     * @param orderId identificador de la orden
     * @return devolución encontrada, si existe
     */
    default Optional<Return> findByOrderId(
            String orderId
    ) {
        if (orderId == null
                || orderId.isBlank()) {
            return Optional.empty();
        }

        return findAll()
                .stream()
                .filter(returnProcess ->
                        returnProcess.getOrder() != null
                                && orderId.equals(
                                        returnProcess
                                                .getOrder()
                                                .getOrderId()
                                )
                )
                .findFirst();
    }

    /**
     * Comprueba si una orden ya tiene una devolución.
     *
     * @param orderId identificador de la orden
     * @return true si la devolución ya existe
     */
    default boolean existsByOrderId(
            String orderId
    ) {
        return findByOrderId(orderId).isPresent();
    }

    /**
     * Obtiene todas las devoluciones registradas.
     *
     * @return lista de devoluciones
     */
    List<Return> findAll();
}