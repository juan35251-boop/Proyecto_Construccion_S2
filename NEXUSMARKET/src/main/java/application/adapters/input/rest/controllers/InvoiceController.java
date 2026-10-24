package application.adapters.input.rest.controllers;

import application.adapters.input.rest.dto.CreateInvoiceRequest;
import application.adapters.input.rest.dto.InvoiceResponse;
import application.domain.models.Invoice;
import application.ports.input.GenerateInvoiceUseCase;
import application.ports.input.InvoiceQueryUseCase;
import application.ports.input.OrderQueryUseCase;
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
 * Controlador REST encargado de generar
 * y consultar facturas.
 *
 * La identificación del solicitante se recibe mediante
 * el encabezado X-Requester-Identification.
 */
@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private static final String REQUESTER_HEADER =
            "X-Requester-Identification";

    private final GenerateInvoiceUseCase
            generateInvoiceUseCase;

    private final InvoiceQueryUseCase
            invoiceQueryUseCase;

    private final OrderQueryUseCase
            orderQueryUseCase;

    /**
     * Construye el controlador con sus casos de uso.
     *
     * @param generateInvoiceUseCase generación de facturas
     * @param invoiceQueryUseCase consulta de facturas
     * @param orderQueryUseCase consulta autorizada de pedidos
     */
    public InvoiceController(
            GenerateInvoiceUseCase generateInvoiceUseCase,
            InvoiceQueryUseCase invoiceQueryUseCase,
            OrderQueryUseCase orderQueryUseCase
    ) {
        validateDependency(
                generateInvoiceUseCase,
                "Generate invoice use case"
        );

        validateDependency(
                invoiceQueryUseCase,
                "Invoice query use case"
        );

        validateDependency(
                orderQueryUseCase,
                "Order query use case"
        );

        this.generateInvoiceUseCase =
                generateInvoiceUseCase;

        this.invoiceQueryUseCase =
                invoiceQueryUseCase;

        this.orderQueryUseCase =
                orderQueryUseCase;
    }

    /**
     * Genera la factura de un pedido pagado.
     *
     * Antes de generarla, verifica que el solicitante
     * tenga acceso al pedido.
     *
     * @param requesterIdentification identificación del solicitante
     * @param request información del pedido
     * @return factura generada
     */
    @PostMapping
    public ResponseEntity<InvoiceResponse>
    generateInvoice(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification,

            @Valid
            @RequestBody
            CreateInvoiceRequest request
    ) {
        orderQueryUseCase
                .findAccessibleOrderByIdentifiers(
                        requesterIdentification,
                        request.orderId()
                );

        Invoice invoice =
                generateInvoiceUseCase
                        .generateByOrderId(
                                request.orderId()
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        InvoiceResponse.from(invoice)
                );
    }

    /**
     * Consulta las facturas visibles
     * para el usuario solicitante.
     *
     * @param requesterIdentification identificación del usuario
     * @return facturas autorizadas
     */
    @GetMapping
    public ResponseEntity<List<InvoiceResponse>>
    findAccessibleInvoices(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification
    ) {
        List<InvoiceResponse> invoices =
                invoiceQueryUseCase
                        .findAccessibleInvoicesByRequesterIdentification(
                                requesterIdentification
                        )
                        .stream()
                        .map(InvoiceResponse::from)
                        .toList();

        return ResponseEntity.ok(invoices);
    }

    /**
     * Consulta una factura específica.
     *
     * @param requesterIdentification identificación del usuario
     * @param invoiceId identificador de la factura
     * @return factura autorizada
     */
    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponse>
    findInvoiceById(
            @RequestHeader(REQUESTER_HEADER)
            String requesterIdentification,

            @PathVariable
            String invoiceId
    ) {
        Invoice invoice =
                invoiceQueryUseCase
                        .findAccessibleInvoiceByIdentifiers(
                                requesterIdentification,
                                invoiceId
                        );

        return ResponseEntity.ok(
                InvoiceResponse.from(invoice)
        );
    }

    /**
     * Valida una dependencia requerida.
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