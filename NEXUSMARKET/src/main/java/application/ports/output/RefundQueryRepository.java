package application.ports.output;

import application.domain.models.Refund;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida utilizado para consultar reembolsos.
 *
 * No depende de JPA, MySQL ni otra tecnología
 * específica de persistencia.
 */
public interface RefundQueryRepository {

    /**
     * Busca un reembolso mediante su identificador.
     *
     * La implementación predeterminada conserva
     * la compatibilidad con repositorios en memoria.
     *
     * @param refundId identificador del reembolso
     * @return reembolso encontrado, si existe
     */
    default Optional<Refund> findById(
            String refundId
    ) {
        if (refundId == null
                || refundId.isBlank()) {
            return Optional.empty();
        }

        return findAll()
                .stream()
                .filter(refund ->
                        refundId.equals(
                                refund.getRefundId()
                        )
                )
                .findFirst();
    }

    /**
     * Busca el reembolso asociado
     * con una devolución.
     *
     * @param returnId identificador de la devolución
     * @return reembolso encontrado, si existe
     */
    default Optional<Refund> findByReturnId(
            String returnId
    ) {
        if (returnId == null
                || returnId.isBlank()) {
            return Optional.empty();
        }

        return findAll()
                .stream()
                .filter(refund ->
                        refund.getReturnProcess() != null
                                && returnId.equals(
                                        refund
                                                .getReturnProcess()
                                                .getReturnId()
                                )
                )
                .findFirst();
    }

    /**
     * Comprueba si una devolución ya fue reembolsada.
     *
     * @param returnId identificador de la devolución
     * @return true si ya existe un reembolso
     */
    default boolean existsByReturnId(
            String returnId
    ) {
        return findByReturnId(returnId).isPresent();
    }

    /**
     * Obtiene todos los reembolsos registrados.
     *
     * @return lista de reembolsos
     */
    List<Refund> findAll();
}