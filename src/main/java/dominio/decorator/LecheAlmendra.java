package dominio.decorator;

import dominio.modelo.Producto;

import java.math.BigDecimal;

/**
 * Decorador para cambiar o agregar leche de almendras en Bragadan.
 */
public class LecheAlmendra extends AdicionDecorator {

    public static final String NOMBRE = "Leche de Almendra";
    public static final BigDecimal PRECIO_POR_DEFECTO = new BigDecimal("2000.00");

    public LecheAlmendra(Producto productoDecorado, BigDecimal precioAdicional) {
        super(productoDecorado, NOMBRE, precioAdicional);
    }

    public LecheAlmendra(Producto productoDecorado) {
        this(productoDecorado, PRECIO_POR_DEFECTO);
    }
}
