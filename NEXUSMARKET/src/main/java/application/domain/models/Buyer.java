package application.domain.models;

import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

import java.util.ArrayList;
import java.util.List;

public class Buyer extends User {

    private String primaryAddress;
    private final List<String> additionalAddresses;
    private BuyerStatus commercialStatus;

    public Buyer(
            String identification,
            String fullName,
            String email,
            UserStatus status,
            String primaryAddress,
            BuyerStatus commercialStatus
    ) {
        super(identification, fullName, email, status);

        validateAddress(primaryAddress);
        validateCommercialStatus(commercialStatus);

        this.primaryAddress = primaryAddress;
        this.commercialStatus = commercialStatus;
        this.additionalAddresses = new ArrayList<>();
    }

    @Override
    public SystemRole getRole() {
        return SystemRole.BUYER;
    }

    public String getPrimaryAddress() {
        return primaryAddress;
    }

    public List<String> getAdditionalAddresses() {
        return List.copyOf(additionalAddresses);
    }

    public BuyerStatus getCommercialStatus() {
        return commercialStatus;
    }

    public void changePrimaryAddress(String newPrimaryAddress) {
        validateAddress(newPrimaryAddress);
        this.primaryAddress = newPrimaryAddress;
    }

    public void addAdditionalAddress(String address) {
        validateAddress(address);
        additionalAddresses.add(address);
    }

    public boolean removeAdditionalAddress(String address) {
        return additionalAddresses.remove(address);
    }

    public void changeCommercialStatus(BuyerStatus newStatus) {
        validateCommercialStatus(newStatus);
        this.commercialStatus = newStatus;
    }

    public boolean canPurchase() {
        return isActive()
                && commercialStatus == BuyerStatus.ACTIVE;
    }

    private void validateAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException(
                    "Address must not be empty."
            );
        }
    }

    private void validateCommercialStatus(BuyerStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Buyer commercial status must not be null."
            );
        }
    }
}
