package dominio.decorator;

import dominio.modelo.Producto;

import java.math.BigDecimal;

/**
 * Decorador genérico configurable para adiciones dinámicas en Bragadan.
 */
public class AdicionGenerica extends AdicionDecorator {

    public AdicionGenerica(Producto productoDecorado, String nombreAdicion, BigDecimal precioAdicional) {
        super(productoDecorado, nombreAdicion, precioAdicional);
    }
}
