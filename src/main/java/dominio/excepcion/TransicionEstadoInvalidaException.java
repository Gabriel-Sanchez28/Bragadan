package dominio.excepcion;

import com.bragadan.dominio.modelo.EstadoPedido;

/**
 * Lanzada cuando se intenta una transición de estado ilegal en el ciclo de vida de un pedido.
 */
public class TransicionEstadoInvalidaException extends BragadanException {

    private final EstadoPedido estadoActual;
    private final EstadoPedido estadoDestino;

    public TransicionEstadoInvalidaException(EstadoPedido estadoActual, EstadoPedido estadoDestino) {
        super(String.format("Transición inválida en Bragadan: no se puede pasar de %s a %s",
                estadoActual, estadoDestino));
        this.estadoActual = estadoActual;
        this.estadoDestino = estadoDestino;
    }

    public EstadoPedido getEstadoActual() {
        return estadoActual;
    }

    public EstadoPedido getEstadoDestino() {
        return estadoDestino;
    }
}
