package application.domain.models;

/**
 * Representa un reembolso procesado a partir de una devolución.
 *
 * Todo reembolso debe estar asociado a un proceso de devolución válido
 * y debe ser gestionado por un administrador activo.
 *
 * Desde la devolución relacionada se puede consultar el comprador y la
 * orden que originaron el reembolso.
 */
public class Refund {

    /**
     * Proceso de devolución que origina el reembolso.
     */
    private final Return returnProcess;

    /**
     * Administrador responsable de procesar el reembolso.
     */
    private final Administrator processedBy;

    /**
     * Crea un reembolso asociado a una devolución.
     *
     * La devolución debe existir y el administrador encargado de procesarla
     * debe existir y encontrarse activo.
     *
     * @param returnProcess devolución que origina el reembolso
     * @param processedBy administrador responsable del proceso
     *
     * @throws IllegalArgumentException si la devolución es nula
     * @throws IllegalArgumentException si el administrador es nulo
     * @throws IllegalStateException si el administrador no está activo
     */
    public Refund(
            Return returnProcess,
            Administrator processedBy
    ) {
        validateReturn(returnProcess);
        validateAdministrator(processedBy);

        this.returnProcess = returnProcess;
        this.processedBy = processedBy;
    }

    /**
     * Obtiene la devolución asociada al reembolso.
     *
     * @return el proceso de devolución relacionado
     */
    public Return getReturnProcess() {
        return returnProcess;
    }

    /**
     * Obtiene el administrador que procesó el reembolso.
     *
     * @return el administrador responsable
     */
    public Administrator getProcessedBy() {
        return processedBy;
    }

    /**
     * Obtiene el comprador que solicitó la devolución.
     *
     * La información se consulta mediante el proceso de devolución.
     *
     * @return el comprador relacionado con el reembolso
     */
    public Buyer getBuyer() {
        return returnProcess.getBuyer();
    }

    /**
     * Obtiene la orden que originó la devolución y el reembolso.
     *
     * La información se consulta mediante el proceso de devolución.
     *
     * @return la orden relacionada con el reembolso
     */
    public Order getOrder() {
        return returnProcess.getOrder();
    }

    /**
     * Determina si el reembolso pertenece a la devolución recibida.
     *
     * La comparación se realiza por identidad, por lo que ambas referencias
     * deben apuntar a la misma instancia de {@link Return}.
     *
     * @param returnProcess devolución que se desea comparar
     * @return {@code true} si corresponde a la misma instancia;
     *         de lo contrario, {@code false}
     */
    public boolean belongsTo(Return returnProcess) {
        return this.returnProcess == returnProcess;
    }

    /**
     * Valida que el reembolso esté asociado a una devolución.
     *
     * @param returnProcess devolución que se desea validar
     *
     * @throws IllegalArgumentException si la devolución es nula
     */
    private void validateReturn(Return returnProcess) {
        if (returnProcess == null) {
            throw new IllegalArgumentException(
                    "Refund must be associated with a return."
            );
        }
    }

    /**
     * Valida que el reembolso sea procesado por un administrador activo.
     *
     * @param administrator administrador que se desea validar
     *
     * @throws IllegalArgumentException si el administrador es nulo
     * @throws IllegalStateException si el administrador no está activo
     */
    private void validateAdministrator(
            Administrator administrator
    ) {
        if (administrator == null) {
            throw new IllegalArgumentException(
                    "Refund must be processed by an administrator."
            );
        }

        if (!administrator.isActive()) {
            throw new IllegalStateException(
                    "Only an active administrator can process refunds."
            );
        }
    }
}