package dominio.observer;

import comun.MonedaUtils;
import dominio.modelo.EstadoPedido;
import dominio.modelo.Pedido;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Observador del área de Caja / Facturación en la cafetería Bragadan.
 * Se encarga del control de cobros, arqueo y registro financiero del pedido.
 */
public class Caja implements PedidoObserver {

    private final String nombre = "Caja";
    private final List<NotificacionEvento> notificaciones = new ArrayList<>();

    @Override
    public void actualizar(Pedido pedido, EstadoPedido estadoAnterior) {
        EstadoPedido nuevoEstado = pedido.getEstado();
        String mensaje = switch (nuevoEstado) {
            case EN_PREPARACION -> String.format(
                    "Caja [Bragadan]: Pedido #%d confirmado en preparación. Total registrado: %s.",
                    pedido.getId(), MonedaUtils.formatear(pedido.calcularTotal()));
            case LISTO -> String.format(
                    "Caja [Bragadan]: Pedido #%d marcado como listo. Ticket pendiente de cobro/entrega.",
                    pedido.getId());
            case ENTREGADO -> String.format(
                    "Caja [Bragadan]: Pedido #%d entregado al cliente. Factura cerrada exitosamente por %s.",
                    pedido.getId(), MonedaUtils.formatear(pedido.calcularTotal()));
            case CANCELADO -> String.format(
                    "Caja [Bragadan]: Pedido #%d CANCELADO. Comprobante anulado y reversión contable registrada.",
                    pedido.getId());
            default -> String.format(
                    "Caja [Bragadan]: Transición en Pedido #%d de %s a %s.",
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
