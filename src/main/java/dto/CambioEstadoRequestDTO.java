package com.bragadan.dto;

/**
 * DTO para solicitar la transición de estado de un pedido en Bragadan.
 */
public record CambioEstadoRequestDTO(
        String nuevoEstado,
        String motivo
) {
    public CambioEstadoRequestDTO(String nuevoEstado) {
        this(nuevoEstado, null);
    }
}
