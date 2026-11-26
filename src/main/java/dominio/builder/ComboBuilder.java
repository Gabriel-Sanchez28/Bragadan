package dominio.builder;

import com.bragadan.dominio.modelo.Combo;
import com.bragadan.dominio.modelo.Producto;

import java.math.BigDecimal;

/**
 * Interfaz fluida del patrón Builder para la construcción paso a paso de combos en Bragadan.
 */
public interface ComboBuilder {

    /**
     * Reinicia el estado interno del builder para armar un nuevo combo.
     */
    ComboBuilder reiniciar();

    /**
     * Asigna el código identificador del combo.
     */
    ComboBuilder conCodigo(String codigo);

    /**
     * Asigna el nombre comercial del combo.
     */
    ComboBuilder conNombre(String nombre);

    /**
     * Asigna la descripción comercial del combo.
     */
    ComboBuilder conDescripcion(String descripcion);

    /**
     * Asigna un porcentaje de descuento aplicado sobre la suma de los productos (ej: 0.10 para 10%).
     */
    ComboBuilder conDescuento(BigDecimal porcentajeDescuento);

    /**
     * Agrega el plato principal (obligatorio).
     */
    ComboBuilder agregarPrincipal(Producto principal);

    /**
     * Agrega la bebida (obligatoria).
     */
    ComboBuilder agregarBebida(Producto bebida);

    /**
     * Agrega un acompañamiento opcional (snack, entrada, etc.).
     */
    ComboBuilder agregarAcompanamiento(Producto acompanamiento);

    /**
     * Agrega un postre opcional.
     */
    ComboBuilder agregarPostre(Producto postre);

    /**
     * Construye y retorna la instancia final de Combo validando invariantes obligatorias.
     *
     * @return Combo completamente construido y validado.
     * @throws com.bragadan.dominio.excepcion.ComboInvalidoException si faltan elementos obligatorios.
     */
    Combo build();
}
