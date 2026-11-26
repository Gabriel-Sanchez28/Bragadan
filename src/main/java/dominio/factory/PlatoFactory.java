package dominio.factory;

import com.bragadan.dominio.excepcion.ConfiguracionInvalidaException;
import com.bragadan.dominio.modelo.CategoriaPlato;
import com.bragadan.dominio.modelo.Plato;
import com.bragadan.dominio.modelo.Producto;
import com.bragadan.dominio.modelo.TipoProducto;

import java.util.Objects;

/**
 * Fábrica concreta para la creación de platos en la cafetería Bragadan.
 */
public class PlatoFactory implements ProductoFactory {

    @Override
    public Producto crear(ConfigProducto config) {
        Objects.requireNonNull(config, "La configuración del producto no puede ser nula");
        if (config.getTipo() != TipoProducto.PLATO) {
            throw new ConfiguracionInvalidaException("Tipo incompatible para PlatoFactory: " + config.getTipo());
        }

        CategoriaPlato categoria = CategoriaPlato.SNACK;
        Object catObj = config.obtenerAtributo("categoria");
        if (catObj instanceof CategoriaPlato cp) {
            categoria = cp;
        } else if (catObj instanceof String str) {
            categoria = CategoriaPlato.desdeTexto(str);
        }

        return new Plato(
                config.getCodigo(),
                config.getNombre(),
                config.getDescripcion(),
                config.getPrecioBase(),
                categoria
        );
    }

    @Override
    public TipoProducto getTipoSoportado() {
        return TipoProducto.PLATO;
    }
}
