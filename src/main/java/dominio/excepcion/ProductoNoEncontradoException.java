package dominio.excepcion;

/**
 * Lanzada cuando se busca un producto que no existe en el catálogo o menú de Bragadan.
 */
public class ProductoNoEncontradoException extends BragadanException {

    private final String codigoProducto;

    public ProductoNoEncontradoException(String codigoProducto) {
        super("Producto no encontrado en Bragadan: " + codigoProducto);
        this.codigoProducto = codigoProducto;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }
}
