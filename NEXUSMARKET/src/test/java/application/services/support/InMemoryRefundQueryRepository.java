package application.services.support;

import application.domain.models.Refund;
import application.ports.output.RefundQueryRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio en memoria para probar consultas de reembolsos.
 */
public class InMemoryRefundQueryRepository
        implements RefundQueryRepository {

    private final List<Refund> refunds =
            new ArrayList<>();

    /**
     * Agrega un reembolso al repositorio de prueba.
     */
    public void add(Refund refund) {
        refunds.add(refund);
    }

    /**
     * Devuelve una copia de los reembolsos almacenados.
     */
    @Override
    public List<Refund> findAll() {
        return List.copyOf(refunds);
    }
}