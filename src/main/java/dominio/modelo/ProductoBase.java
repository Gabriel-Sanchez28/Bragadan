package dominio.modelo;

import com.bragadan.comun.MonedaUtils;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Clase base abstracta que encapsula los atributos y comportamientos comunes
 * de todos los productos del menú de Bragadan.
 */
public abstract class ProductoBase implements Producto {

    protected final String codigo;
    protected final String nombre;
    protected final String descripcion;
    protected final BigDecimal precioBase;
    protected final TipoProducto tipo;

    protected ProductoBase(String codigo, String nombre, String descripcion, BigDecimal precioBase, TipoProducto tipo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del producto no puede estar vacío");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        this.codigo = codigo.trim();
        this.nombre = nombre.trim();
        this.descripcion = (descripcion == null || descripcion.isBlank()) ? nombre.trim() : descripcion.trim();
        this.precioBase = MonedaUtils.validarMontoNoNegativo(precioBase, "El precio base");
        this.tipo = Objects.requireNonNull(tipo, "El tipo de producto no puede ser nulo");
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
        return descripcion;
    }

    @Override
    public BigDecimal getPrecio() {
        return MonedaUtils.redondear(precioBase);
    }

    @Override
    public TipoProducto getTipo() {
        return tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductoBase that)) return false;
        return Objects.equals(codigo, that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return String.format("%s [%s] - %s (%s)", nombre, codigo, MonedaUtils.formatear(getPrecio()), descripcion);
    }
}
