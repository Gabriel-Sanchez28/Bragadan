package dominio.decorator;

import com.bragadan.comun.MonedaUtils;
import com.bragadan.dominio.modelo.Producto;
import com.bragadan.dominio.modelo.TipoProducto;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Decorador base para la personalización de productos en Bragadan.
 * Implementa el patrón estructural Decorator para agregar adiciones
 * de manera dinámica, acumulando descripción y precio sin alterar el producto original.
 */
public abstract class AdicionDecorator implements Producto {

    protected final Producto productoDecorado;
    protected final String nombreAdicion;
    protected final BigDecimal precioAdicional;

    protected AdicionDecorator(Producto productoDecorado, String nombreAdicion, BigDecimal precioAdicional) {
        this.productoDecorado = Objects.requireNonNull(productoDecorado, "El producto a personalizar no puede ser nulo");
        if (nombreAdicion == null || nombreAdicion.isBlank()) {
            throw new IllegalArgumentException("El nombre de la adición no puede estar vacío");
        }
        this.nombreAdicion = nombreAdicion.trim();
        this.precioAdicional = MonedaUtils.validarMontoNoNegativo(precioAdicional, "Precio adicional");
    }

    @Override
    public String getCodigo() {
        return productoDecorado.getCodigo();
    }

    @Override
    public String getNombre() {
        return productoDecorado.getNombre();
    }

    @Override
    public String getDescripcion() {
        return productoDecorado.getDescripcion() + " + " + nombreAdicion;
    }

    @Override
    public BigDecimal getPrecio() {
        return MonedaUtils.redondear(productoDecorado.getPrecio().add(precioAdicional));
    }

    @Override
    public TipoProducto getTipo() {
        return productoDecorado.getTipo();
    }

    public Producto getProductoDecorado() {
        return productoDecorado;
    }

    public String getNombreAdicion() {
        return nombreAdicion;
    }

    public BigDecimal getPrecioAdicional() {
        return MonedaUtils.redondear(precioAdicional);
    }

    @Override
    public String toString() {
        return String.format("%s [Personalizado] - %s (%s)",
                getNombre(), MonedaUtils.formatear(getPrecio()), getDescripcion());
    }
}
