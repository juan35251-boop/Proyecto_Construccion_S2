package application.adapters.output.persistence.mongodb.adapters;

import application.adapters.output.persistence.mongodb.documents.InventoryMovementMongoDocument;
import application.adapters.output.persistence.mongodb.repositories.SpringDataInventoryMovementMongoRepository;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.User;
import application.ports.output.InventoryMovementQueryRepository;
import application.ports.output.InventoryMovementRepository;
import application.ports.output.InventoryQueryRepository;
import application.ports.output.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

/**
 * Adaptador encargado de almacenar y consultar
 * movimientos de inventario en MongoDB.
 *
 * Los servicios utilizan los puertos de salida y no
 * dependen directamente de MongoRepository.
 */
@Repository
public class MongoInventoryMovementRepositoryAdapter
        implements InventoryMovementRepository,
        InventoryMovementQueryRepository {

    private final SpringDataInventoryMovementMongoRepository
            mongoRepository;

    private final InventoryQueryRepository
            inventoryQueryRepository;

    private final UserRepository userRepository;

    /**
     * Construye el adaptador con sus dependencias.
     *
     * @param mongoRepository repositorio técnico MongoDB
     * @param inventoryQueryRepository puerto de inventarios
     * @param userRepository puerto de usuarios
     */
    public MongoInventoryMovementRepositoryAdapter(
            SpringDataInventoryMovementMongoRepository mongoRepository,
            InventoryQueryRepository inventoryQueryRepository,
            UserRepository userRepository
    ) {
        validateRepositories(
                mongoRepository,
                inventoryQueryRepository,
                userRepository
        );

        this.mongoRepository = mongoRepository;
        this.inventoryQueryRepository =
                inventoryQueryRepository;
        this.userRepository = userRepository;
    }

    /**
     * Guarda un movimiento como documento MongoDB.
     *
     * @param movement movimiento que será almacenado
     */
    @Override
    public void save(
            InventoryMovement movement
    ) {
        validateMovement(movement);

        mongoRepository.save(
                toDocument(movement)
        );
    }

    /**
     * Obtiene todos los movimientos históricos.
     *
     * @return movimientos ordenados desde el más reciente
     */
    @Override
    public List<InventoryMovement> findAll() {
        return mongoRepository
                .findAll()
                .stream()
                .map(this::toDomain)
                .sorted(
                        Comparator.comparing(
                                InventoryMovement::getOccurredAt
                        ).reversed()
                )
                .toList();
    }

    /**
     * Obtiene los movimientos de un inventario.
     *
     * @param inventoryId identificador del inventario
     * @return movimientos encontrados
     */
    @Override
    public List<InventoryMovement> findByInventoryId(
            String inventoryId
    ) {
        validateIdentifier(
                inventoryId,
                "Inventory identifier"
        );

        return mongoRepository
                .findAllByInventoryIdOrderByOccurredAtDesc(
                        inventoryId
                )
                .stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * Obtiene los movimientos realizados
     * por un usuario.
     *
     * @param identification identificación del usuario
     * @return movimientos encontrados
     */
    @Override
    public List<InventoryMovement>
    findByPerformedByIdentification(
            String identification
    ) {
        validateIdentifier(
                identification,
                "User identification"
        );

        return mongoRepository
                .findAllByPerformedByIdentificationOrderByOccurredAtDesc(
                        identification
                )
                .stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * Convierte un movimiento del dominio
     * en un documento MongoDB.
     *
     * @param movement movimiento del dominio
     * @return documento MongoDB
     */
    private InventoryMovementMongoDocument toDocument(
            InventoryMovement movement
    ) {
        return new InventoryMovementMongoDocument(
                movement.getMovementId(),
                movement
                        .getInventory()
                        .getInventoryId(),
                movement
                        .getInventory()
                        .getProduct()
                        .getProductId(),
                movement
                        .getInventory()
                        .getWarehouse()
                        .getWarehouseId(),
                movement.getMovementType(),
                movement.getQuantity(),
                movement
                        .getPerformedBy()
                        .getIdentification(),
                movement
                        .getPerformedBy()
                        .getRole(),
                movement.getOccurredAt()
        );
    }

    /**
     * Reconstruye un movimiento histórico.
     *
     * El inventario y el usuario se recuperan mediante
     * sus respectivos puertos de salida.
     *
     * @param document documento almacenado
     * @return movimiento del dominio
     */
    private InventoryMovement toDomain(
            InventoryMovementMongoDocument document
    ) {
        Inventory inventory =
                inventoryQueryRepository
                        .findById(
                                document.getInventoryId()
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Inventory movement references an inventory that does not exist."
                                )
                        );

        User performedBy =
                userRepository
                        .findByIdentification(
                                document
                                        .getPerformedByIdentification()
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Inventory movement references a user that does not exist."
                                )
                        );

        return new InventoryMovement(
                document.getMovementId(),
                inventory,
                document.getMovementType(),
                document.getQuantity(),
                performedBy,
                document.getOccurredAt()
        );
    }

    /**
     * Valida que los repositorios requeridos existan.
     */
    private void validateRepositories(
            SpringDataInventoryMovementMongoRepository mongoRepository,
            InventoryQueryRepository inventoryQueryRepository,
            UserRepository userRepository
    ) {
        if (mongoRepository == null) {
            throw new IllegalArgumentException(
                    "Inventory movement MongoDB repository must not be null."
            );
        }

        if (inventoryQueryRepository == null) {
            throw new IllegalArgumentException(
                    "Inventory query repository must not be null."
            );
        }

        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository must not be null."
            );
        }
    }

    /**
     * Valida que exista un movimiento.
     *
     * @param movement movimiento que será validado
     */
    private void validateMovement(
            InventoryMovement movement
    ) {
        if (movement == null) {
            throw new IllegalArgumentException(
                    "Inventory movement must not be null."
            );
        }
    }

    /**
     * Valida un identificador obligatorio.
     *
     * @param identifier valor que será validado
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