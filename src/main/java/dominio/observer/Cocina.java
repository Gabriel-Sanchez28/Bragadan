package dominio.observer;

import com.bragadan.dominio.modelo.EstadoPedido;
import com.bragadan.dominio.modelo.Pedido;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Observador del área de Cocina / Baristas en la cafetería Bragadan.
 * Se encarga de procesar comandas cuando el pedido entra en preparación y alertar cuando esté listo.
 */
public class Cocina implements PedidoObserver {

    private final String nombre = "Cocina";
    private final List<NotificacionEvento> notificaciones = new ArrayList<>();

    @Override
    public void actualizar(Pedido pedido, EstadoPedido estadoAnterior) {
        EstadoPedido nuevoEstado = pedido.getEstado();
        String mensaje = switch (nuevoEstado) {
            case EN_PREPARACION -> String.format(
                    "Cocina [Bragadan]: Comanda #%d recibida en cocina. Iniciando preparación de %d ítem(s).",
                    pedido.getId(), pedido.getItems().size());
            case LISTO -> String.format(
                    "Cocina [Bragadan]: Pedido #%d terminado y listo en barra de despacho.",
                    pedido.getId());
            case CANCELADO -> String.format(
                    "Cocina [Bragadan]: ¡ALERTA! Pedido #%d cancelado. Detener preparación inmediatamente.",
                    pedido.getId());
            default -> String.format(
                    "Cocina [Bragadan]: Notificado cambio de estado en Pedido #%d (%s -> %s).",
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
