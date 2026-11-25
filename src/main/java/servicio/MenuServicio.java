package servicio;

import dominio.ConfigProducto;
import dominio.Producto;
import dominio.ProductoFactoryRegistry;
import infraestructura.repositorio.MenuRepositorio;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MenuServicio {
    private final MenuRepositorio menuRepositorio;
    private final ProductoFactoryRegistry factoryRegistry;

    public MenuServicio(MenuRepositorio menuRepositorio, ProductoFactoryRegistry factoryRegistry) {
        this.menuRepositorio = Objects.requireNonNull(menuRepositorio, "El repositorio del menú no puede ser nulo");
        this.factoryRegistry = Objects.requireNonNull(factoryRegistry, "El registro de fábricas no puede ser nulo");
    }

    //Agrega un nuevo producto al menú.
    public void agregarProducto(ConfigProducto config) {
        Objects.requireNonNull(config, "La configuración del producto no puede ser nula");
        menuRepositorio.guardar(config);
    }

    //Modifica las características de un producto existente en el menú.
    //@throws ProductoNoEncontradoException si el producto con el código dado no existe.
    public void modificarProducto(String codigo, ConfigProducto nuevaConfig) {
        Objects.requireNonNull(codigo, "El código del producto no puede ser nulo");
        Objects.requireNonNull(nuevaConfig, "La nueva configuración no puede ser nula");

        if (!menuRepositorio.existePorCodigo(codigo)) {
            throw new ProductoNoEncontradoException(codigo);
        }

        // Aseguramos que conserve o use el código especificado
        nuevaConfig.setCodigo(codigo);
        menuRepositorio.guardar(nuevaConfig);
    }

    //Consulta todo el todo el catálogo del menú y retorna una lista de ProductoDTO
    public List<ProductoDTO> consultarMenu() {
        return menuRepositorio.listarTodos().stream()
                .map(factoryRegistry::crearProducto)
                .map(ProductoMapper::toDTO)
                .toList();
    }

    //Consulta las configuraciones raw del menú como DTOs.
    public List<ConfigProductoDTO> consultarConfiguraciones() {
        return menuRepositorio.listarTodos().stream()
                .map(ProductoMapper::toConfigDTO)
                .toList();
    }

    //Crea una instancia de dominio de un Producto a partir de su código en el catálogo.
    //@throws ProductoNoEncontradoException si el código no está registrado en el menú.
    public Producto crearProducto(String codigoProducto) {
        Objects.requireNonNull(codigoProducto, "El código del producto no puede ser nulo");
        ConfigProducto config = menuRepositorio.buscarPorCodigo(codigoProducto)
                .orElseThrow(() -> new ProductoNoEncontradoException(codigoProducto));
        return factoryRegistry.crearProducto(config);
    }

    //Obtiene la configuración de un producto si existe.
    public Optional<ConfigProducto> obtenerConfiguracion(String codigoProducto) {
        return menuRepositorio.buscarPorCodigo(codigoProducto);
    }

    public ProductoFactoryRegistry getFactoryRegistry() {
        return factoryRegistry;
    }
}
