package dominio.excepcion;

/**
 * Excepción base para todas las excepciones del dominio de la cafetería Bragadan.
 */
public class BragadanException extends RuntimeException {

    public BragadanException(String mensaje) {
        super(mensaje);
    }

    public BragadanException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
