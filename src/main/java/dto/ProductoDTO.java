package dto;

import java.math.BigDecimal;

/**
 * DTO inmutable que representa un producto para el consumo de capas externas.
 */
public record ProductoDTO(
        String codigo,
        String nombre,
        String descripcion,
        BigDecimal precio,
        String precioFormateado,
        String tipo
) {
}
