package application.services;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.Seller;
import application.domain.models.Supervisor;
import application.domain.models.Warehouse;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseOwnerType;
import application.services.results.AdministrativeReport;
import application.services.support.InMemoryAdministrativeReportQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas del servicio encargado de generar
 * reportes administrativos y operativos.
 */
class AdministrativeReportServiceTest {

    @Test
    @DisplayName("El administrador puede generar el reporte")
    void administratorShouldGenerateReport() {
        AdministrativeReport expectedReport =
                createReport();

        InMemoryAdministrativeReportQueryRepository repository =
                new InMemoryAdministrativeReportQueryRepository(
                        expectedReport
                );

        AdministrativeReportService service =
                new AdministrativeReportService(repository);

        AdministrativeReport result = service.generate(
                createAdministrator(UserStatus.ACTIVE)
        );

        assertEquals(expectedReport, result);
        assertEquals(1, repository.getGenerationCount());
        assertEquals(20, result.totalUsers());
        assertEquals(8, result.totalOrders());
        assertEquals(2, result.totalRefunds());
    }

    @Test
    @DisplayName("El supervisor puede generar el reporte")
    void supervisorShouldGenerateReport() {
        AdministrativeReport expectedReport =
                createReport();

        AdministrativeReportService service =
                new AdministrativeReportService(
                        new InMemoryAdministrativeReportQueryRepository(
                                expectedReport
                        )
                );

        AdministrativeReport result = service.generate(
                createSupervisor(UserStatus.ACTIVE)
        );

        assertEquals(expectedReport, result);
    }

    @Test
    @DisplayName("El comprador no puede generar reportes globales")
    void buyerShouldNotGenerateReport() {
        AdministrativeReportService service =
                new AdministrativeReportService(
                        new InMemoryAdministrativeReportQueryRepository(
                                createReport()
                        )
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.generate(createBuyer())
        );
    }

    @Test
    @DisplayName("El vendedor no puede generar reportes globales")
    void sellerShouldNotGenerateReport() {
        AdministrativeReportService service =
                new AdministrativeReportService(
                        new InMemoryAdministrativeReportQueryRepository(
                                createReport()
                        )
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.generate(createSeller())
        );
    }

    @Test
    @DisplayName("Debe rechazar un administrador inactivo")
    void shouldRejectInactiveAdministrator() {
        AdministrativeReportService service =
                new AdministrativeReportService(
                        new InMemoryAdministrativeReportQueryRepository(
                                createReport()
                        )
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.generate(
                        createAdministrator(
                                UserStatus.INACTIVE
                        )
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un usuario nulo")
    void shouldRejectNullUser() {
        AdministrativeReportService service =
                new AdministrativeReportService(
                        new InMemoryAdministrativeReportQueryRepository(
                                createReport()
                        )
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.generate(null)
        );
    }

    @Test
    @DisplayName("Debe rechazar un reporte nulo")
    void shouldRejectNullReport() {
        AdministrativeReportService service =
                new AdministrativeReportService(
                        new InMemoryAdministrativeReportQueryRepository(
                                null
                        )
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.generate(
                        createAdministrator(
                                UserStatus.ACTIVE
                        )
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar valores negativos en el reporte")
    void shouldRejectNegativeReportValues() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AdministrativeReport(
                        -1,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0
                )
        );
    }

    @Test
    @DisplayName("Debe rechazar un repositorio nulo")
    void shouldRejectNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AdministrativeReportService(null)
        );
    }

    /**
     * Crea un reporte válido con información de ejemplo.
     *
     * El orden coincide con los componentes definidos
     * en el record AdministrativeReport.
     */
    private AdministrativeReport createReport() {
        return new AdministrativeReport(
                20,  // Total de usuarios
                17,  // Usuarios activos
                2,   // Usuarios bloqueados
                8,   // Compradores
                5,   // Vendedores
                6,   // Bodegas
                15,  // Productos
                12,  // Productos publicados
                100, // Unidades disponibles
                25,  // Movimientos de inventario
                8,   // Pedidos
                2,   // Pedidos pagados
                1,   // Pedidos despachados
                2,   // Pedidos entregados
                3,   // Pedidos finalizados
                7,   // Facturas
                4,   // Envíos
                3,   // Devoluciones
                2    // Reembolsos
        );
    }

    private Administrator createAdministrator(
            UserStatus status
    ) {
        return new Administrator(
                "9001",
                "Administrator",
                "admin@email.com",
                status
        );
    }

    private Supervisor createSupervisor(
            UserStatus status
    ) {
        return new Supervisor(
                "8001",
                "Supervisor",
                "supervisor@email.com",
                status
        );
    }

    private Buyer createBuyer() {
        return new Buyer(
                "1001",
                "Buyer",
                "buyer@email.com",
                UserStatus.ACTIVE,
                "Main Street 10",
                BuyerStatus.ACTIVE
        );
    }

    private Seller createSeller() {
        return new Seller(
                "2001",
                "Seller",
                "seller@email.com",
                UserStatus.ACTIVE,
                new Warehouse(WarehouseOwnerType.SELLER)
        );
    }
}