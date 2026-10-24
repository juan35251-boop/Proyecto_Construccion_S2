package application.adapters.input.rest.controllers;

import application.adapters.input.rest.dto.RequestReturnRequest;
import application.adapters.input.rest.dto.ReturnResponse;
import application.domain.models.Return;
import application.ports.input.RequestReturnUseCase;
import application.ports.input.ReturnQueryUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST encargado de solicitar
 * y consultar devoluciones.
 *
 * Los compradores pueden solicitar y consultar
 * sus propias devoluciones.
 *
 * Los administradores y supervisores pueden consultar
 * todas las devoluciones registradas.
 */
@RestController
@RequestMapping("/api/v1/returns")
public class ReturnController {

    /**
     * Encabezado utilizado temporalmente para identificar
     * al usuario que realiza la solicitud.
     */
    private static final String REQUESTER_HEADER =
            "X-Requester-Identification";

    private final RequestReturnUseCase
            requestReturnUseCase;

    private final ReturnQueryUseCase
            returnQueryUseCase;

    /**
     * Construye el controlador con los casos
     * de uso requeridos.
     *
     * @param requestReturnUseCase caso de uso para
     *                             solicitar devoluciones
     * @param returnQueryUseCase caso de uso para
     *                           consultar devoluciones
     */
    public ReturnController(
            RequestReturnUseCase requestReturnUseCase,
            ReturnQueryUseCase returnQueryUseCase
    ) {
        validateDependency(
                requestReturnUseCase,
                "Request return use case"
        );

        validateDependency(
                returnQueryUseCase,
                "Return query use case"
        );

        this.requestReturnUseCase =
                requestReturnUseCase;

        this.returnQueryUseCase =
                returnQueryUseCase;
    }

    /**
     * Solicita la devolución de una orden.
     *
     * Solamente el comprador propietario de una orden
     * entregada o finalizada puede solicitarla.
     *
     * @param buyerIdentification identificación
     *                            del comprador
     * @param request información de la orden
     * @return devolución registrada
     */
    @PostMapping
    public ResponseEntity<ReturnResponse> requestReturn(
            @RequestHeader(REQUESTER_HEADER)
            String buyerIdentification,

            @Valid
            @RequestBody
            RequestReturnRequest request
    ) {
        Return returnProcess =
                requestReturnUseCase
                        .requestByIdentifiers(
                                buyerIdentification,
                                request.orderId()
                        );

        ReturnResponse response =
                ReturnResponse.from(
                        returnProcess
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Consulta las devoluciones visibles
     * para el usuario solicitante.
     *
     * Los compradores solamente pueden consultar
     * sus propias devoluciones.
     *
     * Los administradores y supervisores pueden
     * consultar todas las devoluciones.
     *
     * @param requesterIdentification identificación
     *                                del solicitante
     * @return devoluciones autorizadas
     */
    @GetMapping
    public ResponseEntity<List<ReturnResponse>>
    findAccessibleReturns(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification
    ) {
        List<ReturnResponse> returns =
                returnQueryUseCase
                        .findAccessibleReturnsByRequesterIdentification(
                                requesterIdentification
                        )
                        .stream()
                        .map(ReturnResponse::from)
                        .toList();

        return ResponseEntity.ok(returns);
    }

    /**
     * Consulta una devolución específica mediante
     * su identificador.
     *
     * @param requesterIdentification identificación
     *                                del solicitante
     * @param returnId identificador de la devolución
     * @return devolución autorizada
     */
    @GetMapping("/{returnId}")
    public ResponseEntity<ReturnResponse> findById(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification,

            @PathVariable
            String returnId
    ) {
        Return returnProcess =
                returnQueryUseCase
                        .findAccessibleReturnByIdentifiers(
                                requesterIdentification,
                                returnId
                        );

        return ResponseEntity.ok(
                ReturnResponse.from(
                        returnProcess
                )
        );
    }

    /**
     * Valida una dependencia obligatoria.
     *
     * @param dependency dependencia recibida
     * @param dependencyName nombre funcional
     */
    private void validateDependency(
            Object dependency,
            String dependencyName
    ) {
        if (dependency == null) {
            throw new IllegalArgumentException(
                    dependencyName + " must not be null."
            );
        }
    }
}