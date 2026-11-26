package dominio.excepcion;

/**
 * Lanzada cuando se busca un pedido con un identificador inexistente en Bragadan.
 */
public class PedidoNoEncontradoException extends BragadanException {

    private final Long pedidoId;

    public PedidoNoEncontradoException(Long pedidoId) {
        super("Pedido no encontrado en Bragadan con ID: " + pedidoId);
        this.pedidoId = pedidoId;
    }

    public Long getPedidoId() {
        return pedidoId;
    }
}
