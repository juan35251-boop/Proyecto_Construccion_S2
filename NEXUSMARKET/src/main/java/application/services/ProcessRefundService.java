package application.services;

import application.domain.models.Administrator;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.ports.output.RefundRepository;

/**
 * Servicio de aplicación encargado de procesar reembolsos.
 *
 * El reembolso debe estar asociado con una devolución
 * y ser procesado por un administrador activo.
 */
public class ProcessRefundService {

    private final RefundRepository refundRepository;

    /**
     * Construye el servicio con el repositorio de reembolsos.
     *
     * @param refundRepository repositorio utilizado para guardar reembolsos
     */
    public ProcessRefundService(
            RefundRepository refundRepository
    ) {
        validateRepository(refundRepository);
        this.refundRepository = refundRepository;
    }

    /**
     * Procesa y almacena un reembolso.
     *
     * El modelo Refund comprueba que la devolución exista
     * y que el administrador esté activo.
     *
     * @param returnProcess devolución que origina el reembolso
     * @param administrator administrador que procesa el reembolso
     * @return reembolso procesado
     */
    public Refund process(
            Return returnProcess,
            Administrator administrator
    ) {
        Refund refund = new Refund(
                returnProcess,
                administrator
        );

        refundRepository.save(refund);

        return refund;
    }

    /**
     * Valida que el repositorio de reembolsos exista.
     */
    private void validateRepository(
            RefundRepository refundRepository
    ) {
        if (refundRepository == null) {
            throw new IllegalArgumentException(
                    "Refund repository must not be null."
            );
        }
    }
}
