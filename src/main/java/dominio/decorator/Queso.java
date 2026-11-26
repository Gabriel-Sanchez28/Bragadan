package dominio.decorator;

import com.bragadan.dominio.modelo.Producto;

import java.math.BigDecimal;

/**
 * Decorador para agregar queso extra a un producto en Bragadan.
 */
public class Queso extends AdicionDecorator {

    public static final String NOMBRE = "Queso Extra";
    public static final BigDecimal PRECIO_POR_DEFECTO = new BigDecimal("1800.00");

    public Queso(Producto productoDecorado, BigDecimal precioAdicional) {
        super(productoDecorado, NOMBRE, precioAdicional);
    }

    public Queso(Producto productoDecorado) {
        this(productoDecorado, PRECIO_POR_DEFECTO);
    }
}
