package application.services;

import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.output.AdministrativeReportQueryRepository;
import application.services.results.AdministrativeReport;

/**
 * Servicio encargado de generar el reporte administrativo.
 *
 * El administrador puede utilizarlo para gestionar la operación.
 * El supervisor puede consultarlo para realizar monitoreo.
 * Los demás participantes no tienen acceso global.
 */
public class AdministrativeReportService {

    private final AdministrativeReportQueryRepository reportRepository;

    /**
     * Construye el servicio con el puerto de reporte.
     *
     * @param reportRepository repositorio de consulta consolidada
     */
    public AdministrativeReportService(
            AdministrativeReportQueryRepository reportRepository
    ) {
        if (reportRepository == null) {
            throw new IllegalArgumentException(
                    "Administrative report repository must not be null."
            );
        }

        this.reportRepository = reportRepository;
    }

    /**
     * Genera el reporte administrativo.
     *
     * @param requestedBy usuario que solicita el reporte
     * @return indicadores operativos del Marketplace
     */
    public AdministrativeReport generate(
            User requestedBy
    ) {
        validateRequester(requestedBy);

        AdministrativeReport report =
                reportRepository.generateReport();

        if (report == null) {
            throw new IllegalStateException(
                    "Administrative report could not be generated."
            );
        }

        return report;
    }

    /**
     * Valida que el solicitante esté activo y tenga permisos globales.
     */
    private void validateRequester(User requestedBy) {
        if (requestedBy == null) {
            throw new IllegalArgumentException(
                    "Requesting user must not be null."
            );
        }

        if (!requestedBy.isActive()) {
            throw new IllegalStateException(
                    "Only active users can generate administrative reports."
            );
        }

        boolean isAdministrator =
                requestedBy.getRole()
                        == SystemRole.ADMINISTRATOR;

        boolean isSupervisor =
                requestedBy.getRole()
                        == SystemRole.SUPERVISOR;

        if (!isAdministrator && !isSupervisor) {
            throw new IllegalStateException(
                    "User is not authorized to generate administrative reports."
            );
        }
    }
}