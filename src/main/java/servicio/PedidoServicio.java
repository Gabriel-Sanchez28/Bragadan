package servicio;

import dominio.*;
import dominio.decorator.Crema;
import dominio.decorator.LecheAlmendra;
import dominio.decorator.Queso;
import dominio.decorator.ShotExtra;
import dominio.excepcion.PedidoNoEncontradoException;
import dominio.modelo.EstadoPedido;
import dominio.modelo.Pedido;
import dominio.modelo.Producto;
import dominio.observer.Caja;
import dominio.observer.Cocina;
import dominio.observer.Mesero;
import dominio.observer.PedidoObserver;
import dto.CambioEstadoRequestDTO;
import dto.PedidoDTO;
import dto.PedidoMapper;
import dto.ResumenPedidoDTO;
import infraestructura.repositorio.PedidoRepositorio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class PedidoServicio {
    private final PedidoRepositorio pedidoRepositorio;
    private final MenuServicio menuServicio;
    private final List<Supplier<PedidoObserver>> proveedoresObservadores;

    public PedidoServicio(PedidoRepositorio pedidoRepositorio, MenuServicio menuServicio,
                          List<Supplier<PedidoObserver>> proveedoresObservadores) {
        this.pedidoRepositorio = Objects.requireNonNull(pedidoRepositorio, "El repositorio de pedidos no puede ser nulo");
        this.menuServicio = Objects.requireNonNull(menuServicio, "El servicio de menú no puede ser nulo");
        this.proveedoresObservadores = proveedoresObservadores != null
                ? List.copyOf(proveedoresObservadores)
                : List.of(Cocina::new, Caja::new, Mesero::new);
    }

    public PedidoServicio(PedidoRepositorio pedidoRepositorio, MenuServicio menuServicio) {
        this(pedidoRepositorio, menuServicio, List.of(Cocina::new, Caja::new, Mesero::new));
    }

    /*Crea un nuevo pedido inicializado en estado RECIBIDO con los observadores por defecto
     * (Cocina, Caja, Mesero).
     */
    public PedidoDTO crearPedido() {
        Long id = pedidoRepositorio.generarSiguienteId();
        Pedido pedido = new Pedido(id);

        // Suscribir observadores de áreas funcionales
        for (Supplier<PedidoObserver> proveedor : proveedoresObservadores) {
            pedido.agregarObservador(proveedor.get());
        }

        pedidoRepositorio.guardar(pedido);
        return PedidoMapper.toDTO(pedido);
    }

    //Crea un pedido suscribiendo una lista específica de instancias de observadores.
    public PedidoDTO crearPedidoConObservadores(List<PedidoObserver> observadores) {
        Long id = pedidoRepositorio.generarSiguienteId();
        Pedido pedido = new Pedido(id);

        if (observadores != null) {
            for (PedidoObserver observador : observadores) {
                pedido.agregarObservador(observador);
            }
        }

        pedidoRepositorio.guardar(pedido);
        return PedidoMapper.toDTO(pedido);
    }

    // Agrega un producto (simple, decorado o combo) a un pedido existente.
    public PedidoDTO agregarItem(Long pedidoId, Producto producto) {
        Objects.requireNonNull(producto, "El producto a agregar no puede ser nulo");
        Pedido pedido = obtenerPedidoEntidad(pedidoId);
        pedido.agregarItem(producto);
        pedidoRepositorio.guardar(pedido);
        return PedidoMapper.toDTO(pedido);
    }

    //Agrega un producto al pedido resolviéndolo directamente desde el catálogo del menú por código.
    public PedidoDTO agregarItemPorCodigo(Long pedidoId, String codigoProducto) {
        Producto producto = menuServicio.crearProducto(codigoProducto);
        return agregarItem(pedidoId, producto);
    }

    // Personaliza un producto base envolviéndolo con una adición mediante el patrón Decorator.
    public Producto personalizar(Producto producto, String tipoAdicion) {
        Objects.requireNonNull(producto, "El producto no puede ser nulo");
        Objects.requireNonNull(tipoAdicion, "El tipo de adición no puede ser nulo");

        String tipoNorm = tipoAdicion.trim().toUpperCase().replace(" ", "_");

        return switch (tipoNorm) {
            case "SHOT_EXTRA", "SHOT" -> new ShotExtra(producto);
            case "LECHE_ALMENDRA", "LECHE_DE_ALMENDRA", "ALMENDRA" -> new LecheAlmendra(producto);
            case "QUESO", "QUESO_EXTRA" -> new Queso(producto);
            case "CREMA", "CREMA_BATIDA" -> new Crema(producto);
            default -> throw new IllegalArgumentException("Tipo de adición no reconocido en Bragadan: " + tipoAdicion);
        };
    }

    //Personaliza un producto aplicando múltiples adiciones en cadena.
    public Producto personalizar(Producto producto, List<String> adiciones) {
        Objects.requireNonNull(producto, "El producto no puede ser nulo");
        if (adiciones == null || adiciones.isEmpty()) {
            return producto;
        }
        Producto resultado = producto;
        for (String adicion : adiciones) {
            resultado = personalizar(resultado, adicion);
        }
        return resultado;
    }

    //Modifica un ítem específico dentro de un pedido en estado RECIBIDO agregándole personalizaciones.
    public PedidoDTO personalizarItemEnPedido(Long pedidoId, int indiceItem, List<String> adiciones) {
        Pedido pedido = obtenerPedidoEntidad(pedidoId);
        if (indiceItem < 0 || indiceItem >= pedido.getItems().size()) {
            throw new IndexOutOfBoundsException("Índice de ítem fuera de rango: " + indiceItem);
        }

        // Reconstruimos la lista con el producto decorado
        Producto itemOriginal = pedido.getItems().get(indiceItem);
        Producto itemPersonalizado = personalizar(itemOriginal, adiciones);

        // Removemos y agregamos
        List<Producto> itemsActuales = new ArrayList<>(pedido.getItems());
        itemsActuales.set(indiceItem, itemPersonalizado);

        // Vaciamos e insertamos la nueva lista en el pedido
        while (!pedido.getItems().isEmpty()) {
            pedido.removerItem(0);
        }
        for (Producto item : itemsActuales) {
            pedido.agregarItem(item);
        }

        pedidoRepositorio.guardar(pedido);
        return PedidoMapper.toDTO(pedido);
    }

    //Ejecuta una transición de estado en el pedido a partir de un DTO de solicitud.
    public PedidoDTO cambiarEstado(Long pedidoId, CambioEstadoRequestDTO request) {
        Objects.requireNonNull(request, "La solicitud de cambio de estado no puede ser nula");
        if (request.nuevoEstado() == null || request.nuevoEstado().isBlank()) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo ni vacío");
        }
        EstadoPedido nuevoEstado = EstadoPedido.valueOf(request.nuevoEstado().trim().toUpperCase());
        return cambiarEstado(pedidoId, nuevoEstado);
    }

    //Sobrecarga directa con enum para cambiar el estado de un pedido.
    public PedidoDTO cambiarEstado(Long pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = obtenerPedidoEntidad(pedidoId);
        pedido.cambiarEstado(nuevoEstado);
        pedidoRepositorio.guardar(pedido);
        return PedidoMapper.toDTO(pedido);
    }

    /**
     * Consulta y retorna la lista de pedidos activos (en curso, no finalizados ni cancelados).
     */
    public List<PedidoDTO> pedidosActivos() {
        return PedidoMapper.toDTOList(pedidoRepositorio.listarActivos());
    }

    /**
     * Retorna todos los pedidos registrados en el sistema.
     */
    public List<PedidoDTO> listarTodos() {
        return PedidoMapper.toDTOList(pedidoRepositorio.listarTodos());
    }

    /**
     * Consulta los detalles de un pedido específico.
     */
    public PedidoDTO obtenerPedido(Long pedidoId) {
        return PedidoMapper.toDTO(obtenerPedidoEntidad(pedidoId));
    }

    /**
     * Obtiene el resumen o ticket detallado de un pedido.
     */
    public ResumenPedidoDTO obtenerResumen(Long pedidoId) {
        return PedidoMapper.toResumenDTO(obtenerPedidoEntidad(pedidoId));
    }

    /**
     * Acceso a la entidad de dominio interna (restringido al paquete de servicio/pruebas de integración).
     */
    public Pedido obtenerPedidoEntidad(Long pedidoId) {
        return pedidoRepositorio.buscarPorId(pedidoId)
                .orElseThrow(() -> new PedidoNoEncontradoException(pedidoId));
    }
}
