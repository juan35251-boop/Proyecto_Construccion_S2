package application.domain.models;

public class Refund {

    private final Return returnProcess;
    private final Administrator processedBy;

    public Refund(
            Return returnProcess,
            Administrator processedBy
    ) {
        validateReturn(returnProcess);
        validateAdministrator(processedBy);

        this.returnProcess = returnProcess;
        this.processedBy = processedBy;
    }

    public Return getReturnProcess() {
        return returnProcess;
    }

    public Administrator getProcessedBy() {
        return processedBy;
    }

    public Buyer getBuyer() {
        return returnProcess.getBuyer();
    }

    public Order getOrder() {
        return returnProcess.getOrder();
    }

    public boolean belongsTo(Return returnProcess) {
        return this.returnProcess == returnProcess;
    }

    private void validateReturn(Return returnProcess) {
        if (returnProcess == null) {
            throw new IllegalArgumentException(
                    "Refund must be associated with a return."
            );
        }
    }

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