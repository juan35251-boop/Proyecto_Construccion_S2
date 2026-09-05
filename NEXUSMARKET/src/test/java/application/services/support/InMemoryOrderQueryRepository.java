package application.services.support;

import application.domain.models.Order;
import application.ports.output.OrderQueryRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio en memoria para probar consultas de pedidos
 * sin utilizar una base de datos.
 */
public class InMemoryOrderQueryRepository
        implements OrderQueryRepository {

    private final List<Order> orders =
            new ArrayList<>();

    /**
     * Agrega un pedido al repositorio de prueba.
     *
     * @param order pedido que se desea almacenar
     */
    public void add(Order order) {
        orders.add(order);
    }

    /**
     * Devuelve una copia de todos los pedidos almacenados.
     */
    @Override
    public List<Order> findAll() {
        return List.copyOf(orders);
    }
}