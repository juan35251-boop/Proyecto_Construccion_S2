package application.adapters.output.persistence.jpa.repositories;

import application.adapters.output.persistence.jpa.entities.WarehouseJpaEntity;
import application.domain.valueobjects.WarehouseOwnerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio técnico de Spring Data utilizado
 * para acceder a las bodegas almacenadas en MySQL.
 *
 * Esta interfaz solamente debe utilizarse dentro
 * del adaptador de persistencia JPA.
 */
public interface SpringDataWarehouseRepository
        extends JpaRepository<WarehouseJpaEntity, Long> {

    /**
     * Comprueba si existe una bodega con el identificador
     * permanente indicado.
     *
     * @param warehouseId identificador de dominio
     * @return true si la bodega existe
     */
    boolean existsByWarehouseId(
            String warehouseId
    );

    /**
     * Busca una bodega mediante su identificador
     * permanente de dominio.
     *
     * @param warehouseId identificador consultado
     * @return entidad encontrada, si existe
     */
    Optional<WarehouseJpaEntity> findByWarehouseId(
            String warehouseId
    );

    /**
     * Comprueba si existe alguna bodega de un determinado tipo.
     *
     * Este método se conserva porque puede ser útil para consultas
     * administrativas y mantiene compatibilidad con pruebas previas.
     *
     * No debe utilizarse para identificar una bodega concreta,
     * porque pueden existir muchas bodegas del mismo tipo.
     *
     * @param ownerType tipo de propietario
     * @return true si existe al menos una bodega de ese tipo
     */
    boolean existsByOwnerType(
            WarehouseOwnerType ownerType
    );
}