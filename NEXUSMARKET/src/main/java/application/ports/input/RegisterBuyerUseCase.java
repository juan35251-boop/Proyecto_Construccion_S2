package application.ports.input;

import application.domain.models.Buyer;

/**
 * Puerto de entrada que define el caso de uso
 * para registrar nuevos compradores.
 *
 * Este contrato representa una operación que NexusMarket
 * ofrece al exterior, sin depender de controladores,
 * HTTP, bases de datos o tecnologías específicas.
 */
public interface RegisterBuyerUseCase {

    /**
     * Registra un nuevo comprador en NexusMarket.
     *
     * El comprador comienza con estado general activo
     * y estado comercial activo.
     *
     * La identificación y el correo electrónico deben
     * ser únicos en toda la plataforma.
     *
     * @param identification identificación del comprador
     * @param fullName nombre completo del comprador
     * @param email correo electrónico del comprador
     * @param primaryAddress dirección principal de entrega
     * @return comprador registrado
     *
     * @throws IllegalArgumentException si algún dato
     *         obligatorio es nulo o está vacío
     * @throws IllegalStateException si la identificación
     *         o el correo ya están registrados
     */
    Buyer register(
            String identification,
            String fullName,
            String email,
            String primaryAddress
    );
}