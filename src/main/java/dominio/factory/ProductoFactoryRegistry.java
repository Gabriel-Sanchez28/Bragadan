package dominio.factory;

import dominio.excepcion.ConfiguracionInvalidaException;
import dominio.modelo.Producto;
import dominio.modelo.TipoProducto;

import java.util.*;

/**
 * Registro centralizado de fábricas de productos para Bragadan (Registry Pattern).
 * Permite registrar y desacoplar la creación de nuevos tipos de productos
 * cumpliendo con el principio Open/Closed (OCP).
 */
public class ProductoFactoryRegistry {

    private final Map<TipoProducto, ProductoFactory> factories;

    public ProductoFactoryRegistry() {
        this.factories = new EnumMap<>(TipoProducto.class);
    }

    /**
     * Registra una fábrica asociada a un tipo de producto.
     */
    public void registrarFactory(TipoProducto tipo, ProductoFactory factory) {
        Objects.requireNonNull(tipo, "El tipo de producto no puede ser nulo");
        Objects.requireNonNull(factory, "La fábrica no puede ser nula");
        this.factories.put(tipo, factory);
    }

    /**
     * Sobrecarga para registrar mediante nombre textual del tipo.
     */
    public void registrarFactory(String tipoTexto, ProductoFactory factory) {
        Objects.requireNonNull(tipoTexto, "El tipo en texto no puede ser nulo");
        TipoProducto tipo = TipoProducto.valueOf(tipoTexto.trim().toUpperCase());
        registrarFactory(tipo, factory);
    }

    /**
     * Obtiene la fábrica registrada para un tipo de producto.
     */
    public Optional<ProductoFactory> obtenerFactory(TipoProducto tipo) {
        return Optional.ofNullable(this.factories.get(tipo));
    }

    /**
     * Crea un producto a partir de su configuración utilizando la fábrica correspondiente.
     *
     * @param config Configuración del producto.
     * @return Producto creado.
     * @throws ConfiguracionInvalidaException si no hay fábrica registrada para el tipo.
     */
    public Producto crearProducto(ConfigProducto config) {
        Objects.requireNonNull(config, "La configuración del producto no puede ser nula");
        ProductoFactory factory = factories.get(config.getTipo());
        if (factory == null) {
            throw new ConfiguracionInvalidaException(
                    "No existe una fábrica registrada en Bragadan para el tipo de producto: " + config.getTipo());
        }
        return factory.crear(config);
    }

    /**
     * Retorna una vista inmutable de las fábricas actualmente registradas.
     */
    public Map<TipoProducto, ProductoFactory> getFabricasRegistradas() {
        return Collections.unmodifiableMap(factories);
    }

    /**
     * Fábrica estática de conveniencia inicializada con las fábricas por defecto de Bragadan.
     */
    public static ProductoFactoryRegistry conFabricasPorDefecto() {
        ProductoFactoryRegistry registry = new ProductoFactoryRegistry();
        registry.registrarFactory(TipoProducto.BEBIDA, new BebidaFactory());
        registry.registrarFactory(TipoProducto.PLATO, new PlatoFactory());
        registry.registrarFactory(TipoProducto.POSTRE, new PostreFactory());
        return registry;
    }
}
