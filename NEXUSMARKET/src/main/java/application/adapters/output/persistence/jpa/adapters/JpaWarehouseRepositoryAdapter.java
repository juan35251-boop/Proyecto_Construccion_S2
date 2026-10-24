package application.adapters.output.persistence.jpa.adapters;

import application.adapters.output.persistence.jpa.entities.WarehouseJpaEntity;
import application.adapters.output.persistence.jpa.mappers.WarehouseJpaMapper;
import application.adapters.output.persistence.jpa.repositories.SpringDataWarehouseRepository;
import application.domain.models.Warehouse;
import application.ports.output.WarehouseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA encargado de implementar el puerto
 * de salida de bodegas utilizando MySQL.
 *
 * Los servicios de aplicación dependen únicamente
 * de WarehouseRepository y no conocen Spring Data.
 */
@Repository
public class JpaWarehouseRepositoryAdapter
        implements WarehouseRepository {

    private final SpringDataWarehouseRepository
            springDataRepository;

    /**
     * Construye el adaptador con el repositorio
     * técnico de Spring Data.
     *
     * @param springDataRepository repositorio JPA de bodegas
     */
    public JpaWarehouseRepositoryAdapter(
            SpringDataWarehouseRepository springDataRepository
    ) {
        validateRepository(springDataRepository);

        this.springDataRepository =
                springDataRepository;
    }

    /**
     * Guarda o actualiza una bodega en MySQL.
     *
     * Si warehouseId ya existe, se conserva la clave
     * técnica Long para actualizar la misma fila.
     *
     * @param warehouse bodega que será almacenada
     */
    @Override
    public void save(Warehouse warehouse) {
        validateWarehouse(warehouse);

        WarehouseJpaEntity entity =
                WarehouseJpaMapper.toEntity(warehouse);

        springDataRepository
                .findByWarehouseId(
                        warehouse.getWarehouseId()
                )
                .ifPresent(existingEntity ->
                        entity.setId(
                                existingEntity.getId()
                        )
                );

        springDataRepository.save(entity);
    }

    /**
     * Comprueba si una bodega concreta existe.
     *
     * @param warehouse bodega consultada
     * @return true si está registrada
     */
    @Override
    public boolean exists(Warehouse warehouse) {
        validateWarehouse(warehouse);

        return springDataRepository
                .existsByWarehouseId(
                        warehouse.getWarehouseId()
                );
    }

    /**
     * Busca una bodega por su identificador permanente.
     *
     * @param warehouseId identificador consultado
     * @return bodega encontrada, si existe
     */
    @Override
    public Optional<Warehouse> findById(
            String warehouseId
    ) {
        validateWarehouseId(warehouseId);

        return springDataRepository
                .findByWarehouseId(warehouseId)
                .map(WarehouseJpaMapper::toDomain);
    }

    /**
     * Obtiene todas las bodegas registradas.
     *
     * @return lista de bodegas del dominio
     */
    @Override
    public List<Warehouse> findAll() {
        return springDataRepository
                .findAll()
                .stream()
                .map(WarehouseJpaMapper::toDomain)
                .toList();
    }

    /**
     * Valida que el repositorio técnico exista.
     *
     * @param springDataRepository repositorio técnico
     */
    private void validateRepository(
            SpringDataWarehouseRepository springDataRepository
    ) {
        if (springDataRepository == null) {
            throw new IllegalArgumentException(
                    "Spring Data warehouse repository must not be null."
            );
        }
    }

    /**
     * Valida que exista una bodega.
     *
     * @param warehouse bodega que será validada
     */
    private void validateWarehouse(
            Warehouse warehouse
    ) {
        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "Warehouse must not be null."
            );
        }
    }

    /**
     * Valida el identificador de una bodega.
     *
     * @param warehouseId identificador que será validado
     */
    private void validateWarehouseId(
            String warehouseId
    ) {
        if (warehouseId == null
                || warehouseId.isBlank()) {
            throw new IllegalArgumentException(
                    "Warehouse identifier must not be empty."
            );
        }
    }
}