package application.ports.output;

import application.domain.models.Warehouse;

import java.util.List;

/**
 * Puerto de salida para almacenar y consultar bodegas.
 *
 * La capa de aplicación depende de esta interfaz y no conoce
 * la tecnología que se utilizará posteriormente para guardar
 * la información.
 */
public interface WarehouseRepository {

    /**
     * Guarda una bodega.
     *
     * @param warehouse bodega que se desea guardar
     */
    void save(Warehouse warehouse);

    /**
     * Comprueba si una bodega ya está registrada.
     *
     * Actualmente la comprobación se realiza usando la instancia
     * de la bodega porque el modelo todavía no tiene un identificador.
     *
     * @param warehouse bodega que se desea comprobar
     * @return true si la bodega ya existe
     */
    boolean exists(Warehouse warehouse);

    /**
     * Obtiene todas las bodegas registradas.
     *
     * @return lista de bodegas
     */
    List<Warehouse> findAll();
}