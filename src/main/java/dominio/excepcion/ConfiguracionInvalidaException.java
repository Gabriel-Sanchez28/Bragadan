package dominio.excepcion;

/**
 * Lanzada cuando los datos de configuración de un producto o fábrica son incompletos o inválidos.
 */
public class ConfiguracionInvalidaException extends BragadanException {

    public ConfiguracionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
