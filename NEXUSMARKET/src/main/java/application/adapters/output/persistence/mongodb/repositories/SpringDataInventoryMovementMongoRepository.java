package application.adapters.output.persistence.mongodb.repositories;

import application.adapters.output.persistence.mongodb.documents.InventoryMovementMongoDocument;
import application.domain.valueobjects.InventoryMovementType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repositorio técnico de Spring Data MongoDB
 * para movimientos de inventario.
 *
 * Este repositorio solamente debe utilizarse dentro
 * del adaptador de persistencia MongoDB.
 */
public interface SpringDataInventoryMovementMongoRepository
        extends MongoRepository<
                InventoryMovementMongoDocument,
                String
        > {

    /**
     * Obtiene los movimientos de un inventario
     * ordenados desde el más reciente.
     *
     * @param inventoryId identificador del inventario
     * @return movimientos encontrados
     */
    List<InventoryMovementMongoDocument>
    findAllByInventoryIdOrderByOccurredAtDesc(
            String inventoryId
    );

    /**
     * Obtiene los movimientos realizados
     * por un usuario.
     *
     * @param identification identificación del usuario
     * @return movimientos encontrados
     */
    List<InventoryMovementMongoDocument>
    findAllByPerformedByIdentificationOrderByOccurredAtDesc(
            String identification
    );

    /**
     * Obtiene los movimientos de un tipo específico.
     *
     * @param movementType tipo de movimiento
     * @return movimientos encontrados
     */
    List<InventoryMovementMongoDocument>
    findAllByMovementTypeOrderByOccurredAtDesc(
            InventoryMovementType movementType
    );
}