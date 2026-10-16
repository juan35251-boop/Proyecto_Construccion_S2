package application.adapters.input.rest.controllers;

import application.adapters.input.rest.dto.BuyerAddressRequest;
import application.adapters.input.rest.dto.BuyerResponse;
import application.adapters.input.rest.dto.RegisterBuyerRequest;
import application.domain.models.Buyer;
import application.ports.input.BuyerAddressUseCase;
import application.ports.input.BuyerQueryUseCase;
import application.ports.input.RegisterBuyerUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/buyers")
public class BuyerController {

    private final RegisterBuyerUseCase registerBuyerUseCase;
    private final BuyerQueryUseCase buyerQueryUseCase;
    private final BuyerAddressUseCase buyerAddressUseCase;

    public BuyerController(
            RegisterBuyerUseCase registerBuyerUseCase,
            BuyerQueryUseCase buyerQueryUseCase,
            BuyerAddressUseCase buyerAddressUseCase
    ) {
        if (registerBuyerUseCase == null) {
            throw new IllegalArgumentException(
                    "Register buyer use case must not be null."
            );
        }

        if (buyerQueryUseCase == null) {
            throw new IllegalArgumentException(
                    "Buyer query use case must not be null."
            );
        }

        if (buyerAddressUseCase == null) {
            throw new IllegalArgumentException(
                    "Buyer address use case must not be null."
            );
        }

        this.registerBuyerUseCase =
                registerBuyerUseCase;

        this.buyerQueryUseCase =
                buyerQueryUseCase;

        this.buyerAddressUseCase =
                buyerAddressUseCase;
    }

    @PostMapping
    public ResponseEntity<BuyerResponse> register(
            @Valid @RequestBody
            RegisterBuyerRequest request
    ) {
        Buyer buyer = registerBuyerUseCase.register(
                request.identification(),
                request.fullName(),
                request.email(),
                request.primaryAddress()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BuyerResponse.from(buyer));
    }

    @GetMapping("/{identification}")
    public ResponseEntity<BuyerResponse>
    findByIdentification(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String requestedByIdentification,
            @PathVariable String identification
    ) {
        Buyer buyer =
                buyerQueryUseCase.findByIdentification(
                        requestedByIdentification,
                        identification
                );

        return ResponseEntity.ok(
                BuyerResponse.from(buyer)
        );
    }

    @PatchMapping("/me/primary-address")
    public ResponseEntity<BuyerResponse>
    changePrimaryAddress(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String buyerIdentification,
            @Valid @RequestBody
            BuyerAddressRequest request
    ) {
        Buyer buyer =
                buyerAddressUseCase.changePrimaryAddress(
                        buyerIdentification,
                        request.address()
                );

        return ResponseEntity.ok(
                BuyerResponse.from(buyer)
        );
    }

    @PostMapping("/me/addresses")
    public ResponseEntity<BuyerResponse>
    addAdditionalAddress(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String buyerIdentification,
            @Valid @RequestBody
            BuyerAddressRequest request
    ) {
        Buyer buyer =
                buyerAddressUseCase.addAdditionalAddress(
                        buyerIdentification,
                        request.address()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BuyerResponse.from(buyer));
    }

    @DeleteMapping("/me/addresses")
    public ResponseEntity<Void>
    removeAdditionalAddress(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String buyerIdentification,
            @Valid @RequestBody
            BuyerAddressRequest request
    ) {
        boolean removed =
                buyerAddressUseCase.removeAdditionalAddress(
                        buyerIdentification,
                        request.address()
                );

        if (!removed) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}