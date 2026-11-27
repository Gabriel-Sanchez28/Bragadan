package dto;

import java.math.BigDecimal;

/**
 * DTO para solicitar la creación de un combo en Bragadan.
 */
public record ComboRequestDTO(
        String codigo,
        String nombre,
        String descripcion,
        String codigoPrincipal,
        String codigoBebida,
        String codigoAcompanamiento,
        String codigoPostre,
        BigDecimal porcentajeDescuento
) {
}
