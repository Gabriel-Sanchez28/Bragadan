package dominio.observer;

import com.bragadan.dominio.modelo.EstadoPedido;
import com.bragadan.dominio.modelo.Pedido;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Observador del personal de salón y entrega (Mesero) en Bragadan.
 * Se encarga del despacho del pedido hacia la mesa o al cliente.
 */
public class Mesero implements PedidoObserver {

    private final String nombre = "Mesero";
    private final List<NotificacionEvento> notificaciones = new ArrayList<>();

    @Override
    public void actualizar(Pedido pedido, EstadoPedido estadoAnterior) {
        EstadoPedido nuevoEstado = pedido.getEstado();
        String mensaje = switch (nuevoEstado) {
            case LISTO -> String.format(
                    "Mesero [Bragadan]: ¡Atención salón! El Pedido #%d está LISTO en barra. Proceder a servir.",
                    pedido.getId());
            case ENTREGADO -> String.format(
                    "Mesero [Bragadan]: Pedido #%d entregado al cliente con éxito.",
                    pedido.getId());
            case CANCELADO -> String.format(
                    "Mesero [Bragadan]: Pedido #%d cancelado. Retirar comanda de mesa si aplica.",
                    pedido.getId());
            default -> String.format(
                    "Mesero [Bragadan]: Pedido #%d cambió de %s a %s.",
                    pedido.getId(), estadoAnterior, nuevoEstado);
        };

        notificaciones.add(new NotificacionEvento(nombre, pedido.getId(), estadoAnterior, nuevoEstado, mensaje));
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public List<NotificacionEvento> getNotificaciones() {
        return Collections.unmodifiableList(notificaciones);
    }

    public void limpiarHistorial() {
        this.notificaciones.clear();
    }
}
