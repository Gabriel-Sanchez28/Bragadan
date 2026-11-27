package dominio.builder;

import dominio.excepcion.ComboInvalidoException;
import dominio.modelo.Combo;
import dominio.modelo.Producto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementación concreta del Builder de Combos para Bragadan.
 * Valida que los elementos obligatorios (principal y bebida) estén presentes antes de construir.
 */
public class ComboBuilderImpl implements ComboBuilder {

    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal porcentajeDescuento;
    private Producto principal;
    private Producto bebida;
    private Producto acompanamiento;
    private Producto postre;

    public ComboBuilderImpl() {
        reiniciar();
    }

    @Override
    public ComboBuilder reiniciar() {
        this.codigo = null;
        this.nombre = null;
        this.descripcion = null;
        this.porcentajeDescuento = BigDecimal.ZERO;
        this.principal = null;
        this.bebida = null;
        this.acompanamiento = null;
        this.postre = null;
        return this;
    }

    @Override
    public ComboBuilder conCodigo(String codigo) {
        this.codigo = codigo;
        return this;
    }

    @Override
    public ComboBuilder conNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    @Override
    public ComboBuilder conDescripcion(String descripcion) {
        this.descripcion = descripcion;
        return this;
    }

    @Override
    public ComboBuilder conDescuento(BigDecimal porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento != null ? porcentajeDescuento : BigDecimal.ZERO;
        return this;
    }

    @Override
    public ComboBuilder agregarPrincipal(Producto principal) {
        this.principal = principal;
        return this;
    }

    @Override
    public ComboBuilder agregarBebida(Producto bebida) {
        this.bebida = bebida;
        return this;
    }

    @Override
    public ComboBuilder agregarAcompanamiento(Producto acompanamiento) {
        this.acompanamiento = acompanamiento;
        return this;
    }

    @Override
    public ComboBuilder agregarPostre(Producto postre) {
        this.postre = postre;
        return this;
    }

    @Override
    public Combo build() {
        if (principal == null) {
            throw new ComboInvalidoException("El combo de Bragadan debe incluir obligatoriamente un plato principal");
        }
        if (bebida == null) {
            throw new ComboInvalidoException("El combo de Bragadan debe incluir obligatoriamente una bebida");
        }

        String codigoFinal = (codigo != null && !codigo.isBlank())
                ? codigo.trim()
                : "COMBO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        String nombreFinal = (nombre != null && !nombre.isBlank())
                ? nombre.trim()
                : "Combo Especial Bragadan";

        String descFinal = (descripcion != null && !descripcion.isBlank())
                ? descripcion.trim()
                : "Combo compuesto por " + principal.getNombre() + " y " + bebida.getNombre();

        List<Producto> items = new ArrayList<>();
        items.add(principal);
        items.add(bebida);
        if (acompanamiento != null) {
            items.add(acompanamiento);
        }
        if (postre != null) {
            items.add(postre);
        }

        Combo combo = new Combo(codigoFinal, nombreFinal, descFinal, items, porcentajeDescuento);
        reiniciar();
        return combo;
    }
}
