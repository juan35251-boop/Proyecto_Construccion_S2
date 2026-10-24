package application.ports.output;

import application.domain.models.Warehouse;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida que define las operaciones
 * necesarias para almacenar y consultar bodegas.
 *
 * Esta interfaz no depende de MySQL, JPA,
 * MongoDB ni ninguna tecnología específica.
 */
public interface WarehouseRepository {

    /**
     * Guarda una bodega.
     *
     * @param warehouse bodega que será almacenada
     */
    void save(Warehouse warehouse);

    /**
     * Comprueba si una bodega concreta está registrada.
     *
     * La identidad se determina mediante warehouseId.
     *
     * @param warehouse bodega consultada
     * @return true si la bodega existe
     */
    boolean exists(Warehouse warehouse);

    /**
     * Busca una bodega mediante su identificador.
     *
     * La implementación predeterminada permite mantener
     * compatibles los repositorios en memoria existentes.
     *
     * Los adaptadores de base de datos pueden sobrescribir
     * este método con una consulta más eficiente.
     *
     * @param warehouseId identificador de la bodega
     * @return bodega encontrada, si existe
     */
    default Optional<Warehouse> findById(
            String warehouseId
    ) {
        if (warehouseId == null
                || warehouseId.isBlank()) {
            return Optional.empty();
        }

        return findAll()
                .stream()
                .filter(warehouse ->
                        warehouseId.equals(
                                warehouse.getWarehouseId()
                        )
                )
                .findFirst();
    }

    /**
     * Obtiene todas las bodegas registradas.
     *
     * @return lista de bodegas
     */
    List<Warehouse> findAll();
}