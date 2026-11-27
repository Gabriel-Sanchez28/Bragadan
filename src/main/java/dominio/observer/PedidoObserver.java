package dominio.observer;

import dominio.modelo.EstadoPedido;
import dominio.modelo.Pedido;

/**
 * Interfaz para los observadores del ciclo de vida de un pedido en Bragadan.
 * Se notifica cuando ocurre una transición de estado válida.
 */
public interface PedidoObserver {

    /**
     * Notificación de actualización ante un cambio de estado válido.
     *
     * @param pedido         El pedido cuyo estado ha cambiado (pedido.getEstado() contiene el nuevo estado).
     * @param estadoAnterior El estado previo antes de la transición.
     */
    void actualizar(Pedido pedido, EstadoPedido estadoAnterior);

    /**
     * Nombre identificador del observador (ej: "Cocina", "Caja", "Mesero").
     */
    String getNombre();
}
