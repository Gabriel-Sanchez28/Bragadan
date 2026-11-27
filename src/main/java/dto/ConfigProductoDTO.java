package dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO inmutable para la transferencia de configuraciones de productos.
 */
public record ConfigProductoDTO(
        String codigo,
        String nombre,
        String descripcion,
        BigDecimal precioBase,
        String tipo,
        Map<String, Object> atributos
) {
    public ConfigProductoDTO {
        atributos = atributos != null ? Map.copyOf(atributos) : Map.of();
    }
}
