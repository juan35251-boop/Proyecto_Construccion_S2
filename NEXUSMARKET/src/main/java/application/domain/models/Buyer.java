package application.domain.models;

import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a un comprador dentro de NexusMarket.
 *
 * Un comprador es un usuario que puede registrar una dirección principal,
 * administrar direcciones adicionales y realizar compras cuando su estado
 * general y su estado comercial se encuentran activos.
 *
 * Hereda de {@link User} la información personal y el estado general
 * dentro del sistema.
 */
public class Buyer extends User {

    /**
     * Dirección principal utilizada por el comprador.
     */
    private String primaryAddress;

    /**
     * Lista de direcciones adicionales registradas por el comprador.
     *
     * La referencia de la lista no puede ser reemplazada después
     * de inicializarse.
     */
    private final List<String> additionalAddresses;

    /**
     * Estado comercial que determina si el comprador está autorizado
     * para realizar compras.
     */
    private BuyerStatus commercialStatus;

    /**
     * Crea un comprador con su información personal, dirección principal
     * y estado comercial.
     *
     * Los datos comunes del usuario se inicializan mediante el constructor
     * de la clase {@link User}. La dirección principal y el estado comercial
     * son validados antes de ser almacenados.
     *
     * @param identification identificación del comprador
     * @param fullName nombre completo del comprador
     * @param email correo electrónico del comprador
     * @param status estado general del comprador dentro del sistema
     * @param primaryAddress dirección principal del comprador
     * @param commercialStatus estado comercial del comprador
     *
     * @throws IllegalArgumentException si la dirección principal es nula
     *                                  o está vacía
     * @throws IllegalArgumentException si el estado comercial es nulo
     */
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

    /**
     * Obtiene el rol correspondiente al comprador.
     *
     * @return el rol {@link SystemRole#BUYER}
     */
    @Override
    public SystemRole getRole() {
        return SystemRole.BUYER;
    }

    /**
     * Obtiene la dirección principal del comprador.
     *
     * @return la dirección principal registrada
     */
    public String getPrimaryAddress() {
        return primaryAddress;
    }

    /**
     * Obtiene una copia no modificable de las direcciones adicionales.
     *
     * Se devuelve una copia para impedir que otros objetos modifiquen
     * directamente la lista interna del comprador.
     *
     * @return una copia de las direcciones adicionales registradas
     */
    public List<String> getAdditionalAddresses() {
        return List.copyOf(additionalAddresses);
    }

    /**
     * Obtiene el estado comercial actual del comprador.
     *
     * @return el estado comercial del comprador
     */
    public BuyerStatus getCommercialStatus() {
        return commercialStatus;
    }

    /**
     * Cambia la dirección principal del comprador.
     *
     * La nueva dirección se valida antes de reemplazar la dirección actual.
     *
     * @param newPrimaryAddress nueva dirección principal
     *
     * @throws IllegalArgumentException si la nueva dirección es nula
     *                                  o está vacía
     */
    public void changePrimaryAddress(String newPrimaryAddress) {
        validateAddress(newPrimaryAddress);
        this.primaryAddress = newPrimaryAddress;
    }

    /**
     * Agrega una dirección adicional al comprador.
     *
     * @param address dirección adicional que se desea registrar
     *
     * @throws IllegalArgumentException si la dirección es nula o está vacía
     */
    public void addAdditionalAddress(String address) {
        validateAddress(address);
        additionalAddresses.add(address);
    }

    /**
     * Elimina una dirección de la lista de direcciones adicionales.
     *
     * @param address dirección que se desea eliminar
     * @return {@code true} si la dirección existía y fue eliminada;
     *         {@code false} si no estaba registrada
     */
    public boolean removeAdditionalAddress(String address) {
        return additionalAddresses.remove(address);
    }

    /**
     * Cambia el estado comercial del comprador.
     *
     * @param newStatus nuevo estado comercial
     *
     * @throws IllegalArgumentException si el nuevo estado es nulo
     */
    public void changeCommercialStatus(BuyerStatus newStatus) {
        validateCommercialStatus(newStatus);
        this.commercialStatus = newStatus;
    }

    /**
     * Determina si el comprador está autorizado para realizar compras.
     *
     * Para comprar, su estado general de usuario debe estar activo y su
     * estado comercial debe ser {@link BuyerStatus#ACTIVE}.
     *
     * @return {@code true} si puede comprar; de lo contrario,
     *         {@code false}
     */
    public boolean canPurchase() {
        return isActive()
                && commercialStatus == BuyerStatus.ACTIVE;
    }

    /**
     * Valida que una dirección contenga información.
     *
     * @param address dirección que se desea validar
     *
     * @throws IllegalArgumentException si la dirección es nula,
     *                                  está vacía o solamente contiene espacios
     */
    private void validateAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException(
                    "Address must not be empty."
            );
        }
    }

    /**
     * Valida que el estado comercial del comprador no sea nulo.
     *
     * @param status estado comercial que se desea validar
     *
     * @throws IllegalArgumentException si el estado recibido es nulo
     */
    private void validateCommercialStatus(BuyerStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Buyer commercial status must not be null."
            );
        }
    }
}