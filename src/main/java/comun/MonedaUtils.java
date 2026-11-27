package comun;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Utilidades monetarias centralizadas para la cafetería Bragadan.
 * Asegura el uso consistente de BigDecimal, escala a2 decimales y RoundingMode.HALF_UP.
 */
public final class MonedaUtils {

    public static final int ESCALA = 2;
    public static final RoundingMode MODO_REDONDEO = RoundingMode.HALF_UP;
    public static final BigDecimal CERO = BigDecimal.ZERO.setScale(ESCALA, MODO_REDONDEO);

    private static final NumberFormat FORMATO_MONEDA = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));

    private MonedaUtils() {
        // Clase de utilidades no instanciable
    }

    /**
     * Redondea un importe a 2 decimales
     */
    public static BigDecimal redondear(BigDecimal monto) {
        if (monto == null) {
            return CERO;
        }
        return monto.setScale(ESCALA, MODO_REDONDEO);
    }

    /**
     * Valida que un importe no sea nulo ni negativo.
     */
    public static BigDecimal validarMontoNoNegativo(BigDecimal monto, String nombreCampo) {
        Objects.requireNonNull(monto, nombreCampo + " no puede ser nulo");
        if (monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(nombreCampo + " no puede ser negativo: " + monto);
        }
        return redondear(monto);
    }

    /**
     * Formatea un valor monetario para presentación.
     */
    public static String formatear(BigDecimal monto) {
        return FORMATO_MONEDA.format(redondear(monto));
    }
}
