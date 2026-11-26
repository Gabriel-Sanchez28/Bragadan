package dominio.builder;

import com.bragadan.dominio.modelo.Combo;
import com.bragadan.dominio.modelo.Producto;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Director del patrón Builder para Bragadan.
 * Define recetas y combinaciones preestablecidas de combos (desayuno, almuerzo, merienda)
 * aplicando promociones estándar de la cafetería.
 */
public class ComboDirector {

    private final ComboBuilder builder;
    public static final BigDecimal DESCUENTO_ESTANDAR = new BigDecimal("0.10"); // 10% de ahorro

    public ComboDirector(ComboBuilder builder) {
        this.builder = Objects.requireNonNull(builder, "El builder no puede ser nulo");
    }

    /**
     * Construye un Combo Desayuno Bragadan (Principal + Bebida + Acompañamiento opcional).
     */
    public Combo armarDesayuno(Producto principal, Producto bebida, Producto acompanamiento) {
        builder.reiniciar()
                .conCodigo("COMBO-DESAYUNO")
                .conNombre("Combo Desayuno Bragadan")
                .conDescripcion("Desayuno energético con bebida, plato principal y acompañamiento")
                .conDescuento(DESCUENTO_ESTANDAR)
                .agregarPrincipal(principal)
                .agregarBebida(bebida);

        if (acompanamiento != null) {
            builder.agregarAcompanamiento(acompanamiento);
        }

        return builder.build();
    }

    /**
     * Construye un Combo Almuerzo Completo Bragadan (Principal + Bebida + Acompañamiento + Postre).
     */
    public Combo armarAlmuerzo(Producto principal, Producto bebida, Producto acompanamiento, Producto postre) {
        builder.reiniciar()
                .conCodigo("COMBO-ALMUERZO")
                .conNombre("Combo Almuerzo Bragadan Gourmet")
                .conDescripcion("Almuerzo balanceado con plato fuerte, bebida fresca, snack y postre")
                .conDescuento(new BigDecimal("0.15")) // 15% de ahorro en almuerzo
                .agregarPrincipal(principal)
                .agregarBebida(bebida);

        if (acompanamiento != null) {
            builder.agregarAcompanamiento(acompanamiento);
        }
        if (postre != null) {
            builder.agregarPostre(postre);
        }

        return builder.build();
    }

    /**
     * Construye un Combo Merienda o Pausa de Café Bragadan (Principal/Pastel + Bebida + Postre).
     */
    public Combo armarMerienda(Producto principal, Producto bebida, Producto postre) {
        builder.reiniciar()
                .conCodigo("COMBO-MERIENDA")
                .conNombre("Combo Merienda Bragadan")
                .conDescripcion("Pausa de café perfecta con bocadillo, bebida y repostería artesanal")
                .conDescuento(DESCUENTO_ESTANDAR)
                .agregarPrincipal(principal)
                .agregarBebida(bebida);

        if (postre != null) {
            builder.agregarPostre(postre);
        }

        return builder.build();
    }

    /**
     * Combo exprés básico (Principal + Bebida).
     */
    public Combo armarComboExpress(Producto principal, Producto bebida) {
        return builder.reiniciar()
                .conCodigo("COMBO-EXPRESS")
                .conNombre("Combo Exprés Bragadan")
                .conDescripcion("Opción rápida para llevar")
                .conDescuento(new BigDecimal("0.05"))
                .agregarPrincipal(principal)
                .agregarBebida(bebida)
                .build();
    }
}
