package dominio.excepcion;

/**
 * Lanzada cuando la construcción de un Combo no cumple las invariantes de negocio
 * (por ejemplo, falta plato principal o bebida obligatoria).
 */
public class ComboInvalidoException extends BragadanException {

    public ComboInvalidoException(String mensaje) {
        super(mensaje);
    }
}
