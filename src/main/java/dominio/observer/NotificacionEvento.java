package dominio.observer;

import com.bragadan.dominio.modelo.EstadoPedido;

import java.time.LocalDateTime;

/**
 * Registro inmutable que almacena el detalle de un evento de notificación
 * recibido por un observador en la cafetería Bragadan.
 */
public record NotificacionEvento(
        String observador,
        Long pedidoId,
        EstadoPedido estadoAnterior,
        EstadoPedido nuevoEstado,
        String mensaje,
        LocalDateTime fechaHora
) {
    public NotificacionEvento(String observador, Long pedidoId, EstadoPedido estadoAnterior,
                              EstadoPedido nuevoEstado, String mensaje) {
        this(observador, pedidoId, estadoAnterior, nuevoEstado, mensaje, LocalDateTime.now());
    }
}
