package application.services;

import application.domain.models.Buyer;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.input.ReturnQueryUseCase;
import application.ports.output.ReturnQueryRepository;

import java.util.List;

/**
 * Servicio encargado de consultar devoluciones.
 *
 * Implementa el puerto de entrada ReturnQueryUseCase,
 * permitiendo consultar devoluciones sin depender
 * directamente de esta implementación.
 *
 * El comprador solamente consulta sus devoluciones.
 * Los administradores y supervisores pueden consultarlas
 * globalmente para gestión y monitoreo.
 */
public class ReturnQueryService
        implements ReturnQueryUseCase {

    private final ReturnQueryRepository
            returnQueryRepository;

    /**
     * Construye el servicio con el repositorio requerido.
     *
     * @param returnQueryRepository repositorio
     *                              de devoluciones
     */
    public ReturnQueryService(
            ReturnQueryRepository returnQueryRepository
    ) {
        if (returnQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Return query repository must not be null."
            );
        }

        this.returnQueryRepository =
                returnQueryRepository;
    }

    /**
     * Obtiene las devoluciones visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return devoluciones autorizadas
     */
    @Override
    public List<Return> findAccessibleReturns(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        List<Return> returns =
                returnQueryRepository.findAll();

        /*
         * Administradores y supervisores tienen
         * acceso global de lectura.
         */
        if (hasGlobalAccess(requestedBy)) {
            return List.copyOf(returns);
        }

        /*
         * El comprador solamente puede consultar
         * las devoluciones que le pertenecen.
         */
        if (requestedBy instanceof Buyer buyer) {
            return returns.stream()
                    .filter(
                            returnProcess ->
                                    returnProcess.getBuyer()
                                            == buyer
                    )
                    .toList();
        }

        /*
         * Los vendedores, operadores logísticos
         * y demás roles no tienen acceso.
         */
        throw new IllegalStateException(
                "User is not authorized to query returns."
        );
    }

    /**
     * Valida que quien consulta sea un usuario activo.
     *
     * @param requestedBy usuario solicitante
     */
    private void validateActiveRequester(
            User requestedBy
    ) {
        if (requestedBy == null) {
            throw new IllegalArgumentException(
                    "Requesting user must not be null."
            );
        }

        if (!requestedBy.isActive()) {
            throw new IllegalStateException(
                    "Only active users can query returns."
            );
        }
    }

    /**
     * Indica si el usuario posee acceso global
     * de lectura sobre las devoluciones.
     *
     * @param user usuario que realiza la consulta
     * @return true si es administrador o supervisor
     */
    private boolean hasGlobalAccess(User user) {
        return user.getRole()
                == SystemRole.ADMINISTRATOR
                || user.getRole()
                == SystemRole.SUPERVISOR;
    }
}