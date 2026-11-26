package factory;

import com.bragadan.comun.MonedaUtils;
import com.bragadan.dominio.modelo.TipoProducto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Configuración dinámica de un producto del menú de Bragadan.
 * Permite definir, modificar características en tiempo de ejecución
 * e instanciar productos concretos a través de las fábricas registradas.
 */
public class ConfigProducto {

    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precioBase;
    private TipoProducto tipo;
    private final Map<String, Object> atributos;

    public ConfigProducto(String codigo, String nombre, String descripcion, BigDecimal precioBase,
                          TipoProducto tipo, Map<String, Object> atributos) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código no puede estar vacío");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.codigo = codigo.trim();
        this.nombre = nombre.trim();
        this.descripcion = (descripcion == null || descripcion.isBlank()) ? nombre.trim() : descripcion.trim();
        this.precioBase = MonedaUtils.validarMontoNoNegativo(precioBase, "Precio base");
        this.tipo = Objects.requireNonNull(tipo, "El tipo de producto no puede ser nulo");
        this.atributos = atributos != null ? new HashMap<>(atributos) : new HashMap<>();
    }

    public ConfigProducto(String codigo, String nombre, String descripcion, BigDecimal precioBase, TipoProducto tipo) {
        this(codigo, nombre, descripcion, precioBase, tipo, null);
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código no puede estar vacío");
        }
        this.codigo = codigo.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = (descripcion == null || descripcion.isBlank()) ? nombre : descripcion.trim();
    }

    public BigDecimal getPrecioBase() {
        return MonedaUtils.redondear(precioBase);
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = MonedaUtils.validarMontoNoNegativo(precioBase, "Precio base");
    }

    public TipoProducto getTipo() {
        return tipo;
    }

    public void setTipo(TipoProducto tipo) {
        this.tipo = Objects.requireNonNull(tipo, "El tipo de producto no puede ser nulo");
    }

    public Map<String, Object> getAtributos() {
        return Collections.unmodifiableMap(atributos);
    }

    public void agregarAtributo(String clave, Object valor) {
        Objects.requireNonNull(clave, "La clave del atributo no puede ser nula");
        this.atributos.put(clave.trim(), valor);
    }

    public Object obtenerAtributo(String clave) {
        return this.atributos.get(clave);
    }

    @SuppressWarnings("unchecked")
    public <T> T obtenerAtributoComo(String clave, Class<T> claseEsperada) {
        Object valor = this.atributos.get(clave);
        if (valor != null && claseEsperada.isInstance(valor)) {
            return (T) valor;
        }
        return null;
    }

    /**
     * Crea una copia profunda de esta configuración para permitir modificaciones seguras.
     */
    public ConfigProducto clonar() {
        return new ConfigProducto(this.codigo, this.nombre, this.descripcion, this.precioBase, this.tipo, this.atributos);
    }

    @Override
    public String toString() {
        return String.format("ConfigProducto[%s - %s (%s): %s | %s]",
                codigo, nombre, tipo, MonedaUtils.formatear(precioBase), atributos);
    }
}
