package dominio.factory;

import com.bragadan.dominio.modelo.Producto;
import com.bragadan.dominio.modelo.TipoProducto;

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
