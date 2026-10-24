package application.adapters.input.rest.controllers;

import application.adapters.input.rest.dto.ProcessRefundRequest;
import application.adapters.input.rest.dto.RefundResponse;
import application.domain.models.Refund;
import application.ports.input.ProcessRefundUseCase;
import application.ports.input.RefundQueryUseCase;
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
 * Controlador REST encargado de procesar
 * y consultar reembolsos.
 *
 * Solamente un administrador activo puede procesar
 * un reembolso.
 */
@RestController
@RequestMapping("/api/v1/refunds")
public class RefundController {

    private static final String REQUESTER_HEADER =
            "X-Requester-Identification";

    private final ProcessRefundUseCase
            processRefundUseCase;

    private final RefundQueryUseCase
            refundQueryUseCase;

    /**
     * Construye el controlador con sus casos
     * de uso requeridos.
     *
     * @param processRefundUseCase procesamiento de reembolsos
     * @param refundQueryUseCase consulta de reembolsos
     */
    public RefundController(
            ProcessRefundUseCase processRefundUseCase,
            RefundQueryUseCase refundQueryUseCase
    ) {
        validateDependency(
                processRefundUseCase,
                "Process refund use case"
        );

        validateDependency(
                refundQueryUseCase,
                "Refund query use case"
        );

        this.processRefundUseCase =
                processRefundUseCase;

        this.refundQueryUseCase =
                refundQueryUseCase;
    }

    /**
     * Procesa el reembolso de una devolución.
     *
     * @param administratorIdentification administrador solicitante
     * @param request devolución que será reembolsada
     * @return reembolso procesado
     */
    @PostMapping
    public ResponseEntity<RefundResponse> processRefund(
            @RequestHeader(REQUESTER_HEADER)
            String administratorIdentification,

            @Valid
            @RequestBody
            ProcessRefundRequest request
    ) {
        Refund refund =
                processRefundUseCase
                        .processByIdentifiers(
                                administratorIdentification,
                                request.returnId()
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        RefundResponse.from(refund)
                );
    }

    /**
     * Consulta los reembolsos visibles
     * para el usuario solicitante.
     *
     * @param requesterIdentification identificación del usuario
     * @return reembolsos autorizados
     */
    @GetMapping
    public ResponseEntity<List<RefundResponse>>
    findAccessibleRefunds(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification
    ) {
        List<RefundResponse> refunds =
                refundQueryUseCase
                        .findAccessibleRefundsByRequesterIdentification(
                                requesterIdentification
                        )
                        .stream()
                        .map(RefundResponse::from)
                        .toList();

        return ResponseEntity.ok(refunds);
    }

    /**
     * Consulta un reembolso específico.
     *
     * @param requesterIdentification identificación del solicitante
     * @param refundId identificador del reembolso
     * @return reembolso autorizado
     */
    @GetMapping("/{refundId}")
    public ResponseEntity<RefundResponse> findById(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification,

            @PathVariable
            String refundId
    ) {
        Refund refund =
                refundQueryUseCase
                        .findAccessibleRefundByIdentifiers(
                                requesterIdentification,
                                refundId
                        );

        return ResponseEntity.ok(
                RefundResponse.from(refund)
        );
    }

    /**
     * Valida una dependencia obligatoria.
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