package application.services.support;

import application.domain.models.Return;
import application.ports.output.ReturnQueryRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio en memoria para probar consultas de devoluciones.
 */
public class InMemoryReturnQueryRepository
        implements ReturnQueryRepository {

    private final List<Return> returns =
            new ArrayList<>();

    /**
     * Agrega una devolución al repositorio de prueba.
     */
    public void add(Return returnProcess) {
        returns.add(returnProcess);
    }

    /**
     * Devuelve una copia de las devoluciones almacenadas.
     */
    @Override
    public List<Return> findAll() {
        return List.copyOf(returns);
    }
}