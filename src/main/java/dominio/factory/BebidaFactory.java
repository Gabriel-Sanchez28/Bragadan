package dominio.factory;

import dominio.excepcion.ConfiguracionInvalidaException;
import dominio.modelo.Bebida;
import dominio.modelo.Producto;
import dominio.modelo.TemperaturaBebida;
import dominio.modelo.TipoProducto;

import java.util.Objects;

/**
 * Fábrica concreta para la creación de bebidas en la cafetería Bragadan.
 */
public class BebidaFactory implements ProductoFactory {

    @Override
    public Producto crear(ConfigProducto config) {
        Objects.requireNonNull(config, "La configuración del producto no puede ser nula");
        if (config.getTipo() != TipoProducto.BEBIDA) {
            throw new ConfiguracionInvalidaException("Tipo incompatible para BebidaFactory: " + config.getTipo());
        }

        TemperaturaBebida temperatura = TemperaturaBebida.CALIENTE;
        Object tempObj = config.obtenerAtributo("temperatura");
        if (tempObj instanceof TemperaturaBebida tb) {
            temperatura = tb;
        } else if (tempObj instanceof String str) {
            temperatura = TemperaturaBebida.desdeTexto(str);
        }

        return new Bebida(
                config.getCodigo(),
                config.getNombre(),
                config.getDescripcion(),
                config.getPrecioBase(),
                temperatura
        );
    }

    @Override
    public TipoProducto getTipoSoportado() {
        return TipoProducto.BEBIDA;
    }
}
