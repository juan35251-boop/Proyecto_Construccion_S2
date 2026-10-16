package application.adapters.input.rest.controllers;

import application.adapters.input.rest.dto.RegisterSellerRequest;
import application.adapters.input.rest.dto.SellerResponse;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseOwnerType;
import application.ports.input.RegisterSellerUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sellers")
public class SellerController {

    private final RegisterSellerUseCase
            registerSellerUseCase;

    public SellerController(
            RegisterSellerUseCase registerSellerUseCase
    ) {
        if (registerSellerUseCase == null) {
            throw new IllegalArgumentException(
                    "Register seller use case must not be null."
            );
        }

        this.registerSellerUseCase =
                registerSellerUseCase;
    }

    @PostMapping
    public ResponseEntity<SellerResponse> register(
            @RequestHeader(
                    "X-Requester-Identification"
            )
            String administratorIdentification,

            @Valid
            @RequestBody
            RegisterSellerRequest request
    ) {
        Warehouse firstWarehouse =
                new Warehouse(
                        WarehouseOwnerType.SELLER
                );

        Seller seller =
                registerSellerUseCase.register(
                        administratorIdentification,
                        request.identification(),
                        request.fullName(),
                        request.email(),
                        request.status(),
                        firstWarehouse
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        SellerResponse.from(seller)
                );
    }
}