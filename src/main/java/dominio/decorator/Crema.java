package dominio.decorator;

import com.bragadan.dominio.modelo.Producto;

import java.math.BigDecimal;

/**
 * Decorador para agregar crema a una bebida o postre en Bragadan.
 */
public class Crema extends AdicionDecorator {

    public static final String NOMBRE = "Crema Batida";
    public static final BigDecimal PRECIO_POR_DEFECTO = new BigDecimal("1200.00");

    public Crema(Producto productoDecorado, BigDecimal precioAdicional) {
        super(productoDecorado, NOMBRE, precioAdicional);
    }

    public Crema(Producto productoDecorado) {
        this(productoDecorado, PRECIO_POR_DEFECTO);
    }
}
