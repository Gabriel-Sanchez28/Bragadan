package dominio.modelo;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Representa una bebida en la cafetería Bragadan (cafés, tés, jugos, etc.),
 * con su respectiva temperatura de servicio.
 */
public class Bebida extends ProductoBase {

    private final TemperaturaBebida temperatura;

    public Bebida(String codigo, String nombre, String descripcion, BigDecimal precioBase, TemperaturaBebida temperatura) {
        super(codigo, nombre, descripcion, precioBase, TipoProducto.BEBIDA);
        this.temperatura = Objects.requireNonNullElse(temperatura, TemperaturaBebida.CALIENTE);
    }

    public TemperaturaBebida getTemperatura() {
        return temperatura;
    }

    @Override
    public String toString() {
        return String.format("%s (Bebida %s) - %s", getNombre(), temperatura, getPrecio());
    }
}
