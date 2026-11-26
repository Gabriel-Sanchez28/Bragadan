package dominio.modelo;

import java.math.BigDecimal;

/**
 * Representa un postre o repostería en Bragadan (tortas, croissants, galletas, etc.).
 */
public class Postre extends ProductoBase {

    private final boolean libreDeGluten;

    public Postre(String codigo, String nombre, String descripcion, BigDecimal precioBase, boolean libreDeGluten) {
        super(codigo, nombre, descripcion, precioBase, TipoProducto.POSTRE);
        this.libreDeGluten = libreDeGluten;
    }

    public Postre(String codigo, String nombre, String descripcion, BigDecimal precioBase) {
        this(codigo, nombre, descripcion, precioBase, false);
    }

    public boolean isLibreDeGluten() {
        return libreDeGluten;
    }

    @Override
    public String toString() {
        return String.format("%s (Postre%s) - %s",
                getNombre(),
                libreDeGluten ? " [Sin Gluten]" : "",
                getPrecio());
    }
}
