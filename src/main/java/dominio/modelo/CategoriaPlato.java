package dominio.modelo;

/**
 * Categorías de platos ofrecidos en la cafetería Bragadan.
 */
public enum CategoriaPlato {
    DESAYUNO,
    ALMUERZO,
    ENTRADA,
    FUERTE,
    SNACK;

    public static CategoriaPlato desdeTexto(String valor) {
        if (valor == null || valor.isBlank()) {
            return SNACK;
        }
        try {
            return CategoriaPlato.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return SNACK;
        }
    }
}
