package application.services.support;

import application.domain.models.Shipment;
import application.ports.output.ShipmentQueryRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio en memoria para probar consultas de envíos.
 */
public class InMemoryShipmentQueryRepository
        implements ShipmentQueryRepository {

    private final List<Shipment> shipments =
            new ArrayList<>();

    /**
     * Agrega un envío al repositorio de prueba.
     */
    public void add(Shipment shipment) {
        shipments.add(shipment);
    }

    /**
     * Devuelve una copia de los envíos almacenados.
     */
    @Override
    public List<Shipment> findAll() {
        return List.copyOf(shipments);
    }
}