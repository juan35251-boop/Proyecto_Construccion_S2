package application.ports.output;

import application.services.results.AdministrativeReport;

/**
 * Puerto de salida para obtener información consolidada.
 *
 * La futura implementación podrá calcular los indicadores
 * eficientemente mediante consultas a la base de datos.
 */
public interface AdministrativeReportQueryRepository {

    /**
     * Genera los indicadores administrativos del Marketplace.
     *
     * @return reporte administrativo consolidado
     */
    AdministrativeReport generateReport();
}