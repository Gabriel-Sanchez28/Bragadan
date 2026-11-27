package dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO inmutable que expone la información completa de un pedido hacia capas externas
 */
public record PedidoDTO(
        Long id,
        String estado,
        List<ProductoDTO> items,
        BigDecimal total,
        String totalFormateado,
        LocalDateTime fechaCreacion,
        int cantidadItems
) {
    public PedidoDTO {
        items = items != null ? List.copyOf(items) : List.of();
    }
}
