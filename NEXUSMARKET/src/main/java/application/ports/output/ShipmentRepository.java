package application.ports.output;

import application.domain.models.Shipment;

import java.util.Optional;

/**
 * Puerto de salida encargado de almacenar
 * y recuperar envíos.
 */
public interface ShipmentRepository {

    /**
     * Guarda o actualiza un envío.
     *
     * @param shipment envío que será almacenado
     */
    void save(Shipment shipment);

    /**
     * Busca un envío por su identificador.
     *
     * La implementación predeterminada permite conservar
     * compatibilidad con repositorios de prueba antiguos.
     *
     * @param shipmentId identificador del envío
     * @return envío encontrado o vacío
     */
    default Optional<Shipment> findById(
            String shipmentId
    ) {
        return Optional.empty();
    }

    /**
     * Busca el envío perteneciente a un pedido.
     *
     * @param orderId identificador del pedido
     * @return envío encontrado o vacío
     */
    default Optional<Shipment> findByOrderId(
            String orderId
    ) {
        return Optional.empty();
    }

    /**
     * Comprueba si ya existe un envío para un pedido.
     *
     * Un pedido físico solamente debe tener
     * un proceso de envío activo.
     *
     * @param orderId identificador del pedido
     * @return true si ya existe un envío
     */
    default boolean existsByOrderId(
            String orderId
    ) {
        return findByOrderId(orderId).isPresent();
    }
}