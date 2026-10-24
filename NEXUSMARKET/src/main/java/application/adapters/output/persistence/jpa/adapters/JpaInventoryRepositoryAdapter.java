package application.adapters.output.persistence.jpa.adapters;

import application.adapters.output.persistence.jpa.entities.InventoryJpaEntity;
import application.adapters.output.persistence.jpa.mappers.InventoryJpaMapper;
import application.adapters.output.persistence.jpa.repositories.SpringDataInventoryRepository;
import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.Warehouse;
import application.ports.output.InventoryQueryRepository;
import application.ports.output.InventoryRepository;
import application.ports.output.ProductQueryRepository;
import application.ports.output.WarehouseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA encargado de almacenar y consultar
 * el estado actual de los inventarios en MySQL.
 *
 * Implementa los puertos de escritura y consulta sin
 * exponer entidades JPA a los servicios de aplicación.
 *
 * Los movimientos históricos no se almacenan mediante
 * este adaptador; serán gestionados mediante MongoDB.
 */
@Repository
public class JpaInventoryRepositoryAdapter
        implements InventoryRepository,
        InventoryQueryRepository {

    private final SpringDataInventoryRepository
            springDataRepository;

    private final ProductQueryRepository
            productQueryRepository;

    private final WarehouseRepository
            warehouseRepository;

    /**
     * Construye el adaptador con los repositorios requeridos.
     *
     * @param springDataRepository repositorio JPA de inventarios
     * @param productQueryRepository puerto para consultar productos
     * @param warehouseRepository puerto para consultar bodegas
     */
    public JpaInventoryRepositoryAdapter(
            SpringDataInventoryRepository springDataRepository,
            ProductQueryRepository productQueryRepository,
            WarehouseRepository warehouseRepository
    ) {
        validateRepositories(
                springDataRepository,
                productQueryRepository,
                warehouseRepository
        );

        this.springDataRepository =
                springDataRepository;

        this.productQueryRepository =
                productQueryRepository;

        this.warehouseRepository =
                warehouseRepository;
    }

    /**
     * Guarda o actualiza el estado de un inventario.
     *
     * Si inventoryId ya existe, se conserva la clave
     * técnica Long generada por MySQL.
     *
     * @param inventory inventario que será almacenado
     */
    @Override
    public void save(Inventory inventory) {
        validateInventory(inventory);

        InventoryJpaEntity entity =
                InventoryJpaMapper.toEntity(inventory);

        springDataRepository
                .findByInventoryId(
                        inventory.getInventoryId()
                )
                .ifPresent(existingEntity ->
                        entity.setId(
                                existingEntity.getId()
                        )
                );

        springDataRepository.save(entity);
    }

    /**
     * Obtiene todos los inventarios almacenados.
     *
     * Cada entidad se reconstruye utilizando el producto
     * y la bodega relacionados.
     *
     * @return lista de inventarios del dominio
     */
    @Override
    public List<Inventory> findAll() {
        return springDataRepository
                .findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * Busca un inventario mediante inventoryId.
     *
     * @param inventoryId identificador consultado
     * @return inventario encontrado, si existe
     */
    @Override
    public Optional<Inventory> findById(
            String inventoryId
    ) {
        validateIdentifier(
                inventoryId,
                "Inventory identifier"
        );

        return springDataRepository
                .findByInventoryId(inventoryId)
                .map(this::toDomain);
    }

    /**
     * Obtiene los inventarios relacionados
     * con un producto.
     *
     * @param productId identificador del producto
     * @return inventarios encontrados
     */
    @Override
    public List<Inventory> findByProductId(
            String productId
    ) {
        validateIdentifier(
                productId,
                "Product identifier"
        );

        return springDataRepository
                .findAllByProductId(productId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * Obtiene los inventarios almacenados
     * en una bodega.
     *
     * @param warehouseId identificador de la bodega
     * @return inventarios encontrados
     */
    @Override
    public List<Inventory> findByWarehouseId(
            String warehouseId
    ) {
        validateIdentifier(
                warehouseId,
                "Warehouse identifier"
        );

        return springDataRepository
                .findAllByWarehouseId(warehouseId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * Convierte una entidad JPA en un inventario
     * perteneciente al dominio.
     *
     * @param entity entidad almacenada
     * @return inventario reconstruido
     */
    private Inventory toDomain(
            InventoryJpaEntity entity
    ) {
        Product product =
                productQueryRepository
                        .findById(entity.getProductId())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Inventory references a product that does not exist."
                                )
                        );

        Warehouse warehouse =
                warehouseRepository
                        .findById(entity.getWarehouseId())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Inventory references a warehouse that does not exist."
                                )
                        );

        return InventoryJpaMapper.toDomain(
                entity,
                product,
                warehouse
        );
    }

    /**
     * Valida los repositorios requeridos.
     *
     * @param springDataRepository repositorio JPA
     * @param productQueryRepository repositorio de productos
     * @param warehouseRepository repositorio de bodegas
     */
    private void validateRepositories(
            SpringDataInventoryRepository springDataRepository,
            ProductQueryRepository productQueryRepository,
            WarehouseRepository warehouseRepository
    ) {
        if (springDataRepository == null) {
            throw new IllegalArgumentException(
                    "Inventory JPA repository must not be null."
            );
        }

        if (productQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Product query repository must not be null."
            );
        }

        if (warehouseRepository == null) {
            throw new IllegalArgumentException(
                    "Warehouse repository must not be null."
            );
        }
    }

    /**
     * Valida que exista un inventario.
     *
     * @param inventory inventario que será validado
     */
    private void validateInventory(
            Inventory inventory
    ) {
        if (inventory == null) {
            throw new IllegalArgumentException(
                    "Inventory must not be null."
            );
        }
    }

    /**
     * Valida un identificador obligatorio.
     *
     * @param identifier identificador que será validado
     * @param fieldName nombre del campo
     */
    private void validateIdentifier(
            String identifier,
            String fieldName
    ) {
        if (identifier == null
                || identifier.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }
    }
}