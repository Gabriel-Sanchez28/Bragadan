package dominio.factory;


import dominio.modelo.Producto;
import dominio.modelo.TipoProducto;

/**
 * Interfaz del patrón Factory Method para la creación de productos en Bragadan.
 */
public interface ProductoFactory {

    /**
     * Crea una instancia concreta de Producto a partir de su configuración.
     *
     * @param config Configuración del producto a instanciar.
     * @return Instancia concreta de Producto.
     */
    Producto crear(ConfigProducto config);

    /**
     * Retorna el tipo de producto que esta fábrica es capaz de construir.
     */
    TipoProducto getTipoSoportado();
}
