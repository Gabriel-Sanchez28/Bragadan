package dominio.modelo;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Representa un plato de comida en Bragadan (sándwiches, desayunos, bowls, etc.),
 * clasificado por su categoría alimentaria.
 */
public class Plato extends ProductoBase {

    private final CategoriaPlato categoria;

    public Plato(String codigo, String nombre, String descripcion, BigDecimal precioBase, CategoriaPlato categoria) {
        super(codigo, nombre, descripcion, precioBase, TipoProducto.PLATO);
        this.categoria = Objects.requireNonNullElse(categoria, CategoriaPlato.SNACK);
    }

    public CategoriaPlato getCategoria() {
        return categoria;
    }

    @Override
    public String toString() {
        return String.format("%s (Plato [%s]) - %s", getNombre(), categoria, getPrecio());
    }
}
