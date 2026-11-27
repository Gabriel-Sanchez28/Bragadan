package dominio.factory;

import dominio.excepcion.ConfiguracionInvalidaException;
import dominio.modelo.Postre;
import dominio.modelo.Producto;
import dominio.modelo.TipoProducto;

import java.util.Objects;

/**
 * Fábrica concreta para la creación de postres en la cafetería Bragadan.
 */
public class PostreFactory implements ProductoFactory {

    @Override
    public Producto crear(ConfigProducto config) {
        Objects.requireNonNull(config, "La configuración del producto no puede ser nula");
        if (config.getTipo() != TipoProducto.POSTRE) {
            throw new ConfiguracionInvalidaException("Tipo incompatible para PostreFactory: " + config.getTipo());
        }

        boolean libreDeGluten = false;
        Object glutenObj = config.obtenerAtributo("libreDeGluten");
        if (glutenObj instanceof Boolean b) {
            libreDeGluten = b;
        } else if (glutenObj instanceof String s) {
            libreDeGluten = Boolean.parseBoolean(s);
        }

        return new Postre(
                config.getCodigo(),
                config.getNombre(),
                config.getDescripcion(),
                config.getPrecioBase(),
                libreDeGluten
        );
    }

    @Override
    public TipoProducto getTipoSoportado() {
        return TipoProducto.POSTRE;
    }
}
