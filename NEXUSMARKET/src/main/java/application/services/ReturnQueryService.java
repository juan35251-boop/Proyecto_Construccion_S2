package application.services;

import application.domain.models.Buyer;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.ports.output.ReturnQueryRepository;

import java.util.List;

/**
 * Servicio encargado de consultar devoluciones.
 *
 * El comprador solamente consulta sus devoluciones.
 * Los administradores y supervisores pueden consultarlas
 * globalmente para gestión y monitoreo.
 */
public class ReturnQueryService {

    private final ReturnQueryRepository returnQueryRepository;

    /**
     * Construye el servicio con el repositorio requerido.
     *
     * @param returnQueryRepository repositorio de devoluciones
     */
    public ReturnQueryService(
            ReturnQueryRepository returnQueryRepository
    ) {
        if (returnQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Return query repository must not be null."
            );
        }

        this.returnQueryRepository = returnQueryRepository;
    }

    /**
     * Obtiene las devoluciones visibles para el usuario.
     *
     * @param requestedBy usuario que realiza la consulta
     * @return devoluciones autorizadas
     */
    public List<Return> findAccessibleReturns(
            User requestedBy
    ) {
        validateActiveRequester(requestedBy);

        List<Return> returns =
                returnQueryRepository.findAll();

        if (hasGlobalAccess(requestedBy)) {
            return List.copyOf(returns);
        }

        if (requestedBy instanceof Buyer buyer) {
            return returns.stream()
                    .filter(
                            returnProcess ->
                                    returnProcess.getBuyer() == buyer
                    )
                    .toList();
        }

        throw new IllegalStateException(
                "User is not authorized to query returns."
        );
    }

    /**
     * Valida que quien consulta sea un usuario activo.
     */
    private void validateActiveRequester(User requestedBy) {
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
     * Administradores y supervisores tienen acceso global de lectura.
     */
    private boolean hasGlobalAccess(User user) {
        return user.getRole() == SystemRole.ADMINISTRATOR
                || user.getRole() == SystemRole.SUPERVISOR;
    }
}