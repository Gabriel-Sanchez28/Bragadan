package dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que representa el resumen formateado o factura simplificada de un pedido de Bragadan.
 */
public record ResumenPedidoDTO(
        Long pedidoId,
        String estado,
        List<String> lineasDetalle,
        BigDecimal total,
        String totalFormateado,
        LocalDateTime fechaHora
) {
    public ResumenPedidoDTO {
        lineasDetalle = lineasDetalle != null ? List.copyOf(lineasDetalle) : List.of();
    }
}
