package dominio.modelo;

import java.math.BigDecimal;

/**
 * Abstracción fundamental para cualquier elemento comercializable en Bragadan.
 * Cumple con el principio de sustitución de Liskov (LSP): cualquier producto base,
 * decorado o combo puede ser tratado uniformemente a través de esta interfaz.
 */
public interface Producto {

    /**
     * Identificador o código único de referencia.
     */
    String getCodigo();

    /**
     * Nombre comercial del producto.
     */
    String getNombre();

    /**
     * Descripción detallada del producto, incluyendo posibles personalizaciones.
     */
    String getDescripcion();

    /**
     * Precio final calculado del producto, redondeado a 2 decimales.
     */
    BigDecimal getPrecio();

    /**
     * Tipo de producto según la categorización del dominio.
     */
    TipoProducto getTipo();
}
