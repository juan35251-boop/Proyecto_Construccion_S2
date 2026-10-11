package application.ports.input;

import application.domain.models.Administrator;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.valueobjects.UserStatus;

/**
 * Puerto de entrada que define el caso de uso
 * para registrar vendedores en NexusMarket.
 *
 * De acuerdo con las reglas del negocio, un vendedor
 * no puede registrarse por sí mismo. La operación debe
 * ser realizada por un administrador activo.
 */
public interface RegisterSellerUseCase {

    /**
     * Registra un nuevo vendedor y lo relaciona
     * con su primera bodega.
     *
     * La identificación y el correo electrónico del
     * vendedor deben ser únicos en toda la plataforma.
     *
     * @param administrator administrador que realiza el registro
     * @param identification identificación del vendedor
     * @param fullName nombre completo del vendedor
     * @param email correo electrónico del vendedor
     * @param status estado inicial del vendedor
     * @param firstWarehouse primera bodega del vendedor
     * @return vendedor registrado
     *
     * @throws IllegalArgumentException si alguno de los
     *         datos obligatorios es inválido
     * @throws IllegalStateException si el administrador
     *         no está activo o los datos ya están registrados
     */
    Seller register(
            Administrator administrator,
            String identification,
            String fullName,
            String email,
            UserStatus status,
            Warehouse firstWarehouse
    );
}