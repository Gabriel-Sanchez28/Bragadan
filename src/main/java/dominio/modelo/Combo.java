package dominio.modelo;

import com.bragadan.comun.MonedaUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Representa un Combo en Bragadan utilizando el patrón Composite.
 * Un Combo se comporta como cualquier otro Producto, pero está internamente
 * compuesto por una colección de productos (principal, bebida, acompañamiento, postre).
 */
public class Combo implements Producto {

    private final String codigo;
    private final String nombre;
    private final String descripcion;
    private final List<Producto> items;
    private final BigDecimal porcentajeDescuento;

    public Combo(String codigo, String nombre, String descripcion, List<Producto> items, BigDecimal porcentajeDescuento) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del combo no puede estar vacío");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del combo no puede estar vacío");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Un combo debe tener al menos un ítem");
        }

        this.codigo = codigo.trim();
        this.nombre = nombre.trim();
        this.descripcion = (descripcion == null || descripcion.isBlank()) ? nombre.trim() : descripcion.trim();
        this.items = List.copyOf(items);
        this.porcentajeDescuento = porcentajeDescuento != null ? porcentajeDescuento : BigDecimal.ZERO;
    }

    public Combo(String codigo, String nombre, String descripcion, List<Producto> items) {
        this(codigo, nombre, descripcion, items, BigDecimal.ZERO);
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public String getDescripcion() {
        String detalleItems = items.stream()
                .map(Producto::getNombre)
                .collect(Collectors.joining(", "));
        return String.format("%s (Incluye: %s)", descripcion, detalleItems);
    }

    @Override
    public BigDecimal getPrecio() {
        BigDecimal suma = items.stream()
                .map(Producto::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (porcentajeDescuento.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal factorDescuento = BigDecimal.ONE.subtract(porcentajeDescuento);
            return MonedaUtils.redondear(suma.multiply(factorDescuento));
        }

        return MonedaUtils.redondear(suma);
    }

    @Override
    public TipoProducto getTipo() {
        return TipoProducto.COMBO;
    }

    /**
     * Retorna una copia inmutable de los productos que componen el combo.
     */
    public List<Producto> getItems() {
        return Collections.unmodifiableList(items);
    }

    public BigDecimal getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Combo combo)) return false;
        return Objects.equals(codigo, combo.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return String.format("Combo %s [%s] - %s (%d ítems)",
                nombre, codigo, MonedaUtils.formatear(getPrecio()), items.size());
    }
}
