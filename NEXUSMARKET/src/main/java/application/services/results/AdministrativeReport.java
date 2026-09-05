package application.services.results;

/**
 * Resultado consolidado de la operación del Marketplace.
 *
 * No es una entidad del dominio. Es un objeto de lectura utilizado
 * para presentar información administrativa y de supervisión.
 *
 * No contiene valores monetarios porque los modelos actuales no
 * definen precios ni montos.
 */
public record AdministrativeReport(
        int totalUsers,
        int activeUsers,
        int blockedUsers,
        int totalBuyers,
        int totalSellers,
        int totalWarehouses,
        int totalProducts,
        int publishedProducts,
        int totalInventoryUnits,
        int totalInventoryMovements,
        int totalOrders,
        int paidOrders,
        int dispatchedOrders,
        int deliveredOrders,
        int finalizedOrders,
        int totalInvoices,
        int totalShipments,
        int totalReturns,
        int totalRefunds
) {

    /**
     * Valida que ningún indicador sea negativo.
     */
    public AdministrativeReport {
        if (totalUsers < 0
                || activeUsers < 0
                || blockedUsers < 0
                || totalBuyers < 0
                || totalSellers < 0
                || totalWarehouses < 0
                || totalProducts < 0
                || publishedProducts < 0
                || totalInventoryUnits < 0
                || totalInventoryMovements < 0
                || totalOrders < 0
                || paidOrders < 0
                || dispatchedOrders < 0
                || deliveredOrders < 0
                || finalizedOrders < 0
                || totalInvoices < 0
                || totalShipments < 0
                || totalReturns < 0
                || totalRefunds < 0) {
            throw new IllegalArgumentException(
                    "Administrative report values must not be negative."
            );
        }
    }
}