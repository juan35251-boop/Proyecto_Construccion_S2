package application.ports.input;

import application.domain.models.User;
import application.services.results.AdministrativeReport;

/**
 * Puerto de entrada que define el caso de uso
 * para generar reportes administrativos.
 *
 * Los administradores pueden utilizarlos para gestionar
 * la operación y los supervisores para monitorearla.
 */
public interface AdministrativeReportUseCase {

    /**
     * Genera el reporte administrativo consolidado.
     *
     * @param requestedBy usuario que solicita el reporte
     * @return indicadores operativos de NexusMarket
     */
    AdministrativeReport generate(
            User requestedBy
    );
}