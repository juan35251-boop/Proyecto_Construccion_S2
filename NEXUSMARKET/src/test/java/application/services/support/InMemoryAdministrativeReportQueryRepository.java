package application.services.support;

import application.ports.output.AdministrativeReportQueryRepository;
import application.services.results.AdministrativeReport;

/**
 * Repositorio en memoria utilizado para probar
 * la generación del reporte administrativo.
 */
public class InMemoryAdministrativeReportQueryRepository
        implements AdministrativeReportQueryRepository {

    private final AdministrativeReport report;
    private int generationCount;

    /**
     * Construye el repositorio con el reporte que devolverá.
     *
     * @param report reporte configurado para la prueba
     */
    public InMemoryAdministrativeReportQueryRepository(
            AdministrativeReport report
    ) {
        this.report = report;
    }

    /**
     * Devuelve el reporte configurado.
     */
    @Override
    public AdministrativeReport generateReport() {
        generationCount++;
        return report;
    }

    /**
     * Indica cuántas veces se solicitó el reporte.
     */
    public int getGenerationCount() {
        return generationCount;
    }
}