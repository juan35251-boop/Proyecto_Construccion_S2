package application.domain.models;

import application.domain.valueobjects.OrderStatus;

import java.util.Objects;
import java.util.UUID;

/**
 * Representa una devolución solicitada por un comprador.
 *
 * Toda devolución debe estar asociada a una orden entregada
 * o finalizada. Solamente el comprador propietario de la orden
 * puede solicitarla.
 */
public class Return {

    /**
     * Identificador permanente de la devolución.
     */
    private final String returnId;

    /**
     * Orden sobre la cual se solicita la devolución.
     */
    private final Order order;

    /**
     * Comprador que solicita la devolución.
     */
    private final Buyer buyer;

    /**
     * Crea una devolución nueva y genera automáticamente
     * su identificador.
     *
     * @param order orden que será devuelta
     * @param buyer comprador solicitante
     */
    public Return(
            Order order,
            Buyer buyer
    ) {
        this(
                UUID.randomUUID().toString(),
                order,
                buyer
        );
    }

    /**
     * Reconstruye una devolución persistida.
     *
     * Este constructor conserva el identificador almacenado
     * previamente en la base de datos.
     *
     * @param returnId identificador permanente
     * @param order orden relacionada
     * @param buyer comprador solicitante
     */
    public Return(
            String returnId,
            Order order,
            Buyer buyer
    ) {
        validateReturnId(returnId);
        validateOrder(order);
        validateBuyer(buyer);
        validateOrderOwner(order, buyer);
        validateEligibleOrder(order);

        this.returnId = returnId.trim();
        this.order = order;
        this.buyer = buyer;
    }

    /**
     * Obtiene el identificador de la devolución.
     *
     * @return identificador permanente
     */
    public String getReturnId() {
        return returnId;
    }

    /**
     * Obtiene la orden asociada.
     *
     * @return orden devuelta
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Obtiene el comprador solicitante.
     *
     * @return comprador de la devolución
     */
    public Buyer getBuyer() {
        return buyer;
    }

    /**
     * Determina si la devolución corresponde a una orden.
     *
     * La comparación se realiza mediante el identificador
     * permanente de la orden.
     *
     * @param comparedOrder orden comparada
     * @return true si corresponde a la misma orden
     */
    public boolean belongsTo(
            Order comparedOrder
    ) {
        return comparedOrder != null
                && order.getOrderId().equals(
                        comparedOrder.getOrderId()
                );
    }

    /**
     * Determina si la devolución fue solicitada
     * por el comprador indicado.
     *
     * La comparación utiliza la identificación del comprador,
     * por lo que funciona después de reconstruirlo desde JPA.
     *
     * @param comparedBuyer comprador comparado
     * @return true si es el comprador solicitante
     */
    public boolean wasRequestedBy(
            Buyer comparedBuyer
    ) {
        return comparedBuyer != null
                && buyer.getIdentification().equals(
                        comparedBuyer.getIdentification()
                );
    }

    /**
     * Valida el identificador de la devolución.
     */
    private void validateReturnId(
            String returnId
    ) {
        if (returnId == null
                || returnId.isBlank()) {
            throw new IllegalArgumentException(
                    "Return identifier must not be empty."
            );
        }
    }

    /**
     * Valida que exista una orden.
     */
    private void validateOrder(
            Order order
    ) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Return must be associated with an order."
            );
        }
    }

    /**
     * Valida que exista un comprador.
     */
    private void validateBuyer(
            Buyer buyer
    ) {
        if (buyer == null) {
            throw new IllegalArgumentException(
                    "Return must be requested by a buyer."
            );
        }
    }

    /**
     * Valida que el comprador sea propietario de la orden.
     *
     * La comparación utiliza identificaciones porque los objetos
     * recuperados desde MySQL pueden ser instancias diferentes.
     */
    private void validateOrderOwner(
            Order order,
            Buyer buyer
    ) {
        if (order.getBuyer() == null
                || !buyer.getIdentification().equals(
                        order.getBuyer().getIdentification()
                )) {
            throw new IllegalStateException(
                    "Buyer can only return their own order."
            );
        }
    }

    /**
     * Valida que la orden pueda ser devuelta.
     */
    private void validateEligibleOrder(
            Order order
    ) {
        boolean delivered =
                order.getStatus()
                        == OrderStatus.DELIVERED;

        boolean finalized =
                order.getStatus()
                        == OrderStatus.FINALIZED;

        if (!delivered && !finalized) {
            throw new IllegalStateException(
                    "Only delivered or finalized orders can be returned."
            );
        }
    }

    /**
     * Compara devoluciones por su identificador permanente.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Return returnProcess)) {
            return false;
        }

        return returnId.equals(
                returnProcess.returnId
        );
    }

    /**
     * Genera el código hash mediante el identificador.
     */
    @Override
    public int hashCode() {
        return Objects.hash(returnId);
    }
}