package dominio.decorator;

import com.bragadan.dominio.modelo.Producto;

import java.math.BigDecimal;

/**
 * Decorador para agregar un shot extra de café en Bragadan.
 */
public class ShotExtra extends AdicionDecorator {

    public static final String NOMBRE = "Shot Extra";
    public static final BigDecimal PRECIO_POR_DEFECTO = new BigDecimal("1500.00");

    public ShotExtra(Producto productoDecorado, BigDecimal precioAdicional) {
        super(productoDecorado, NOMBRE, precioAdicional);
    }

    public ShotExtra(Producto productoDecorado) {
        this(productoDecorado, PRECIO_POR_DEFECTO);
    }
}
