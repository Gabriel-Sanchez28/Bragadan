package dominio.modelo;

/**
 * Temperatura de servicio para bebidas en Bragadan.
 */
public enum TemperaturaBebida {
    CALIENTE,
    FRIA,
    TEMPERATURA_AMBIENTE;

    public static TemperaturaBebida desdeTexto(String valor) {
        if (valor == null || valor.isBlank()) {
            return CALIENTE;
        }
        try {
            return TemperaturaBebida.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return CALIENTE;
        }
    }
}
